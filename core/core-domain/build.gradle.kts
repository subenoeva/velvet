plugins {
    alias(libs.plugins.velvet.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.subenoeva.velvet.core.domain"
}

dependencies {
    implementation(project(":core:core-common"))
    api(libs.coroutines.core)
    api(libs.paging.common)
    api(libs.kotlinx.serialization.json)
    implementation(libs.javax.inject)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
