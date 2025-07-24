plugins {
    kotlin("jvm") version "2.0.21"
    application
}

repositories {
    mavenCentral()
    google()
}

application {
    mainClass.set("com.example.gateway.MainKt")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.example.gateway.MainKt")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("androidx.security:security-crypto:1.1.0-beta01")
}
