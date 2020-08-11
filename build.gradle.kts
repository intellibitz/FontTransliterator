//group = "intellibitz"

plugins {
    // Apply the java application plugin to add support for building a CLI application.
    application
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0-rc"
    kotlin("kapt") version "1.4.0-rc"
    kotlin("plugin.serialization") version "1.4.0-rc"
    id("org.jetbrains.dokka") version "1.4.0-rc"
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://dl.bintray.com/kotlin/dokka")
}

distributions {
    main {
        distributionBaseName.set("sted")
        version = 0.9
    }
}

application {
    //    mainModule.set("intellibitz")
//    the executable batch file name and the jar name
    applicationName = "sted"
    applicationDefaultJvmArgs = listOf("-Dsted.app.home=STED_APP_HOME")
    // Define the main class for the application.
    mainClass.set("sted.Main")
//    copy it at the base of the distribution, NOTE: default is under 'bin'
    executableDir = ""
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// if normal source directory convention is not followed, define custom sourcesets
sourceSets {
    main {
        java.srcDirs(listOf("src/main/intellibitz"))
        resources.srcDirs(listOf("src/main/resources"))
    }
    test {
        java.srcDirs(listOf("src/test/intellibitz"))
        resources.srcDirs(listOf("src/test/resources"))
    }
}

dependencies {
    implementation(kotlin("script-runtime"))
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
    // Align versions of all Kotlin components
    implementation(kotlin("bom"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0-rc")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0") // JVM dependency
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.0-rc")
    implementation(kotlin("reflect"))
    // Use the Kotlin test library.
    testImplementation(kotlin("test"))
    // Use the Kotlin JUnit integration.
    testImplementation(kotlin("test-junit"))
//    testImplementation(kotlin("test-junit5"))
//    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.0") // for kotest framework
//    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.0") // for kotest core jvm assertions
//    testImplementation("io.kotest:kotest-property-jvm:4.1.0") // for kotest property test
    //    required, to run from command line gradle task
    runtimeOnly(files("src/dist"))
}

tasks {
    startScripts {
        doLast {
            var text = windowsScript.absoluteFile.readText()
            text = text.replace("STED_APP_HOME", "%APP_HOME")
            text = text.replace("CLASSPATH=", "CLASSPATH=.;")
            windowsScript.absoluteFile.writeText(text)
            var text2 = unixScript.absoluteFile.readText()
            text2 = text2.replace("STED_APP_HOME", "\$APP_HOME")
            text2 = text2.replace("CLASSPATH=", "CLASSPATH=.:")
            unixScript.absoluteFile.writeText(text2)
        }
    }
    jar {
        manifest {
            attributes(
                "Main-Class" to "sted.main"
            )
        }
    }
    dokkaHtml {
        outputDirectory = "$buildDir/dokka"
    }
/*
    test{
        useJUnitPlatform()
    }
*/
}
