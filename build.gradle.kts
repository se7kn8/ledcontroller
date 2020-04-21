plugins {
    kotlin("jvm") version "1.3.70"
    id("de.clashsoft.angular-gradle") version "0.1.6"
    application
}

group = "com.github.se7_kn8"
version = "1.6.1-SNAPSHOT"

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

angular {
    appDir.set("webapp")
    buildArgs.set(listOf("build", "--prod"))
}

tasks.withType<de.clashsoft.gradle.angular.BuildAngularTask> {
    finalizedBy("copyAngular")
}

tasks.register<Copy>("copyAngular"){
    dependsOn("clearAngularOutput")
    from("webapp/dist/lighting-control")
    into("src/main/resources/static/lighting-control")
}

tasks.register<Delete>("clearAngularOutput"){
    delete("src/main/resources/static/lighting-control")
}