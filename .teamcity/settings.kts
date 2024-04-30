package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object Build : BuildType({
    name = "Build"

    vcs {
        root(HttpsGithubComPmabresRaspberryBenchmarkRefsHeadsMain)
    }
    steps {
        script {
            id = "simpleRunner"
            scriptContent = """
                apt update
                apt -y install curl
                curl -s https://packagecloud.io/install/repositories/akopytov/sysbench/script.deb.sh | bash
                apt -y install sysbench
                sysbench run --test=cpu --num-threads=4
            """.trimIndent()
            dockerImage = "ubuntu:jammy"
        }
    }
    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_2"
            }
        }
    }
})
