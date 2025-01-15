import org.jetbrains.kotlin.gradle.internal.kapt.incremental.UnknownSnapshot

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
//    classpath("com.google.gms:google-services:4.3.10")  // Update if necessary
//    classpath("com.android.tools.build:gradle:7.0.4")   // Update if necessary

}


buildscript {
    repositories {
        maven { url = uri( "https://jitpack.io") }
        jcenter()  // Deprecated, but still required for some legacy dependencies
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // Correct placement
    }
}