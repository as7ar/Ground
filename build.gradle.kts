plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "9.3.0"
//    id("java")
}

group = "kr.astar"
version = "1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT") {
        exclude("com.google.code.gson", "gson")
    }
//    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.1.0-SNAPSHOT")
    compileOnly("net.wesjd:anvilgui:1.10.6-SNAPSHOT")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    minimize {
        exclude("kr.astar.*")
    }
    archiveFileName.set("Ground-${version}.jar")
    archiveClassifier.set("all")
    mergeServiceFiles()

    dependencies {
        include(dependency("net.wesjd:anvilgui:1.10.6-SNAPSHOT"))
    }

//    destinationDirectory=file("C:\\Users\\PC\\Desktop\\Test_Server\\21.8\\plugins")
}