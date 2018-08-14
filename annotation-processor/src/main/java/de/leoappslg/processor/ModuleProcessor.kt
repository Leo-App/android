package de.leoappslg.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import de.leoappslg.annotation.Module
import de.leoappslg.annotation.Modules
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ModuleProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("de.leoappslg.annotation.Modules", "de.leoappslg.annotation.Module")
class ModuleProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {

        val registeredModules = mutableListOf<Triple<String, String, String>>()
        val authenticationModules = mutableListOf<Triple<String, String, String>>()

        env.getElementsAnnotatedWith(Module::class.java).forEach { element ->

            if (element.kind != ElementKind.CLASS)
                return@forEach

            val name = element.getAnnotation(Module::class.java).name
            val relevantList = if (element.getAnnotation(Module::class.java).authentication) authenticationModules else registeredModules

            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: cur: ${processingEnv.elementUtils.getPackageOf(element)}.${element.simpleName}")

            if (element.getAnnotation(Module::class.java).authentication) {
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: AUTH: ${processingEnv.elementUtils.getPackageOf(element)}.${element.simpleName}")
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: AUTHNAME: $name")

            }
            relevantList.add(
                    Triple(name, element.simpleName.toString(), processingEnv.elementUtils.getPackageOf(element).toString())
            )
        }

        env.getElementsAnnotatedWith(Modules::class.java).forEach { element ->

            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: ANNOTATED")

            if (element.kind != ElementKind.CLASS && element.kind != ElementKind.METHOD)
                return@forEach

            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: IS CLASS OR METHOD")

            val classPackage = processingEnv.elementUtils.getPackageOf(element).toString()
            val addedFeatures = element.getAnnotation(Modules::class.java).features
            val authentication = element.getAnnotation(Modules::class.java).authentication

            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: $classPackage")
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: $authentication")

            if (!authenticationModules.containsFirst(authentication)) {
                //           throw AuthenticationModuleNotFoundException("You need to register a valid Authentication module")
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "No auth found")
                return@forEach
            }
            generateModuleListing(registeredModules.filter { entry -> addedFeatures.contains(entry.first) },
                    classPackage, authenticationModules.getItemWithFirst(authentication)!!)

        }


        return true
    }

    private fun List<Triple<String, *, *>>.getItemWithFirst(first: String): Triple<*, *, *>? {
        forEach {
            if (it.first == first)
                return it
        }
        return null
    }

    private fun List<Triple<String, *, *>>.containsFirst(first: String): Boolean {
        forEach {
            if (it.first == first)
                return true
        }
        return false
    }

    private fun generateModuleListing(modules: List<Triple<*, *, *>>, targetPackage: String, auth: Triple<*, *, *>) {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "TEST")

        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "debug: ${modules.size} - $targetPackage, ${auth.second}.${auth.third}")

        val className = "ModuleLoader"

        val authentication = ClassName("de.leoappslg.core.modules", "Authentication")
        val feature = ClassName("de.leoappslg.core.modules", "Feature")
        val featureList = ClassName("kotlin.collections", "List")
                .parameterizedBy(feature)

        val moduleString = getClassNameListing(modules).joinToString(prefix = "\"", postfix = "\"", separator = "\", \"")
        val authString = "${auth.second}.${auth.third}"

        val loader = FileSpec.builder(targetPackage, className)
                .addType(
                        TypeSpec.classBuilder(className)
                            /*    .addProperty(
                                        PropertySpec.builder("modules", featureList)
                                                .delegate(CodeBlock.builder()
                                                        .beginControlFlow("lazy") //begin lazy init
                                                        .addStatement("val list = mutableListOf<%T>()", feature)
                                                        .addStatement("val names = mutableListOf($moduleString)")
                                                        .beginControlFlow("for (cur in names)") //begin for loop
                                                        .addStatement("val kClass = %T.forName(cur).kotlin", Class::class)
                                                        .addStatement("val ctor = kClass.primaryConstructor")
                                                        .beginControlFlow("if (ctor != null)") //begin if statement
                                                        .addStatement("list.add(ctor.call())")
                                                        .endControlFlow() //end if statement
                                                        .endControlFlow() //end for loop
                                                        .addStatement("modules")
                                                        .endControlFlow() //end lazy init
                                                        .build())
                                                .build()) */
                          /*      .addProperty(
                                        PropertySpec.builder("authentication", authentication)
                                                .delegate(CodeBlock.builder()
                                                        .beginControlFlow("lazy") //begin lazy init
                                                        .addStatement("val kClass = %T.forName($authString).kotlin", Class::class)
                                                        .addStatement("val ctor = kClass.primaryConstructor")
                                                        .addStatement("ctor!!.call()") //ctor cant be null
                                                        .endControlFlow() //end lazy init
                                                        .build())
                                                .build())
                                .build() */
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

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        val file = File(kaptKotlinGeneratedDir)
        file.mkdir()
        loader.writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun getClassNameListing(modules: List<Triple<*, *, *>>): List<String> {
        val returnList = mutableListOf<String>()
        modules.forEach {
            returnList.add("${it.second}.${it.third}")
        }
        return returnList
    }

}