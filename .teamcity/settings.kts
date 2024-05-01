import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerRegistry
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.03"

project {

    vcsRoot(HttpsGithubComPmabresRaspberryBenchmarkRefsHeadsMain)

    buildType(Build)

    features {
        dockerRegistry {
            id = "PROJECT_EXT_2"
            name = "Docker Registry"
        }
    }
}

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
                sysbench cpu --threads=4 run
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

object HttpsGithubComPmabresRaspberryBenchmarkRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/pmabres/raspberry-benchmark#refs/heads/main"
    url = "https://github.com/pmabres/raspberry-benchmark"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
