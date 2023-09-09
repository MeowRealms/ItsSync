@file:Suppress("PropertyName", "SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val taboolib_version: String by project

val kotlinVersionNum: String
    get() = project.kotlin.coreLibrariesVersion.replace(".", "")

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

subprojects {
    apply<JavaPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("http://ptms.ink:8081/repository/releases/")
            isAllowInsecureProtocol = true
        }
        maven("https://repo.spongepowered.org/maven")
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
        // 引入 Taboolib
        // 注意 common 模块不可使用 implementation 引入（即使用 ShadowJar 插件打包）
        // 因为需要借助 TabooLib Gradle 插件修改一些东西
        compileOnly("io.izzel.taboolib:common:$taboolib_version")
        compileOnly("redis.clients:jedis:4.2.3")
        // 使用 shadowJar 插件打包的模块
        implementation("io.izzel.taboolib:common-5:$taboolib_version")
        implementation("io.izzel.taboolib:module-chat:$taboolib_version")
        implementation("io.izzel.taboolib:module-configuration:$taboolib_version")
        implementation("io.izzel.taboolib:module-database:$taboolib_version")
        implementation("io.izzel.taboolib:module-lang:$taboolib_version")
        implementation("io.izzel.taboolib:module-metrics:$taboolib_version")
        implementation("io.izzel.taboolib:module-nms:$taboolib_version")
        implementation("io.izzel.taboolib:platform-bukkit:$taboolib_version")
        implementation("io.izzel.taboolib:platform-bungee:$taboolib_version")
        implementation("io.izzel.taboolib:platform-velocity:$taboolib_version")
        implementation("io.izzel.taboolib:expansion-command-helper:$taboolib_version")
        implementation("io.izzel.taboolib:expansion-persistent-container-object:$taboolib_version")
        implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.39-SNAPSHOT")
        implementation("com.eatthepath:fast-uuid:0.2.0")
    }

    // =============================
    //       下面的东西不用动
    // =============================
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xextended-compiler-checks")
        }
    }
    // 这里不要乱改
    tasks.withType<ShadowJar> {
        // 重定向 TabooLib
        relocate("taboolib", "${rootProject.group}.taboolib")
        // 重定向 Kotlin
        relocate("kotlin.", "kotlin${kotlinVersionNum}.") { exclude("kotlin.Metadata") }

        // =============================
        //     如果你要重定向就在下面加
        // =============================
        relocate("com.alibaba.fastjson2", "${rootProject.group}.libs.fastjson2")
        relocate("com.eatthepath.uuid", "${rootProject.group}.libs.uuid")
        relocate("redis.clients.jedis.", "redis.clients.jedis_4_2_3.")
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}