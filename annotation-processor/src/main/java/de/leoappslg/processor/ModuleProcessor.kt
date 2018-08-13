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

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ModuleProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ModuleProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {

        val registeredModules = mutableListOf<Triple<String, String, String>>()
        val authenticationModules = mutableListOf<Triple<String, String, String>>()

        env.getElementsAnnotatedWith(Module::class.java).forEach { element ->

            println("TEST")

            if (element.kind != ElementKind.CLASS)
                return@forEach

            val name = element.getAnnotation(Module::class.java).name
            val relevantList = if (element.getAnnotation(Module::class.java).authentication) authenticationModules else registeredModules

            relevantList.add(
                    Triple(name, element.simpleName.toString(), processingEnv.elementUtils.getPackageOf(element).toString())
            )
        }

        env.getElementsAnnotatedWith(Modules::class.java).forEach { element ->

            if (element.kind != ElementKind.CLASS || element.kind != ElementKind.METHOD)
                return@forEach

            val classPackage = processingEnv.elementUtils.getPackageOf(element).toString()
            val addedFeatures = element.getAnnotation(Modules::class.java).features
            val authentication = element.getAnnotation(Modules::class.java).authentication

            if (!authenticationModules.contains(authentication))
                throw AuthenticationModuleNotFoundException("You need to register a valid Authentication module")

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

    private fun List<Triple<String, *, *>>.contains(first: String): Boolean {
        var contains = false
        forEach {
            if (it.first == first)
                contains = true
        }
        return contains
    }

    private fun generateModuleListing(modules: List<Triple<*, *, *>>, targetPackage: String, auth: Triple<*, *, *>) {

        val className = "ModuleLoader"

        val authentication = ClassName("de.leoappslg.core.modules", "Authentication")
        val feature = ClassName("de.leoappslg.core.modules", "Feature")
        val featureList = ClassName("kotlin.collections", "MutableList")
                .parameterizedBy(feature)

        val moduleString = getClassNameListing(modules).joinToString(prefix = "\"", postfix = "\"", separator = "\", \"")
        val authString = "${auth.third}.${auth.second}"

        val loader = FileSpec.builder(targetPackage, className)
                .addType(
                        TypeSpec.classBuilder(className)
                                .addProperty(
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
                                                .build())
                                .addProperty(
                                        PropertySpec.builder("authentication", authentication)
                                                .delegate(CodeBlock.builder()
                                                        .beginControlFlow("lazy") //begin lazy init
                                                        .addStatement("val kClass = %T.forName($authString).kotlin", Class::class)
                                                        .addStatement("val ctor = kClass.primaryConstructor")
                                                        .addStatement("list.add(ctor!!.call())") //ctor cant be null
                                                        .endControlFlow() //end lazy init
                                                        .build())
                                                .build())
                                .build()
                ).build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        val file = File(kaptKotlinGeneratedDir)
        file.mkdir()
        loader.writeTo(File(kaptKotlinGeneratedDir)) //, "$className.kt"

    }

    private fun getClassNameListing(modules: List<Triple<*, *, *>>): List<String> {
        val returnList = mutableListOf<String>()
        modules.forEach {
            returnList.add("${it.third}.${it.second}")
        }
        return returnList
    }

}