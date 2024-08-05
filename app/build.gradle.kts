import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlinx.serialization)
    id("com.google.dagger.hilt.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}
val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.ucne.cinetix"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ucne.cinetix"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug{
            buildConfigField("String", "API_KEY", properties.getProperty("API_KEY") )
        }
        release {
            buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom)) //ultima version del bom aqui: https://developer.android.com/jetpack/compose/bom
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //navegacion
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json) //nuevo para navigation

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.runtime.livedata)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    //  optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    //Hilt Dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    //retroFit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //lottie
    implementation("com.airbnb.android:lottie-compose:5.0.3")

    //SerializableName
    implementation("com.google.code.gson:gson:2.10.1")

    //Pagination
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")

    //RoomPaging
    implementation("androidx.room:room-paging:2.5.0-alpha01")

    //coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.github.skydoves:landscapist-coil:1.4.9")

    //LazyItem
    implementation("androidx.compose.foundation:foundation:1.1.1")
    implementation("androidx.compose.foundation:foundation-layout:1.5.1")

    //constraintLayout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.24.5-alpha")

    // RatingBar
    implementation("com.github.a914-gowtham:compose-ratingbar:1.2.3")

    //material icons
    implementation("androidx.compose.material:material-icons-extended:1.1.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}