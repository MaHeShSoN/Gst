plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    id("kotlin-kapt")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.goldinvoice0.billingsoftware"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.goldinvoice0.billingsoftware"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.2")
    implementation("com.github.Pfuster12:LiveChart:1.3.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    //Pdf
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    //LiveData and ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    implementation ("com.google.code.gson:gson:2.11.0")


    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp ("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //Color Picker
    implementation(files("C:/Users/mahesh/OneDrive/Desktop/Downloads/colorpicker-0.0.15.aar"))

    //Signature
    implementation("com.github.gcacace:signature-pad:1.3.1")

    //Qr Code
    implementation("com.google.zxing:core:3.4.1")

    //For Charts
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //For tab View
    implementation ("androidx.viewpager2:viewpager2:1.1.0")

    //For Image View
    implementation("com.github.bumptech.glide:glide:4.16.0")


    implementation("androidx.recyclerview:recyclerview:1.3.2") // Or latest!

}