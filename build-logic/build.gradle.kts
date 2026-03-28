plugins {
    `kotlin-dsl`
}

group = "com.subenoeva.velvet.buildlogic"

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("velvetAndroidApplication") {
            id = "velvet.android.application"
            implementationClass = "VelvetAndroidApplicationPlugin"
        }
        register("velvetAndroidLibrary") {
            id = "velvet.android.library"
            implementationClass = "VelvetAndroidLibraryPlugin"
        }
        register("velvetAndroidFeature") {
            id = "velvet.android.feature"
            implementationClass = "VelvetAndroidFeaturePlugin"
        }
        register("velvetKotlinLibrary") {
            id = "velvet.kotlin.library"
            implementationClass = "VelvetKotlinLibraryPlugin"
        }
    }
}
