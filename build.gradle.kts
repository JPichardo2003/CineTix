// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false //aqui se busca la ultima version https://github.com/google/ksp/releases
    alias(libs.plugins.kotlinx.serialization) apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
}