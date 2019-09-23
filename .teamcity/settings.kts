/*
 * MUCtool Web Toolkit
 *
 * Copyright 2019 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.dockerRegistry
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

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

version = "2019.1"

project {

    buildType(ApiHealthChecks)
    buildType(Build)

    features {
        dockerRegistry {
            id = "PROJECT_EXT_2"
            name = "Docker Registry"
            url = "https://docker.io"
            userName = "loxal"
            password = "credentialsJSON:00cc6915-e715-4a6c-9b7f-5fad1181491c"
        }
        feature {
            id = "PROJECT_EXT_5"
            type = "IssueTracker"
            param("secure:password", "")
            param("name", "loxal/muctool")
            param("pattern", """#(\d+)""")
            param("authType", "anonymous")
            param("repository", "https://github.com/loxal/muctool")
            param("type", "GithubIssues")
            param("secure:accessToken", "")
            param("username", "")
        }
    }
}

object ApiHealthChecks : BuildType({
    name = "API Health Checks"

    maxRunningBuilds = 5

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            scriptContent = "sh ./ops/api-health-check.sh"
            dockerImage = "openjdk:13-slim-buster"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerRunParameters = "-v /home/minion/.gradle:/root/.gradle"
        }
    }

    triggers {
        schedule {
            schedulingPolicy = cron {
                minutes = "0/1"
            }
            branchFilter = ""
            triggerBuild = always()
            withPendingChangesOnly = false
            param("hour", "19")
        }
    }

    failureConditions {
        errorMessage = true
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "ERROR"
            reverse = false
            stopBuildOnFailure = true
        }
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "AssertionError"
            reverse = false
            stopBuildOnFailure = true
        }
    }
})

object Build : BuildType({
    name = "Release"

    params {
        param("env.SCM_HASH", "%build.vcs.number%")
        param("env.BUILD_NUMBER", "%build.number%")
    }

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Build Service JAR w/ Docker"
            scriptContent = "./gradlew clean build shadowJar --info --no-build-cache"
            dockerImage = "openjdk:13-slim-buster"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerPull = true
            dockerRunParameters = "-v /home/minion/.gradle:/root/.gradle"
        }
        script {
            scriptContent = """
                BUILD_COUNTER=%build.counter% 
                sh release.sh '-P password=M0npugis'
                
                sudo su minion
                cd /srv/muctool
                git reset --hard HEAD
                git pull
                sudo chown -R 1000:1000 /srv/muctool
            """.trimIndent()
        }
    }

    triggers {
        vcs {
        }
    }

    failureConditions {
        errorMessage = true
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "AssertionError"
            reverse = false
            stopBuildOnFailure = true
        }
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "ERROR"
            reverse = false
            stopBuildOnFailure = true
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_2"
            }
        }
    }
})
