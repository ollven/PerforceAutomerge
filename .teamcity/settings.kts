import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.merge
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.perforceAdminAccess
import jetbrains.buildServer.configs.kotlin.triggers.perforceShelveTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.PerforceVcsRoot

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

version = "2025.03"

project {

    vcsRoot(PerforceLocalhost1666repo1mainline)

    buildType(Artifact2)
    buildType(Composite)
    buildType(Build1)
    buildType(Artifact1)

    features {
        perforceAdminAccess {
            id = "PROJECT_EXT_78"
            name = "Perforce Administrator Access"
            port = "1666"
            userName = "teamcity-testers"
            password = "credentialsJSON:80324454-3f9c-4856-b8af-8b7ac6ccb92a"
        }
    }
}

object Artifact1 : BuildType({
    name = "Artifact1"

    artifactRules = """
        2024-12-23 15.33.26.jpg => images
        2024-12-23 15.33.45.jpg => images
    """.trimIndent()

    vcs {
        root(PerforceLocalhost1666repo1mainline)
    }
})

object Artifact2 : BuildType({
    name = "Artifact2"

    artifactRules = "filetoconflict1.txt => files"

    vcs {
        root(PerforceLocalhost1666repo1mainline)
    }
})

object Build1 : BuildType({
    name = "Build1"

    vcs {
        root(PerforceLocalhost1666repo1mainline)
    }

    steps {
        script {
            id = "simpleRunner"
            scriptContent = "ls"
            param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
            param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
            param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        merge {
            branchFilter = "+:<default>"
            destinationBranch = "//repo1/development"
        }
    }
})

object Composite : BuildType({
    name = "Composite"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(PerforceLocalhost1666repo1mainline)

        showDependenciesChanges = true
    }

    triggers {
        perforceShelveTrigger {
        }
    }

    dependencies {
        artifacts(Artifact1) {
            buildRule = lastSuccessful()
            artifactRules = "images => ima ges"
        }
        artifacts(Artifact2) {
            buildRule = lastSuccessful()
            artifactRules = "files => fi les"
        }
    }
})

object PerforceLocalhost1666repo1mainline : PerforceVcsRoot({
    name = "perforce://localhost:1666/repo1/mainline"
    port = "1666"
    mode = stream {
        streamName = "//repo1/mainline"
        enableFeatureBranches = true
        branchSpec = "+:*"
    }
    userName = "teamcity-testers"
    password = "credentialsJSON:80324454-3f9c-4856-b8af-8b7ac6ccb92a"
    workspaceOptions = """
        Options:        noallwrite clobber nocompress unlocked nomodtime rmdir
        Host:           %teamcity.agent.hostname%
        SubmitOptions:  revertunchanged
        LineEnd:        local
    """.trimIndent()
})
