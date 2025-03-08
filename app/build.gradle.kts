plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.harsh.streamingapp"
    compileSdk = 35

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
        }
    }

    defaultConfig {
        applicationId = "com.harsh.streamingapp"
        minSdk = 23
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.analytics) // connect your project to firebase and enable FCM notification
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.snevatechnologies:EasyPrefs:1.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("io.socket:socket.io-client:2.1.0")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
//    ksp("androidx.room:room-compiler-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.30.1") // also this one library
    implementation("jp.wasabeef:glide-transformations:4.3.0") // this library is used for blur effect, we will replace it later
}