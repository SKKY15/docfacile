import java.util.Properties

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

val googleApiKey = localProperties["GOOGLE_API_KEY"] as String?
    ?: throw GradleException("GOOGLE_API_KEY is missing in local.properties")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.startup_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.startup_app"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        manifestPlaceholders["GOOGLE_API_KEY"] = googleApiKey
        buildConfigField("String", "GOOGLE_API_KEY", "\"$googleApiKey\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            manifestPlaceholders["GOOGLE_API_KEY"] = googleApiKey
        }
        getByName("release") {
            manifestPlaceholders["GOOGLE_API_KEY"] = googleApiKey
        }
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
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.core.animation)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.android.maps.utils)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.gson)
    implementation(libs.circleimageview)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
