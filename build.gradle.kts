plugins {
    // Apply the java application plugin to add support for building a CLI application.
    application
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0-rc"
}

version = 0.9
group = "intellibitz"

application {
    // Define the main class for the application.
    mainClass.set("intellibitz.sted.Main")
//    mainModule.set("intellibitz")
//    the executable batch file name and the jar name
    applicationName = "sted"
//    copy it at the base of the distribution, NOTE: default is under 'bin'
    executableDir = ""
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

distributions {
    main {
        distributionBaseName.set("sted")
    }
}

// if normal source directory convention is not followed, define custom sourcesets
sourceSets {
    main {
        java.srcDirs(listOf("src/main"))
        resources.srcDirs(listOf("src/main/resources"))
    }
    test {
        java.srcDirs(listOf("src/test"))
        resources.srcDirs(listOf("src/test/resources"))
    }
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation(kotlin("script-runtime"))
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    // Align versions of all Kotlin components
    implementation(kotlin("bom"))
    // Use the Kotlin test library.
    testImplementation(kotlin("test"))
    // Use the Kotlin JUnit integration.
    testImplementation(kotlin("test-junit"))
}
