package com.jgriff.gradle.plugins.virgo

import com.jgriff.gradle.plugins.virgo.extensions.VirgoExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class VirgoAssemblyPluginSpec extends Specification {
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply(plugin: 'com.github.jgriff.virgo.assembly')
    }

    def "extensions are installed"() {
        expect:
        project.extensions.getByName("virgo") instanceof VirgoExtension
    }

    def "tasks are created for assemblies"() {
        project.virgo {
            assemblies {
                foo {}
                bar {}
            }
        }

        expect:
        project.tasks.findByName("assembleFoo") instanceof VirgoAssemblyTask
        project.tasks.findByName("assembleBar") instanceof VirgoAssemblyTask
    }

    def "no tasks are created when no assemblies exist"() {
        project.virgo {
            assemblies {  }
        }

        expect:
        project.tasks.withType(VirgoAssemblyTask).findAll().isEmpty()
    }

    def "no tasks are created when no configuration exist"() {
        expect:
        project.tasks.withType(VirgoAssemblyTask).findAll().isEmpty()
    }
}
