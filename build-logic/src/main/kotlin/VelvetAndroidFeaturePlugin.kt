import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class VelvetAndroidFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("velvet.android.library")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")
        }

        extensions.configure<LibraryExtension> {
            buildFeatures { compose = true }
        }

        dependencies {
            "implementation"(project(":core:core-ui"))
            "implementation"(project(":core:core-domain"))
            "implementation"(project(":core:core-common"))

            "implementation"(libs.findLibrary("hilt-android").get())
            "ksp"(libs.findLibrary("hilt-compiler").get())
            "implementation"(libs.findLibrary("hilt-navigation-compose").get())
            "implementation"(libs.findLibrary("navigation3-runtime").get())
            "implementation"(libs.findLibrary("navigation3-ui").get())
            "implementation"(libs.findLibrary("lifecycle-viewmodel-compose").get())
            "implementation"(libs.findLibrary("lifecycle-runtime-compose").get())
            "implementation"(libs.findLibrary("coroutines-android").get())
        }
    }
}
