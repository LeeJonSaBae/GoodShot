import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "1.9.22"
}

private val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val SERVER_IP: String = properties.getProperty("SERVER_IP")
val YOUTUBE_IP: String = properties.getProperty("YOUTUBE_IP")
val YOUTUBE_KEY: String = properties.getProperty("YOUTUBE_KEY")

android {
    namespace = "com.ijonsabae.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "SERVER_IP", SERVER_IP)
        buildConfigField("String", "YOUTUBE_IP", YOUTUBE_IP)
        buildConfigField("String", "YOUTUBE_KEY", YOUTUBE_KEY)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(project(":domain"))

    // paging3
    implementation(libs.androidx.paging.runtime)

    //serialization
    implementation(libs.kotlinx.serialization.json)

    // Hilt 세팅
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
//    ksp(libs.hilt.compiler)

    // Retrofit 관련 의존
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // datastore 사용
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}