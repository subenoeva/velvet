import java.util.Properties

plugins {
    alias(libs.plugins.velvet.android.application)
}

android {
    namespace = "com.subenoeva.velvet"

    defaultConfig {
        applicationId = "com.subenoeva.velvet"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-ui"))
    implementation(project(":feature:feature-home"))
    implementation(project(":feature:feature-search"))
    implementation(project(":feature:feature-detail"))
    implementation(project(":feature:feature-favorites"))
    implementation(project(":feature:feature-settings"))

    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
