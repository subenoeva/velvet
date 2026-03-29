// Top-level build file. Module-specific config lives in convention plugins (build-logic).

tasks.register("testAll") {
    description = "Runs unit tests for all modules"
    group = "verification"
    dependsOn(subprojects.flatMap { sub -> sub.tasks.matching { it.name == "test" } })
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
