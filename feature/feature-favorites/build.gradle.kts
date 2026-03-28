plugins {
    alias(libs.plugins.velvet.android.feature)
}

android {
    namespace = "com.subenoeva.velvet.feature.favorites"
}

dependencies {
    implementation(project(":core:core-database"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
}
