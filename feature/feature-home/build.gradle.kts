plugins {
    alias(libs.plugins.velvet.android.feature)
}

android {
    namespace = "com.subenoeva.velvet.feature.home"
}

dependencies {
    implementation(project(":core:core-network"))
    implementation(platform(libs.compose.bom))
    implementation(libs.paging.compose)
    implementation(libs.compose.material3)
}
