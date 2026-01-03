import idk.core.buildsrc.MyClass;
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "idk.core.engine"
    compileSdk = 35

    defaultConfig {
        applicationId = "idk.core.engine"
        minSdk = 24
        targetSdk = 35
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
}
tasks.register<MyClass>("myTask")
tasks.named("preBuild") {
    dependsOn("myTask")
}
dependencies {
    // https://mvnrepository.com/artifact/com.github.stephengold/Minie
    implementation("com.github.stephengold:Minie:9.0.3+droid")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation("de.javagl:obj:0.4.0")
    implementation(libs.constraintlayout)
    //implementation("com.github.stephengold:Minie:8.1.0:android-arm64-v8a")
    //implementation("com.github.stephengold:Minie:8.1.0:android-armeabi-v7a")
    //implementation("com.github.stephengold:Minie:8.1.0:android-x86")
    //implementation("com.github.stephengold:Minie:8.1.0:android-x86_64")
    //implementation(files("src\\main\\libs\\gdx-1.13.5.jar"))
    //implementation(files("src\\main\\libs\\gdx-bullet-1.13.5.jar"))
    //implementation(files("src\\main\\libs\\shared.jar"))

testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}