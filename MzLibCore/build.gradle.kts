import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("mz.lib.java-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

tasks.withType<ShadowJar>{
    archiveBaseName.set("MzLibCore")
    archiveFileName.set(archiveBaseName.get() + " v" + archiveVersion.get() + ".jar")
    destinationDirectory.set(File(destinationDirectory.get().asFile.parentFile.parentFile.parentFile, "out"))
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "mz.mzlib.plugin.PluginManager"
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation("io.github.karlatemp:unsafe-accessor:1.7.0")
    implementation("net.bytebuddy:byte-buddy-agent:1.12.22")
    implementation("com.google.code.gson:gson:2.8.0")
    implementation("com.google.guava:guava:31.0.1-jre")
//    implementation("org.graalvm.polyglot:polyglot:latest.release")
//    implementation("org.graalvm.polyglot:js:latest.release")
}

description = "MzLibCore"
