//version = 0.9
//group = "intellibitz"

plugins {
    // Apply the java application plugin to add support for building a CLI application.
    application
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0-rc"
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")
}

distributions {
    main {
        distributionBaseName.set("sted")
        version = 0.9
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

application {
    //    mainModule.set("intellibitz")
//    the executable batch file name and the jar name
    applicationName = "sted"
    applicationDefaultJvmArgs = listOf("-Duser.dir=STED_APP_HOME")
    // Define the main class for the application.
    mainClass.set("intellibitz.sted.Main")
//    copy it at the base of the distribution, NOTE: default is under 'bin'
    executableDir = ""
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks {
    startScripts {
        doLast {
            var text = windowsScript.absoluteFile.readText()
//            text = text.replace("STED_APP_HOME", "%~dp0..")
            text = text.replace("STED_APP_HOME", "%APP_HOME%")
            text = text.replace("CLASSPATH=", "CLASSPATH=.;")
            windowsScript.absoluteFile.writeText(text)
            var text2 = unixScript.absoluteFile.readText()
            text2 = text2.replace("STED_APP_HOME", "\$APP_HOME")
            text2 = text2.replace("CLASSPATH=", "CLASSPATH=.:")
            unixScript.absoluteFile.writeText(text2)
        }
    }
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

