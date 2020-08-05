plugins {
    // Apply the application plugin to add support for building a CLI application.
    application
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0-rc"
}

version = 0.9
group = "intellibitz"

application {
    // Define the main class for the application.
    mainClass.set("intellibitz.sted.Main")
//    the executable batch file name and the jar name
    applicationName = "sted"
//    copy it at the base of the distribution, NOTE: default is under 'bin'
    executableDir = ""
}

// if normal source directory convention is not followed, define custom sourcesets
sourceSets.main {
    java.srcDirs(listOf("src/main"))
    resources.srcDirs(listOf("src/main/resources"))
}
sourceSets.test {
    java.srcDirs(listOf("src/test"))
    resources.srcDirs(listOf("src/test/resources"))
}

distributions {
    main {
        contents {

        }
    }
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
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

defaultTasks("runSted")
tasks {
    register("initSted") {
        doLast {
//            if (File(buildPathProp).createNewFile()) println("created $buildPathProp")
        }
    }
    register("copyResource") {
        dependsOn("initSted")
        doLast {
/*
            copy {
                from(".")
                into(buildPathProp)
                exclude("out", "build", "dist", "gradle", ".idea", ".gradle")
            }
*/
        }
    }
    register("compileSted") {
        dependsOn("initSted")
        dependsOn("compileJava")
        doLast {
        }
    }
    register("deploySted") {
        dependsOn("copyResource")
        dependsOn("compileSted")
        dependsOn("jar")
    }
    register("runSted") {
        dependsOn("installDist")
        doLast {
            exec {
                workingDir("./build/install/sted")
                commandLine("cmd", "/c", "sted.bat")
//                executable("./build/install/sted.bat")
            }
        }
    }
}

