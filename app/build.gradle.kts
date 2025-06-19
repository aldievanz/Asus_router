plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.product_bottomnav"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.product_bottomnav"
        minSdk = 27
        targetSdk = 34
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

    // UI Components and Navigation
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit and Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.15.1") // Keep only one version of Glide
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Gson for JSON Parsing
    implementation("com.google.code.gson:gson:2.8.8")

    // Circular Image View
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // RecyclerView for Lists
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")
    implementation("commons-io:commons-io:2.11.0")
}
