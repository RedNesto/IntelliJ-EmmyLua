/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.intellij").version("1.7.0")
    id("org.jetbrains.kotlin.jvm").version("1.6.21")
    id("de.undercouch.download").version("5.0.4")
}

data class BuildData(
    val ideaSDKShortVersion: String,
    // https://www.jetbrains.com/intellij-repository/releases
    val ideaSDKVersion: String,
    val sinceBuild: String,
    val untilBuild: String,
    val archiveName: String = "IntelliJ-EmmyLua",
    val jvmTarget: String = "11",
    val targetCompatibilityLevel: JavaVersion = JavaVersion.VERSION_11,
    val explicitJavaDependency: Boolean = true,
    val bunch: String = ideaSDKShortVersion,
    // https://github.com/JetBrains/gradle-intellij-plugin/issues/403#issuecomment-542890849
    val instrumentCodeCompilerVersion: String = ideaSDKVersion,
    val pluginVerifierIdeVersions: Set<String> = setOf(ideaSDKVersion.substringBefore('-'))
)

val buildDataList = listOf(
    BuildData(
        ideaSDKShortVersion = "222",
        ideaSDKVersion = "222.3345.47-EAP-SNAPSHOT",
        sinceBuild = "222",
        untilBuild = "222.*",
    ),
    BuildData(
        ideaSDKShortVersion = "221",
        ideaSDKVersion = "221.5080.210",
        sinceBuild = "221",
        untilBuild = "221.*",
        pluginVerifierIdeVersions = setOf("221.5080.210")
    ),
    BuildData(
        ideaSDKShortVersion = "213",
        ideaSDKVersion = "213.5744.223",
        sinceBuild = "212",
        untilBuild = "221.*",
        jvmTarget = "1.8",
    ),
    BuildData(
        ideaSDKShortVersion = "211",
        ideaSDKVersion = "211.7142.45",
        sinceBuild = "211",
        untilBuild = "211.*",
        jvmTarget = "1.8",
    ),
    BuildData(
        ideaSDKShortVersion = "203",
        ideaSDKVersion = "203.5981.155",
        sinceBuild = "203",
        untilBuild = "203.*",
        jvmTarget = "1.8",
        pluginVerifierIdeVersions = setOf("203.8084.24")
    ),
    BuildData(
        ideaSDKShortVersion = "202",
        ideaSDKVersion = "202.6397.94",
        sinceBuild = "202",
        untilBuild = "202.*",
        jvmTarget = "1.6",
        targetCompatibilityLevel = JavaVersion.VERSION_1_8
    ),
    BuildData(
        ideaSDKShortVersion = "201",
        ideaSDKVersion = "201.8743.12",
        sinceBuild = "201",
        untilBuild = "201.*",
        jvmTarget = "1.6",
        targetCompatibilityLevel = JavaVersion.VERSION_1_8
    ),
    BuildData(
        ideaSDKShortVersion = "193",
        ideaSDKVersion = "193.5233.102",
        sinceBuild = "193",
        untilBuild = "194.*",
        jvmTarget = "1.6",
        targetCompatibilityLevel = JavaVersion.VERSION_1_8
    ),
    BuildData(
        ideaSDKShortVersion = "182",
        ideaSDKVersion = "182.2371.4",
        sinceBuild = "182",
        untilBuild = "193.*",
        explicitJavaDependency = false,
        jvmTarget = "1.6",
        targetCompatibilityLevel = JavaVersion.VERSION_1_8,
        pluginVerifierIdeVersions = setOf("192.7142.36", "191.8026.42", "183.6156.11", /*"182.5262.2"*/"182.2371.4")
    ),
    BuildData(
        ideaSDKShortVersion = "172",
        ideaSDKVersion = "172.4574.19",
        sinceBuild = "172",
        untilBuild = "181.*",
        explicitJavaDependency = false,
        jvmTarget = "1.6",
        targetCompatibilityLevel = JavaVersion.VERSION_1_8,
        pluginVerifierIdeVersions = setOf("181.5684.4", "173.4710.11", "172.4574.19")
    ),
    BuildData(
        ideaSDKShortVersion = "171",
        ideaSDKVersion = "171.4694.73",
        sinceBuild = "171",
        untilBuild = "171.*",
        explicitJavaDependency = false,
        jvmTarget = "1.6",
        targetCompatibilityLevel = JavaVersion.VERSION_1_8
    )
)

val buildVersion = System.getProperty("IDEA_VER") ?: buildDataList.first().ideaSDKShortVersion

val buildVersionData = buildDataList.find { it.ideaSDKShortVersion == buildVersion }!!

val emmyDebuggerVersion = "1.2.9"

val resDir = "src/main/resources"

val isWin = Os.isFamily(Os.FAMILY_WINDOWS)

val isAppVeyor = System.getenv("APPVEYOR") != null
val isGithubActions = System.getenv("GITHUB_ACTIONS") != null
val isCI = isAppVeyor || isGithubActions

// CI
if (isCI) {
    if (isAppVeyor) {
        version = System.getenv("APPVEYOR_REPO_TAG_NAME") ?: System.getenv("APPVEYOR_BUILD_VERSION")
    } else if (isGithubActions) {
        version = System.getenv("GITHUB_RUN_NUMBER")
    }
    exec {
        executable = "git"
        args("config", "--global", "user.email", "love.tangzx@qq.com")
    }
    exec {
        executable = "git"
        args("config", "--global", "user.name", "tangzx")
    }
}

version = "${version}-IDEA${buildVersion}"

fun getRev(): String {
    val os = ByteArrayOutputStream()
    exec {
        executable = "git"
        args("rev-parse", "HEAD")
        standardOutput = os
    }
    return os.toString().substring(0, 7)
}

task("downloadEmmyDebugger", type = Download::class) {
    src(arrayOf(
        "https://github.com/EmmyLua/EmmyLuaDebugger/releases/download/${emmyDebuggerVersion}/darwin-arm64.zip",
        "https://github.com/EmmyLua/EmmyLuaDebugger/releases/download/${emmyDebuggerVersion}/darwin-x64.zip",
        "https://github.com/EmmyLua/EmmyLuaDebugger/releases/download/${emmyDebuggerVersion}/linux-x64.zip",
        "https://github.com/EmmyLua/EmmyLuaDebugger/releases/download/${emmyDebuggerVersion}/win32-x64.zip",
        "https://github.com/EmmyLua/EmmyLuaDebugger/releases/download/${emmyDebuggerVersion}/win32-x86.zip"
    ))

    dest("temp")
    onlyIfModified(true)
}

task("unzipEmmyDebugger", type = Copy::class) {
    dependsOn("downloadEmmyDebugger")
    from(zipTree("temp/win32-x86.zip")) {
        into("windows/x86")
    }
    from(zipTree("temp/win32-x64.zip")) {
        into("windows/x64")
    }
    from(zipTree("temp/darwin-x64.zip")) {
        into("mac/x64")
    }
    from(zipTree("temp/darwin-arm64.zip")) {
        into("mac/arm64")
    }
    from(zipTree("temp/linux-x64.zip")) {
        into("linux")
    }
    destinationDir = file("temp")
}

task("installEmmyDebugger", type = Copy::class) {
    dependsOn("unzipEmmyDebugger")
    from("temp/windows/x64/") {
        include("emmy_core.dll")
        into("debugger/emmy/windows/x64")
    }
    from("temp/windows/x86/") {
        include("emmy_core.dll")
        into("debugger/emmy/windows/x86")
    }
    from("temp/linux/") {
        include("emmy_core.so")
        into("debugger/emmy/linux")
    }
    from("temp/mac/x64") {
        include("emmy_core.dylib")
        into("debugger/emmy/mac/x64")
    }
    from("temp/mac/arm64") {
        include("emmy_core.dylib")
        into("debugger/emmy/mac/arm64")
    }
    destinationDir = file("src/main/resources")
}

project(":") {
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("com.google.code.gson:gson:2.8.6")
        implementation("org.scala-sbt.ipcsocket:ipcsocket:1.3.0")
        implementation("org.luaj:luaj-jse:3.0.1")
        implementation("org.eclipse.mylyn.github:org.eclipse.egit.github.core:2.1.5")
        implementation("com.jgoodies:forms:1.2.1")
    }

    sourceSets {
        main {
            java.srcDirs("gen", "src/main/compat")
            resources.exclude("debugger/**")
            resources.exclude("std/**")
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = buildVersionData.targetCompatibilityLevel
        targetCompatibility = buildVersionData.targetCompatibilityLevel
    }

    intellij {
        type.set("IU")
        updateSinceUntilBuild.set(false)
        downloadSources.set(false)
        version.set(buildVersionData.ideaSDKVersion)
        localPath.set(System.getenv("IDEA_HOME_${buildVersionData.ideaSDKShortVersion}"))
        sandboxDir.set("${project.buildDir}/${buildVersionData.ideaSDKShortVersion}/idea-sandbox")
        val requiredPlugins = mutableListOf<String>()
        if (buildVersionData.ideaSDKShortVersion.toInt() >= 192) {
            requiredPlugins.add("java")
        }
        if (buildVersionData.ideaSDKShortVersion.toInt() >= 212) {
            requiredPlugins.add("grazie")
        }
        plugins.set(requiredPlugins)
    }

    task("bunch") {
        doLast {
            val rev = getRev()
            // reset
            exec {
                executable = "git"
                args("reset", "HEAD", "--hard")
            }
            // clean untracked files
            exec {
                executable = "git"
                args("clean", "-d", "-f")
            }
            // switch
            exec {
                executable = if (isWin) "bunch/bin/bunch.bat" else "bunch/bin/bunch"
                args("switch", ".", buildVersionData.bunch)
            }
            // reset to HEAD
            exec {
                executable = "git"
                args("reset", rev)
            }
        }
    }

    tasks {
        buildPlugin {
            dependsOn("bunch", "installEmmyDebugger")
            archiveBaseName.set(buildVersionData.archiveName)
            from(fileTree(resDir) { include("!!DONT_UNZIP_ME!!.txt") }) {
                into("/${project.name}")
            }
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = buildVersionData.jvmTarget
            }
        }

        compileTestKotlin {
            kotlinOptions {
                jvmTarget = buildVersionData.jvmTarget
            }
        }

        patchPluginXml {
            dependsOn("installEmmyDebugger")
            sinceBuild.set(buildVersionData.sinceBuild)
            untilBuild.set(buildVersionData.untilBuild)
        }

        instrumentCode {
            compilerVersion.set(buildVersionData.instrumentCodeCompilerVersion)
        }

        runPluginVerifier {
            ideVersions.addAll(buildVersionData.pluginVerifierIdeVersions)
        }

        withType<org.jetbrains.intellij.tasks.PrepareSandboxTask> {
            doLast {
                copy {
                    from("src/main/resources/std")
                    into("$destinationDir/${pluginName.get()}/std")
                }
                copy {
                    from("src/main/resources/debugger")
                    into("$destinationDir/${pluginName.get()}/debugger")
                }
            }
        }
    }
}
