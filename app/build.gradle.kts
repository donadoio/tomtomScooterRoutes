plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val tomtomApiKey: String by project

android {
    namespace = "io.donado.sfroutes"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.donado.sfroutes"
        minSdk = 24
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
        configureEach {
            buildConfigField("String", "TOMTOM_API_KEY", "\"$tomtomApiKey\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true;
        buildConfig = true;
    }
    packaging {
        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    val version = "0.49.0"
    implementation("com.tomtom.sdk.location:provider-android:$version")
    implementation("com.tomtom.sdk.location:provider-map-matched:$version")
    implementation("com.tomtom.sdk.location:provider-simulation:$version")
    implementation("com.tomtom.sdk.maps:map-display:$version")
    implementation("com.tomtom.sdk.datamanagement:navigation-tile-store:$version")
    implementation("com.tomtom.sdk.navigation:navigation-online:$version")
    implementation("com.tomtom.sdk.navigation:route-replanner-online:$version")
    implementation("com.tomtom.sdk.navigation:ui:$version")
    implementation("com.tomtom.sdk.routing:route-planner-online:$version")
}