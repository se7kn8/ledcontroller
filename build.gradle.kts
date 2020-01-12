plugins {
    kotlin("jvm") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-simple:1.8.0-beta4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")
    implementation("io.javalin:javalin:3.7.0")

}