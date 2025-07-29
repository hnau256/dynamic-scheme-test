import java.util.Properties

plugins {
    alias(libs.plugins.compose.desktop)
    alias(libs.plugins.composeMultiplatform)
    id("hnau.android.app")
    alias(libs.plugins.kotlin.serialization)
}

compose.resources {
    packageOfResClass = "hnau.pinfin.projector.resources"
}

android {
    namespace = "hnau.dynamicschemetest"

    defaultConfig {
        val versionPropsFile = file("version.properties")
        val versionProps = Properties().apply {
            load(versionPropsFile.inputStream())
        }
        val localVersionCode = (versionProps["versionCode"] as String).toInt()
        versionName = versionProps["versionName"] as String + "." + localVersionCode
        versionCode = localVersionCode

        tasks.named("preBuild") {
            doFirst {
                versionProps.setProperty("versionCode", (localVersionCode + 1).toString())
                versionProps.store(versionPropsFile.outputStream(), null)
            }
        }
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    signingConfigs {
        create("qa") {
            storeFile = file("keystores/qa.keystore")
            storePassword = "password"
            keyAlias = "qa"
            keyPassword = "password"
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix =".debug"
        }
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            proguardFile("proguard-rules.pro")
            //signingConfig = signingConfigs.getByName("release")
        }
        create("qa") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("qa")
            applicationIdSuffix =".qa"
        }
    }
}

compose.resources {
    packageOfResClass = "hnau.dynamicschemetest.projector"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.serialization.json)
                implementation(libs.hnau.projector)
                implementation(libs.hnau.model)
                implementation(libs.hnau.dynamiccolor)
                implementation(libs.pipe.annotations)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.activity.compose)
                implementation(libs.android.appcompat)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "hnau.dynamicschemetest.app.DesktopAppKt"
    }
}
