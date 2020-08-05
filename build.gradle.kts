// keep this until all targets fully migrated
//ant.importBuild("build.xml")
defaultTasks("initSted")
val projectPathProp: String by project
println("Project Path: $projectPathProp")
val sourcePathProp: String by project
println("Source Path: $sourcePathProp")
val outPathProp: String by project
println("Deploy Path: $outPathProp")
val stedJarProp: String by project
println("Jar Path: $stedJarProp")


plugins {
    // Apply the application plugin to add support for building a CLI application.
    application
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0-rc"
}

// if normal source directory convention is not followed, define custom sourcesets
sourceSets.main {
    java.srcDirs(listOf("src/main"))
    java.outputDir = file(outPathProp)
    resources.srcDirs(listOf("src/main/resources"))
}
sourceSets.test {
    java.srcDirs(listOf("src/test"))
    java.outputDir = file(outPathProp)
    resources.srcDirs(listOf("src/test/resources"))
}

tasks {
    register("initSted") {
        doLast {
            if (File(outPathProp).createNewFile()) println("created $outPathProp")
        }
    }
    register("copyResource") {
        dependsOn("initSted")
        doLast {
            copy {
                from(projectPathProp)
                into(outPathProp)
                exclude("out", "build", "dist", "gradle", ".idea", ".gradle")
            }
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
/*

 */
}

application {
    // Define the main class for the application.
    mainClass.set("intellibitz.sted.Main")
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
