plugins {
    alias(libs.plugins.velvet.android.feature)
}

android {
    namespace = "com.subenoeva.velvet.feature.search"
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.paging.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
}
