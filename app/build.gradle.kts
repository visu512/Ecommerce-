import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services) // Firebase plugin
    id("kotlin-kapt")
}

android {
    namespace = "com.example.ecomapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ecomapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
}

dependencies {
    // Core AndroidX Libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    // Material Design
    implementation("com.google.android.material:material:1.10.0") // Keep only the latest version

    // Firebase BoM (Bill of Materials) - Ensures compatible versions
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Firebase Services
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Glide for Image Loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.play.services.location)
    implementation(libs.androidx.activity)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // picasso
    implementation("com.squareup.picasso:picasso:2.8")

    // Smooth Bottom Navigation Bar
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")

    // Retrofit for API Calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")

    // Testing Libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")

    // Material Search Bar
    implementation("com.github.mancj:MaterialSearchBar:0.8.5")

    // Circular ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-basement:18.2.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Use kapt for Kotlin
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-paging:2.6.1") // Optional: Paging support

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Volley for API Requests
    implementation("com.android.volley:volley:1.2.1")

    // Country Code Picker
    implementation("com.hbb20:ccp:2.5.1")

    // image slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")

    // shimmer effect
//    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    // material3
    implementation("com.google.android.material:material:1.9.0")

// Razor pay get way
    implementation("com.razorpay:checkout:1.6.40")

    // material3
//    implementation("com.google.android.material:material:1.11.0") // Latest Material3


}
