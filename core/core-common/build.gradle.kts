plugins {
    alias(libs.plugins.velvet.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.subenoeva.velvet.core.common"

    buildFeatures { compose = true }
}

dependencies {
    api(libs.lifecycle.viewmodel.compose)
    api(libs.coroutines.android)
    api(libs.kotlinx.serialization.json)

    implementation(libs.compose.runtime)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.compose.ui)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    compileOnly(libs.javax.inject)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
