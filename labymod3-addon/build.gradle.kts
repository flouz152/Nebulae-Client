plugins {
    java
}

group = "nebulae.addon"
version = "1.0.0"

description = "Standalone LabyMod 3 addon bundle for the Nebulae FTHelper and TargetESP modules"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
}

val labyRuntime: Configuration by configurations.creating

configurations {
    compileOnly.extendsFrom(labyRuntime)
}

dependencies {
    labyRuntime(files("libs/labymod-3-client.jar"))
    labyRuntime(files("libs/minecraft-1.16.5-mapped.jar"))
}

tasks.jar {
    from("src/main/resources")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Nebulae"
            )
        )
    }
}

tasks.register<Copy>("prepareLabyRuntime") {
    description = "Copies placeholder runtime jars into the libs/ directory if they are missing."
    from(layout.projectDirectory.dir("placeholders"))
    into(layout.projectDirectory.dir("libs"))
    onlyIf {
        val libsDir = layout.projectDirectory.dir("libs").asFile
        !java.io.File(libsDir, "labymod-3-client.jar").exists() ||
            !java.io.File(libsDir, "minecraft-1.16.5-mapped.jar").exists()
    }
}
