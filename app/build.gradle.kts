import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    alias(libs.plugins.ksp.module)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.plugin)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.medrevpatient.mobile.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.medrevpatient.mobile.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "medrevpatient" + formatDateWithOrdinal(Date()))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            buildConfigField("boolean", "EnableAnim", "true")
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://dev.iroidsolutions.com:3001/api/v1/\""
            ) //please put your respective base url
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = true
            buildConfigField("boolean", "EnableAnim", "true")
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://dev.iroidsolutions.com:3001/api/v1/\""
            ) //please put your respective base url
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
    implementation(libs.window.size)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //implementation(libs.androidx.paging.common.android)

    //Retrofit-Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalar)
    implementation(libs.logging.interceptor)
    implementation(libs.timber)

    // Android Architecture Components
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    //Android
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.datastorePrefs)

    //coroutine
    implementation(libs.kotlin.coroutines.android)

    //Hilt
    implementation(libs.google.hilt.library)
    ksp(libs.google.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    //services
    implementation(libs.firebase.messaging.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)


    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    implementation(libs.coil.gif)

    implementation(libs.accompanist.systemuicontroller)

    //picker
    implementation(libs.snapper)

    //toast messages
    implementation(libs.toasty)

    //Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.dash)
    implementation(libs.media3.ui)

    //Animation - AVD
    implementation(libs.compose.animation.graphic)

    //Loader
    implementation(libs.mahboubehSeyedpour.loading)

    //permission
    implementation(libs.ezpermission)

    //script
    implementation(libs.script)

    //room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //worker
    implementation(libs.worker.runtime)
    implementation(libs.worker.hilt)

    //firebase crashlytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

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

    if (dayInt in 11..13) return suffix

    val suffixMap =
        arrayOfNulls<String>(100) + arrayOf("nd", "st", "rd") + (4..20).map { "th" } + arrayOf(
            "st",
            "nd",
            "rd"
        ) + (24..30).map { "th" } + arrayOf("st")

    suffix = suffixMap[dayInt % 100] ?: "th"

    return suffix
}