plugins {
    kotlin("jvm") version "2.4.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    implementation("org.bouncycastle:bcprov-jdk18on:1.86")
}

kotlin {
    jvmToolchain(26)
}

tasks.test {
    useJUnitPlatform()
}