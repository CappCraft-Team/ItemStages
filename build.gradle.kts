import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.4.21"
    id("net.minecraftforge.gradle") version "4.0.13"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

group = "team.cappcraft.itemstages"
version = "3.0"
val mcVersion = "1.12.2"
base.archivesBaseName = "itemstages"

repositories {
    jcenter()
    mavenCentral()
    maven("https://files.minecraftforge.net/maven")
    //TOP
    maven("http://maven.tterrag.com/")
    //CraftTweaker
    maven("http://maven.blamejared.com/")
    //JEI
    maven("http://dvs1.progwml6.com/files/maven")
    //HWYLA
    maven("http://tehnut.info/maven")
    //Curse Maven
    maven("https://www.cursemaven.com")
    //Kotlin For Forge
    maven("https://maven.pkg.github.com/CappCraft-Team/KotlinForForge") {
        credentials {
            username = "1449182174@qq.com"
            password = "bde866e4093346da4af110310e73209198aa0b84"
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

dependencies {
    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2854")
    implementation("thedarkcolour.kotlinforforge:kotlinforforge:2.0.6")
    implementation("mezz.jei:jei_1.12.2:4.+")
    implementation("mcjty.theoneprobe:TheOneProbe-1.12:1.12-1.+")
    implementation("CraftTweaker2:CraftTweaker2-MC1120-Main:1.12-4.+")
    implementation("curse.maven:bookshelf-228525:2836960")
    implementation("curse.maven:gamestages-268655:2951840")
    implementation(kotlin("stdlib-jdk8"))
}

minecraft {
    mappings("snapshot", "20171003-1.12")
    buildDir.resolve("generatedATs").walk().filter {
        it.isFile && it.name == "accesstransformer.cfg"
    }.let {
        accessTransformers.addAll(it)
    }
    runs {
        create("client") {
            workingDirectory = project.file("run").toString()
            
            // Recommended logging data for a userdev environment
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            
            // Recommended logging level for the console
            property("forge.logging.console.level", "trace")
            
            mods {
                create(base.archivesBaseName) {
                    sources(sourceSets.main.get())
                }
            }
        }
        
        create("server") {
            workingDirectory = project.file("run").toString()
            
            // Recommended logging data for a userdev environment
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            
            // Recommended logging level for the console
            property("forge.logging.console.level", "debug")
            
            mods {
                create(base.archivesBaseName) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

tasks {
    task("transformAccessTransformers") {
        group = "access_transformer"
        doLast {
            buildDir.resolve("generatedATs").deleteRecursively()
            project.configurations.runtimeClasspath.get().filter {
                !it.name.startsWith("forge")
            }.forEach {
                zipTree(it).filter { content ->
                    content.name.endsWith("_at.cfg")
                }.forEach { at ->
                    println("Found AccessTransFormer: ${at.name} in ${it.name}")
                    at.copyTo(buildDir.resolve("generatedATs/${at.nameWithoutExtension}/accesstransformer.cfg"))
                }
            }
        }
    }
    
    processResources {
        inputs.properties(
            "version" to version,
            "mcversion" to mcVersion
        )
        filesMatching("mcmod.info") {
            expand("version" to version, "mcversion" to mcVersion)
        }
        doLast {
            copy {
                from(sourceSets.main.get().resources.srcDirs)
                into(buildDir.resolve("classes/kotlin/main"))
            }
        }
    }
    
    withType<Jar> {
        manifest {
            attributes(
                "Specification-Title" to "ItemStages",
                "Specification-Vendor" to "CappCraft Team",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to "$archiveVersion",
                "Implementation-Vendor" to "CappCraft Team",
                "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            )
        }
    }
    
    withType<KotlinCompile> {
//        doFirst {
//            copy {
//                from(this@withType.source)
//                with(buildDir.resolve("generatedKotlin")) {
//                    this.deleteRecursively()
//                    into(this)
//                    this@withType.source = fileTree(this)
//                }
//
//                filter<org.apache.tools.ant.filters.ReplaceTokens>(
//                    "tokens" to mapOf("version" to version)
//                )
//            }
//        }
        kotlinOptions.jvmTarget = "1.8"
    }
    
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}