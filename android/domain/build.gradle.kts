plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    kotlin("plugin.serialization") version "1.9.22"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {
    //serialization
    implementation(libs.kotlinx.serialization.json)
    // Kotlin Pure Coroutine
    implementation (libs.kotlinx.coroutines.core)
    // Without Android Dependency Paging 3
    implementation(libs.androidx.paging.common)
}