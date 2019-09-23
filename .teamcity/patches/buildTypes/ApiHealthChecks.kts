package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'ApiHealthChecks'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("ApiHealthChecks")) {
    expectSteps {
        script {
            scriptContent = "sh ./ops/api-health-check.sh"
            dockerImage = "openjdk:13-alpine"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerRunParameters = "-v /home/minion/.gradle:/root/.gradle"
        }
    }
    steps {
        update<ScriptBuildStep>(0) {
            dockerImage = "openjdk:13-slim-buster"
        }
    }
}