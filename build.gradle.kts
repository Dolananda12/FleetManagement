// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    kotlin("kapt") version "1.9.21" apply false
    val room_version = "2.6.1"
    id("androidx.room") version room_version apply false
    kotlin("plugin.serialization") version "1.9.0" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}