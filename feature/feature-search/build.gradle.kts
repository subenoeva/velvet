plugins {
    alias(libs.plugins.velvet.android.feature)
}

android {
    namespace = "com.subenoeva.velvet.feature.search"
}

dependencies {
    implementation(project(":core:core-network"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.paging.compose)
}
