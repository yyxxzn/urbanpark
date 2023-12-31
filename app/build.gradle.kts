plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.googlemapsandplaces"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.googlemapsandplaces"
        minSdk = 29
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Manually added for locations and map
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.google.android.gms:play-services-wallet:19.2.0")
//    implementation("com.google.maps:google-maps-services:")

    // For using SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo)
//    implementation("com.google.maps.android:android-maps-utils:0.5+")

    // Manually added -> Google Pay APIs
    // implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation("com.google.android.gms:play-services-pay:16.1.0")
    //implementation("com.google.android.gms:play-services-wallet:19.2.0")

    // Manually added -> Google Auth API
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.google.firebase:firebase-database:20.2.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}