plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.20"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    alias(libs.plugins.google.firebase.appdistribution)
    alias(libs.plugins.google.gms.google.services)
}

// Git 커밋 해시를 가져오는 함수
fun getGitHash(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
            .directory(project.rootDir)
            .start()
        process.inputStream.bufferedReader().readText().trim()
    } catch (e: Exception) {
        "unknown"
    }
}

// 빌드 시간을 가져오는 함수 (간단한 형식)
fun getBuildTime(): String {
    return System.currentTimeMillis().toString()
}

android {
    namespace = "com.august.spiritscribe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.august.spiritscribe"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        
        // Git 해시를 포함한 버전명 설정
        versionName = "0.0.1_${getGitHash()}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // App Distribution을 위한 디버그 빌드 설정
            isDebuggable = true
            // 디버그 빌드는 Git 해시 + -debug 접미사
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            // 릴리즈 빌드는 Git 해시만 사용
            versionNameSuffix = ""
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.espresso.core)
    implementation(libs.hilt.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.material.icons.extended)
    
    // Room dependencies
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.paging.common.android)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)
    
    // Hilt dependencies
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.app.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.accompanist.drawablepainter)

    implementation(libs.coil.compose.v320)
    implementation(libs.coil.network.okhttp.v320)

    implementation(libs.androidx.paging.runtime) // For non-Compose parts
    implementation(libs.androidx.paging.compose) // <-- THIS IS NEEDED
}

// Firebase App Distribution 설정
firebaseAppDistribution {
    // 테스터 그룹 설정 (이메일 주소들을 콤마로 구분)
    groups = "testers"
    
    // 동적 릴리즈 노트 (Git 해시와 빌드 시간 포함)
    releaseNotes = """
        🥃 SpiritScribe 앱 새로운 빌드
        
        📦 버전: ${android.defaultConfig.versionName}
        🔗 커밋: ${getGitHash()}
        🕒 빌드 시간: ${getBuildTime()}
        
        새로운 기능과 개선사항을 테스트해보세요!
        피드백은 Firebase Console을 통해 제공해주세요.
    """.trimIndent()
    
    // Firebase App Distribution에 업로드할 때 사용할 서비스 계정 키 파일 경로
    // (선택사항 - CLI에서 설정할 수도 있음)
    // serviceCredentialsFile = "path/to/service-account-key.json"
}