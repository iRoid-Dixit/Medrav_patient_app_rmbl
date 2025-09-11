import org.gradle.kotlin.dsl.android
import org.gradle.kotlin.dsl.hilt
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //id ("kotlin-kapt")
    //id("com.google.dagger.hilt.android")
    // kotlin("kapt")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
android {
    namespace = "com.medrevpatient.mobile.app"
    compileSdk = 35
    signingConfigs {
        create("release") {
            storeFile =
                file("/Users/imac/Documents/dixit/Projects/medrev_patient_app/Legacy_Cache-Android/MedrevPatient.jks")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }
    defaultConfig {
        applicationId = "com.medrevpatient.mobile.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 9
        versionName = "1.0.9"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        setProperty("archivesBaseName", "medrevpatient" + formatDateWithOrdinal(Date()))
        // setProperty("archivesBaseName", "$applicationId-ver$versionName.ver_code-$versionCode")
        signingConfig = signingConfigs.getByName("release")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            buildConfigField("boolean", "EnableAnim", "true")
            buildConfigField("String", "BASE_URL", "\"http://157.245.106.111:8080/\"") //please put your respective base url
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
            buildConfigField("boolean", "EnableAnim", "true")
            buildConfigField("String", "BASE_URL", "\"http://157.245.106.111:8080/\"") //please put your respective base url
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
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
fun formatDateWithOrdinal(date: Date): String {
    val daySuffix = getDaySuffix(date)
    val formattedDate = SimpleDateFormat("d'$daySuffix'MMMMyyyy").apply {
        timeZone = TimeZone.getDefault()
    }.format(date)
    return formattedDate
}
fun getDaySuffix(date: Date): String {
    val day = SimpleDateFormat("d").apply {
        timeZone = TimeZone.getDefault()
    }.format(date)

    val dayInt = day.toInt()
    var suffix = "th"

    if (dayInt >= 11 && dayInt <= 13) return suffix

    val suffixMap = arrayOf(null, "st", "nd", "rd") +
            (4..20).map { "th" } + arrayOf("st", "nd", "rd") +
            (24..30).map { "th" } + arrayOf("st")

    suffix = suffixMap[dayInt % 100].toString()
    return suffix
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.core)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    // navigation data send activity and compose route
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.appcompat.v170)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.places)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.firebase.messaging.ktx)
    implementation (libs.ui)
    implementation (libs.androidx.foundation)
    implementation (libs.androidx.material)

    implementation (libs.material3)
    implementation(libs.compose.material3.windowsize)
    implementation (libs.androidx.material3.adaptive.navigation.suite)

    //firebase crashlytics
    implementation(platform(libs.firebase.bom))
    implementation (platform(libs.androidx.compose.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    //Android
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)

    // Inject - Dagger hilt
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.hilt.android)

    //compose
    implementation(libs.material3)
    implementation(libs.compose.material3.windowsize)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    // Android Architecture Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Logging
    implementation(libs.kermit)


    //coroutine
    implementation(libs.kotlinx.coroutines.android)

    //  Socket
    implementation(libs.socket.io.client)

    //paging 3
    implementation ( libs.androidx.paging.runtime.ktx)
    implementation (libs.androidx.paging.compose)

    // Icons
    implementation (libs.androidx.material.icons.extended)
    // image load
    implementation(libs.coil.compose)
    // google places api



    // status bar colors changes
    implementation (libs.accompanist.systemuicontroller)

    // pager
    implementation (libs.accompanist.pager)

    // custom toast
    implementation (libs.cookiebar2)

    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
    // permission issue
    implementation (libs.androidx.activity.compose.v193)
    implementation (libs.accompanist.permissions)
    // map load
    //implementation(libs.maps.compose)
    implementation (libs.play.services.location)
    // refresh layout
    implementation (libs.accompanist.swiperefresh)

    // google login
    implementation (libs.play.services.auth)

    implementation(libs.firebase.auth.ktx)

    implementation(libs.facebook.android.sdk)

    //chart
    implementation (libs.compose.charts)
    // Svg image load
    implementation(libs.coil.svg)

}