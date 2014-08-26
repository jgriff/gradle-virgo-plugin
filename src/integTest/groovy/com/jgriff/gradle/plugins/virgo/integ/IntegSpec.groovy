package com.jgriff.gradle.plugins.virgo.integ

import com.jgriff.gradle.plugins.virgo.TemporaryFolderSpec
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection

abstract class IntegSpec extends TemporaryFolderSpec {
    ProjectConnection gradle

    def setup() {
        // tell gradle about our plugin
        buildFile << """
            buildscript {
                dependencies {
                    classpath files(System.getProperty("test.functional.classes"))
                    classpath fileTree(System.getProperty("test.functional.dependencies"))
                }
            }
        """
    }

    def cleanup() {
        if (gradle != null) {
            gradle.close()
        }
    }

    String applyPlugin(String pluginId) {
        "apply plugin: " + pluginId
    }

    File getBuildFile() {
        file("build.gradle")
    }

    void runTasks(String... tasks) {
        gradle = GradleConnector.newConnector()
                        .forProjectDirectory(tempDir.getRoot())
                        .connect();

        gradle.newBuild()
              .forTasks(tasks)
              .run()
    }
}
