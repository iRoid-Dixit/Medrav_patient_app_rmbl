import org.gradle.kotlin.dsl.android
import org.gradle.kotlin.dsl.kotlin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
plugins {
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}
android {
    namespace = "com.griotlegacy.mobile.app"
    compileSdk = 35
    signingConfigs {
        create("release") {
            storeFile =
                file("/Users/imac/Documents/dixit/Projects/legacy_cache_app/Legacy_Cache-Android/Legacy_Cache.jks")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }
    defaultConfig {
        applicationId = "com.griotlegacy.mobile.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 9
        versionName = "1.0.9"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        setProperty("archivesBaseName", "LegacyCache_" + formatDateWithOrdinal(Date()))
        // setProperty("archivesBaseName", "$applicationId-ver$versionName.ver_code-$versionCode")
        signingConfig = signingConfigs.getByName("release")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            buildConfigField("boolean", "EnableAnim", "true")
            buildConfigField("String", "BASE_URL", "\"https://dev.iroidsolutions.com:4009/api/v1/\"") //please put your respective base url
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
            buildConfigField("boolean", "EnableAnim", "true")
            buildConfigField("String", "BASE_URL", "\"https://dev.iroidsolutions.com:4009/api/v1/\"") //please put your respective base url
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
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get().toString()
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
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    //Android
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.datastorePrefs)
    implementation(libs.androidx.activity)
    // Inject - Dagger hilt
    kapt(libs.google.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.google.hilt.library)
//    implementation(libs.androidx.hilt.work)

    //compose
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.windowsize)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.google.gson)

    // Android Architecture Components
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.runtime)

    //firebase
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.messaging.ktx)

    //country code
    implementation(libs.komposecountrycodepicker)

    // Logging
    implementation(libs.kermit)

// image load
    implementation(libs.coil.kt.coil.compose)

    //coroutine
    implementation(libs.kotlin.coroutines.android)

    //System UI
    implementation(libs.accompanist.systemuicontroller)

    //toast messages
    implementation(libs.toasty)

    //paging-3
    implementation(libs.paging.compose)

    //calendar
    implementation(libs.threetenabp)

    //in-app subscription
    implementation(libs.billing.ktx)
    implementation(libs.revenuecat.purchases)

    //facebook login
    implementation(libs.facebook.android.sdk)

    //Youtube video
    implementation(libs.youtubeplayer.compose.android)

    implementation(libs.androidyoutubeplayer.core)
    // custom toast
    implementation (libs.cookiebar2)

    implementation (libs.accompanist.pager)

    // pull to refresh
    implementation (libs.accompanist.swiperefresh)

    //firebase crashlytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)


    // image swap

    //   Socket
    implementation(libs.socket.io.client)




}