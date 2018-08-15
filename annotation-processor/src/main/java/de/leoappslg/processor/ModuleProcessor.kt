package de.leoappslg.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import de.leoappslg.annotation.Module
import de.leoappslg.annotation.Modules
import de.leoappslg.exception.AuthenticationModuleNotFoundException
import de.leoappslg.exception.IllegalModuleNameException
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ModuleProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("de.leoappslg.annotation.Module", "de.leoappslg.annotation.Modules")
class ModuleProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val moduleListingFile = "identified_modules.modules"
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

        val authentication = ClassName("de.leoappslg.core.modules", "Authentication")
        val feature = ClassName("de.leoappslg.core.modules", "Feature")
        val featureList = ClassName("kotlin.collections", "List")
                .parameterizedBy(feature)

        val loader = FileSpec.builder(targetPackage, className)
                .addType(
                        TypeSpec.classBuilder(className)
                                .addType(
                                        TypeSpec.companionObjectBuilder()
                                                .addProperty(
                                                        PropertySpec.varBuilder("features", featureList)
                                                                .addModifiers(KModifier.PRIVATE, KModifier.LATEINIT)
                                                                .build()
                                                )
                                                .addProperty(
                                                        PropertySpec.varBuilder("authentication", authentication)
                                                                .addModifiers(KModifier.PRIVATE, KModifier.LATEINIT)
                                                                .build()
                                                )
                                                .addFunction(
                                                        FunSpec.builder("getFeatures")
                                                                .returns(featureList)
                                                                .beginControlFlow("if (!::features.isInitialized)")
                                                                .addStatement("val listing = mutableListOf<%T>()", feature)
                                                                .addStatement("val names = mutableListOf($moduleString)")
                                                                .beginControlFlow("for (cur in names)") //begin for loop
                                                                .addStatement("val feat = %T.forName(cur).newInstance() as %T", Class::class, feature)
                                                                .addStatement("listing.add(feat)")
                                                                .endControlFlow() //end for loop
                                                                .addStatement("features = listing")
                                                                .endControlFlow()
                                                                .addStatement("return features")
                                                                .build()
                                                )
                                                .addFunction(
                                                        FunSpec.builder("getAuthenticationModule")
                                                                .returns(authentication)
                                                                .beginControlFlow("if (!::authentication.isInitialized)")
                                                                .addStatement("authentication = %T.forName(\"$authString\").newInstance() as %T", Class::class, authentication)
                                                                .endControlFlow()
                                                                .addStatement("return authentication")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()

                )
                .build()

        clearModuleFile()
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
        val writer = FileWriter(file, true)

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
        val file = File(moduleListingFile)
        if (!file.exists())
            file.createNewFile()

        return file
    }

    private fun clearModuleFile() {
        getModuleFile().delete()
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