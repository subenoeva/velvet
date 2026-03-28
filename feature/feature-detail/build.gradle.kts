plugins {
    alias(libs.plugins.velvet.android.feature)
}

android {
    namespace = "com.subenoeva.velvet.feature.detail"
}

dependencies {
    implementation(project(":core:core-network"))
    implementation(project(":core:core-database"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
}
