plugins {
    alias(libs.plugins.velvet.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.subenoeva.velvet.core.ui"
    buildFeatures { compose = true }
}

dependencies {
    val bom = platform(libs.compose.bom)
    api(bom)
    api(libs.compose.ui)
    api(libs.compose.material3)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.runtime)
    api(libs.compose.material.icons.core)
    debugImplementation(libs.compose.ui.tooling)

    api(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
