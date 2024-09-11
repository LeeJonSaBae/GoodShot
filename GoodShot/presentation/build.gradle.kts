plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.ijonsabae.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

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
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation ("androidx.window:window:1.3.0")

    // navigation
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    implementation (libs.androidx.core.splashscreen)

    // Timeline-View
    implementation (libs.vipulasri.timelineview)
    // Mp Android Chart
    implementation (libs.mpandroidchart)
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
    // 글라이드 transformation (blur 효과 등)
    implementation(libs.glide.transformations)

    // Hilt 세팅
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

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
}