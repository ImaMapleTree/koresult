plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.sylvona.koresult.example.ApplicationKt")
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":koresult"))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.logback)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
}
