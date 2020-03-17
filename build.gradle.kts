plugins {
    kotlin("jvm") version "1.3.70"
    application
}

group = "com.github.se7_kn8"
version = "1.6-SNAPSHOT"

application {
    mainClassName = "MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.0")
    implementation("org.apache.logging.log4j:log4j-core:2.13.0")
    implementation("org.apache.logging.log4j:log4j-api:2.13.0")
    implementation("io.javalin:javalin:3.7.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}