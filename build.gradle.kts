plugins {
    kotlin("jvm") version "1.3.61"
    application
}

group = "com.github.se7_kn8"
version = "1.1-SNAPSHOT"

application {
    mainClassName = "MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-simple:1.8.0-beta4")
    implementation("io.javalin:javalin:3.7.0")
}