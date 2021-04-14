plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // Dependencies which should be shaded in
    implementation("io.papermc:paperlib:1.0.6")
    implementation("com.github.johnnyjayjay:compatre:v0.2.2-alpha")

    // Dependencies which are already available or not required at runtime
    compileOnly("org.spigotmc:spigot:1.13.2")
    compileOnly("org.jetbrains:annotations:20.1.0")

    // Annotation processors
    annotationProcessor("org.jetbrains:annotations:20.1.0")
}

group = "net.seliba"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    jar {
        dependsOn("shadowJar")
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("io.papermc.lib", "net.seliba.thirdpersonspectator.paperlib")
        relocate("com.github.johnnyjayjay.compatre", "net.seliba.thirdpersonspectator.compatre")

        // Fix for the ASM for some Spigot versions (for instance 1.13.2)
        relocate("org.objectweb.asm", "net.seliba.thirdpersonspectator.asm")
    }
}
