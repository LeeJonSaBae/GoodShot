import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "1.9.22"
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")

}
private val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val S3_URL: String = properties.getProperty("S3_URL")
val VIDEO: String = properties.getProperty("VIDEO")
val IMAGE: String = properties.getProperty("IMAGE")
val THUMBNAIL: String = properties.getProperty("THUMBNAIL")

android {
    namespace = "com.ijonsabae.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 29
        buildConfigField("String", "S3_URL", S3_URL)
        buildConfigField("String", "VIDEO", VIDEO)
        buildConfigField("String", "THUMBNAIL", THUMBNAIL)
        buildConfigField("String", "IMAGE", IMAGE)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(":domain"))

//    // paging3
    implementation(libs.androidx.paging.runtime)

    implementation(libs.androidx.window)

    //serialization
    implementation(libs.kotlinx.serialization.json)

    // navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.core.splashscreen)

    // Timeline-View
    implementation(libs.vipulasri.timelineview)
    // Mp Android Chart
    implementation(libs.mpandroidchart)
    // CameraX 세팅
    // CameraX core library using the camera2 implementation
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    implementation(libs.androidx.camera.video)
    // If you want to additionally use the CameraX View class
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation(libs.androidx.camera.mlkit.vision)
    // If you want to additionally use the CameraX Extensions library
    implementation(libs.androidx.camera.extensions)
    // Retrofit 관련 의존
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)


    // 코루틴
    implementation(libs.kotlinx.coroutines.android)

    // 글라이드
    implementation(libs.glide)
    // 글라이드 transformation (blur 효과 등)
    implementation(libs.glide.transformations)

    // Hilt 세팅
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // retrofit
    implementation(libs.retrofit)

    // Non Error 뭐시기 에러 뜸 KSP랑 Hilt랑 뭐가 안 맞는 듯
//    ksp(libs.hilt.compiler)

    // RecyclerView
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // AI
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.support)
    implementation (libs.tensorflow.tensorflow.lite.task.vision)

    // Media3 ExoPlayer
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)

    // recyclerview snapping
    implementation(libs.gravitysnaphelper)

    // gif process
    implementation(libs.gifencoder)

    // image cropper
    implementation(libs.android.image.cropper)

    // tooltip
    implementation(libs.balloon)

    // viewpager indicator
    implementation(libs.dotsindicator)

    // youtube parsing
    implementation (libs.jsoup)

}