plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.cricketgame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cricketgame"
        minSdk = 31
        targetSdk = 35
        versionCode = 1

        // Embed commit SHA in version name for traceability.
        // CI overrides this via: ./gradlew -PversionNameSuffix=$(git rev-parse --short HEAD)
        val sha = project.findProperty("versionNameSuffix") ?: "local"
        versionName = "1.0.0-$sha"

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
    // AndroidX core — minimal for now
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
}
