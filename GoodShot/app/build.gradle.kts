plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.ijonsabae.goodshot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ijonsabae.goodshot"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))
    // CameraX 세팅
    // CameraX core library using the camera2 implementation
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation (libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    implementation (libs.androidx.camera.video)
    // If you want to additionally use the CameraX View class
    implementation (libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation (libs.androidx.camera.mlkit.vision)
    // If you want to additionally use the CameraX Extensions library
    implementation (libs.androidx.camera.extensions)

    // Hilt 세팅
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit 관련 의존
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //Room 의존성 추가
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)

    // 코루틴
    implementation(libs.kotlinx.coroutines.android)

    // 글라이드
    implementation(libs.glide)

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
}