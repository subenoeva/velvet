plugins {
    alias(libs.plugins.velvet.android.library)
}

android {
    namespace = "com.subenoeva.velvet.core.common"
}

dependencies {
    api(libs.lifecycle.viewmodel.compose)
    api(libs.coroutines.android)
    api(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
