plugins {
    id("com.android.application")
}

android {
    namespace = "com.nk2sp.ifra"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.nk2sp.ifra"
        minSdk = 30
        targetSdk = 33
        versionCode = 2
        versionName = "1.1"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation (files("libs/activation.jar"))
    implementation (files("libs/additionnal.jar"))
    implementation (files("libs/mail.jar"))
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.android.volley:volley:1.2.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("androidx.camera:camera-view:1.2.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}