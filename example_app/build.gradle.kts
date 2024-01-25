plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
}

android {
    namespace = "com.arnyminerz.markdowntext.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.arnyminerz.markdowntext.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime)

    // Jetpack Compose core
    implementation(libs.androidx.activityCompose)
    implementation(libs.compose.material3.core)
    implementation(libs.compose.ui.base)
    implementation(libs.compose.ui.toolingPreview)

    // Jetpack Compose - Markdown Text
    implementation(project(":markdown-text"))

    debugImplementation(libs.compose.ui.tooling)
}