plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Required for Firebase
}

android {
    namespace = "com.cocomelon.smartguard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cocomelon.smartguard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add Base URL as build config fields for different build types
        buildConfigField("String", "BASE_URL", "\"http://127.0.0.1:5001/smartguard-2352a/us-central1/updateFcmToken\"")
    }

    buildTypes {
        debug {
            // Local development environment URL
            buildConfigField("String", "BASE_URL", "\"http://127.0.0.1:5001/smartguard-2352a/us-central1/updateFcmToken\"")
        }

        release {
            // Production Firebase Function URL
            buildConfigField("String", "BASE_URL", "\"https://us-central1-smartguard-2352a.cloudfunctions.net/updateFcmToken\"")
            isMinifyEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.cardview:cardview:1.0.0")
    // Core Android dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase Core and Auth
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-messaging") // Correctly specified FCM

    // Firebase Analytics (Optional, but useful)
    implementation("com.google.firebase:firebase-analytics")

    // Google Sign-In dependency
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation(libs.cardview)

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // Retrofit for API requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for network logging (optional but recommended)
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
}
