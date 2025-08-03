import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "appdev.studybuddy"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "appdev.studybuddy"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                load(file.inputStream())
            }
        }

        val apiKey = localProperties.getProperty("DOG_API_KEY") ?: ""
        val serverUrl = localProperties.getProperty("SERVER_URL") ?: ""

        buildConfigField("String", "DOG_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }

    kotlin {
        sourceSets.all {
            languageSettings.apply {
                languageVersion = "1.9"
                apiVersion = "1.9"
            }
        }
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
    implementation(libs.ktor.client.core.v235)
    implementation(libs.ktor.client.cio.v235)
    implementation(libs.ktor.client.content.negotiation.v235)
    implementation(libs.ktor.serialization.kotlinx.json.v235)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json.v173)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.androidx.hilt.navigation.compose)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}