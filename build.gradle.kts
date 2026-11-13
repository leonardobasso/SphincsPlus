plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.allopen") version "2.2.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.5.0"
}

sourceSets { create("benchmark") }

benchmark {
    targets {
        register("benchmark")
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
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
    "benchmarkImplementation"(sourceSets.main.get().output)
    "benchmarkImplementation"(sourceSets.main.get().runtimeClasspath)
    "benchmarkImplementation"("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.5.0")
}

kotlin {
    jvmToolchain(26)
}

tasks.test {
    useJUnitPlatform()
}