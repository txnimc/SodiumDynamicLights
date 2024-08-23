plugins {
    `kotlin-dsl`
    id("me.modmuss50.mod-publish-plugin") version "0.6.3" apply false
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.kikugie.dev/releases")
}

dependencies {
   implementation("me.modmuss50:mod-publish-plugin:0.6.3")

}