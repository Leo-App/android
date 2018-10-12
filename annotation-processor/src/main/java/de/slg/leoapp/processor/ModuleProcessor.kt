package de.slg.leoapp.processor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import de.slg.leoapp.annotation.Module
import de.slg.leoapp.annotation.Modules
import de.slg.leoapp.exception.AuthenticationModuleNotFoundException
import de.slg.leoapp.exception.IllegalModuleNameException
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ModuleProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("de.slg.leoapp.annotation.Module", "de.slg.leoapp.annotation.Modules")
class ModuleProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val MODULE_FILE_READ_INDICATOR = "###"
        const val MODULE_LISTING_FILE = "identified_modules.modules"
    }

    class IdentifiedModule(val identifier: String, val name: String, val appPackage: String, val authentication: Boolean) {
        override fun toString(): String {
            return "module:$identifier:$name:$appPackage:$authentication"
        }
    }

    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {

        env.getElementsAnnotatedWith(Module::class.java).forEach { element ->

            if (element.kind != ElementKind.CLASS)
                return@forEach

            val annotation = element.getAnnotation(Module::class.java)

            if (annotation.name.contains(regex = "[#:]".toRegex()))
                throw IllegalModuleNameException("${annotation.name} is not a valid module identifier")

            val curModule = IdentifiedModule(
                    annotation.name,
                    element.simpleName.toString(),
                    processingEnv.elementUtils.getPackageOf(element).toString(),
                    annotation.authentication
            )

            registerModule(curModule)
        }

        env.getElementsAnnotatedWith(Modules::class.java).forEach { element ->

            if (element.kind != ElementKind.CLASS && element.kind != ElementKind.METHOD)
                return@forEach

            val classPackage = processingEnv.elementUtils.getPackageOf(element).toString()
            val addedFeatures = element.getAnnotation(Modules::class.java).features
            val authentication = element.getAnnotation(Modules::class.java).authentication
            val registeredModules = getRegisteredModules()

            for (cur in registeredModules) {
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "registered ${cur.identifier}")
            }

            for (cur in addedFeatures) {
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "added $cur")
            }

            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "used authentication module = $authentication")

            val registeredAuthModule = getAuthenticationModule(getRegisteredModules(), authentication)
                    ?: throw AuthenticationModuleNotFoundException("You need to register a valid Authentication module")

            generateModuleListing(registeredModules.filter { addedFeatures.contains(it.identifier) }, classPackage, registeredAuthModule)

        }

        return true
    }

    private fun generateModuleListing(modules: List<IdentifiedModule>, targetPackage: String, authModule: IdentifiedModule) {
        val className = "ModuleLoader"

        val moduleString = getClassNameListing(modules).joinToString(prefix = "\"", postfix = "\"", separator = "\", \"")
        val authString = "${authModule.appPackage}.${authModule.name}"

        val authentication = ClassName("de.slg.leoapp.core.modules", "Authentication")
        val feature = ClassName("de.slg.leoapp.core.modules", "Feature")
        val featureList = ClassName("kotlin.collections", "List")
                .parameterizedBy(feature)

        val loader = FileSpec.builder(targetPackage, className)
                .addType(
                        TypeSpec.classBuilder(className)
                                .addType(
                                        TypeSpec.companionObjectBuilder()
                                                .addProperty(
                                                        PropertySpec.varBuilder("features", featureList.asNullable())
                                                                .addModifiers(KModifier.PRIVATE)
                                                                .initializer("null")
                                                                .build()
                                                )
                                                .addProperty(
                                                        PropertySpec.varBuilder("authentication", authentication.asNullable())
                                                                .addModifiers(KModifier.PRIVATE)
                                                                .initializer("null")
                                                                .build()
                                                )
                                                .addFunction(
                                                        FunSpec.builder("getFeatures")
                                                                .returns(featureList)
                                                                .beginControlFlow("if (features == null)")
                                                                .addStatement("val listing = mutableListOf<%T>()", feature)
                                                                .addStatement("val names = mutableListOf($moduleString)")
                                                                .beginControlFlow("for (cur in names)") //begin for loop
                                                                .addStatement("val feat = %T.forName(cur).newInstance() as %T", Class::class, feature)
                                                                .addStatement("listing.add(feat)")
                                                                .endControlFlow() //end for loop
                                                                .addStatement("features = listing")
                                                                .endControlFlow()
                                                                .addStatement("return features!!")
                                                                .build()
                                                )
                                                .addFunction(
                                                        FunSpec.builder("getAuthenticationModule")
                                                                .returns(authentication)
                                                                .beginControlFlow("if (authentication == null)")
                                                                .addStatement("authentication = %T.forName(\"$authString\").newInstance() as %T", Class::class, authentication)
                                                                .endControlFlow()
                                                                .addStatement("return authentication!!")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()

                )
                .build()

        markModuleFileAsRead()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        val file = File(kaptKotlinGeneratedDir)
        file.mkdir()
        loader.writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun getClassNameListing(modules: List<IdentifiedModule>): List<String> {
        val returnList = mutableListOf<String>()
        modules.forEach {
            returnList.add("${it.appPackage}.${it.name}")
        }
        return returnList
    }

    private fun registerModule(module: IdentifiedModule) {
        val file = getModuleFile()
        val read = isModuleFileRead(file)

        if (isAlreadyRegistered(module, file) && !read) return //Workaround for AndroidStudio Rebuild problems

        val writer = FileWriter(file, !read)
        writer.write(module.toString() + "::")
        writer.flush()
        writer.close()
    }

    private fun getRegisteredModules(): List<IdentifiedModule> {
        val file = getModuleFile()
        val reader = file.inputStream().bufferedReader()

        val content = reader.use(BufferedReader::readText)
        val modules = content.split("::")

        val moduleList = mutableListOf<IdentifiedModule>()

        modules.forEach {
            val moduleInfo = it.split(":")
            if (moduleInfo[0] != "module")
                return@forEach
            moduleList.add(IdentifiedModule(moduleInfo[1], moduleInfo[2], moduleInfo[3], moduleInfo[4].toBoolean()))
        }

        return moduleList
    }

    /**
     * Gibt die aktuelle Datei zurück, in der registrierte Module gespeichert werden.
     */
    private fun getModuleFile(): File {
        val file = File(MODULE_LISTING_FILE)
        if (!file.exists())
            file.createNewFile()

        return file
    }

    private fun markModuleFileAsRead() {
        val file = getModuleFile()
        val writer = FileWriter(file, true)
        writer.write(MODULE_FILE_READ_INDICATOR)
        writer.flush()
        writer.close()
    }

    private fun isModuleFileRead(file: File): Boolean {
        return file.contains(MODULE_FILE_READ_INDICATOR)
    }

    private fun isAlreadyRegistered(module: IdentifiedModule, moduleFile: File): Boolean {
        return moduleFile.contains("module:${module.identifier}")
    }

    private fun File.contains(s: String): Boolean {
        val reader = inputStream().bufferedReader()
        return reader.use(BufferedReader::readText).contains(s)
    }

    /**
     * Gibt ein registriertes Authentifizierungsmodul zurück. Wird der Parameter "byIdentifier" übergeben, wird ein bestimmtes Modul gesucht,
     * im Regelfall wird das erste registrierte Modul zurückgegeben. Ist keines verfügbar wird null zurückgegeben.
     */
    private fun getAuthenticationModule(registeredModules: List<IdentifiedModule>, byIdentifier: String? = null): IdentifiedModule? {
        registeredModules.forEach {
            if (byIdentifier != null) {
                if (it.identifier == byIdentifier)
                    return it
            } else {
                if (it.authentication)
                    return it
            }
        }
        return null
    }

}