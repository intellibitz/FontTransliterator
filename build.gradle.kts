plugins {
    // Apply the application plugin to add support for building a CLI application.
    application
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0-rc"
}

// keep this until all targets fully migrated
ant.importBuild("build.xml")
val PROJECT_PATH: String by project
val SOURCE_PATH: String by project
val OUT_PROD_PATH: String by project
val OUT_PATH: String by project
val STED_JAR: String by project


// if normal source directory convention is not followed, define custom sourcesets
sourceSets.main {
    java.srcDir("src")
    java.outputDir = file("./out/classes/production/FontTransliterator")
}
sourceSets.test {
    java.srcDir("test")
    java.outputDir = file("./out/classes/test/FontTransliterator")
}

defaultTasks("initSted")

val outPath = File(OUT_PATH)
val outProdPath = File(OUT_PROD_PATH)

tasks {
    register("initSted") {
        doLast {
            outPath.createNewFile()
            outProdPath.createNewFile()
            println("Project Path: ${PROJECT_PATH}")
            println("Source Path: ${SOURCE_PATH}")
            println("Classes Path: ${OUT_PROD_PATH}")
            println("Deploy Path: ${OUT_PATH}")
            println("Jar Path: ${STED_JAR}")
        }
    }
    register("copyResource") {
        dependsOn("initSted")
        doLast {
            copy {
                from(PROJECT_PATH)
                into(OUT_PATH)
                exclude("out", "build", "dist", "test", "gradle", ".idea", ".gradle")
            }
        }
    }
    register("compileSted") {
        dependsOn("initSted")
        doLast {
            named("compileJava")
        }
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
