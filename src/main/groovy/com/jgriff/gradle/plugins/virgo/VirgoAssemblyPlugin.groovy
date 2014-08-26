package com.jgriff.gradle.plugins.virgo

import com.jgriff.gradle.plugins.virgo.extensions.Plan
import com.jgriff.gradle.plugins.virgo.extensions.Repository
import com.jgriff.gradle.plugins.virgo.extensions.UserRegionConfig
import com.jgriff.gradle.plugins.virgo.extensions.VirgoAssemblyDescriptor
import com.jgriff.gradle.plugins.virgo.extensions.VirgoExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin for assembling an application on top of Eclipse VirgoAssemblyDescriptor.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class VirgoAssemblyPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        createExtensionsFor(project)
        createTasksFor(project)
    }

    /**
     * Extends the DSL to express our assembly plugin configuration.
     */
    private void createExtensionsFor(Project project) {
        def assemblies = project.container(VirgoAssemblyDescriptor) {
            def assemblyName = it

            def repositories = project.container(Repository) {
                def repositoryName = it
                project.virgo.assemblies."$assemblyName".extensions.create(repositoryName, Repository, repositoryName)
            }

            def plans = project.container(Plan) {
                def planName = it
                project.virgo.assemblies."$assemblyName".extensions.create(planName, Plan, planName)
            }

            project.virgo.assemblies.extensions.create(assemblyName, VirgoAssemblyDescriptor
                    // constructor arguments
                    , assemblyName, project, repositories, plans)
        }

        project.extensions.create("virgo", VirgoExtension, assemblies)
    }

    /**
     * Auto-create tasks for each assembly configuration.
     */
    private void createTasksFor(Project project) {
        project.virgo.assemblies.all {
            def assemblyName_ = it.name
            def pascalCase = it.name[0].toUpperCase() + it.name.substring(1)
            def assemblyTaskName = "assemble" + pascalCase

            project.tasks.create(assemblyTaskName, VirgoAssemblyTask, {
                group "Assembly"
                description "Assembles a distribution on top of Eclipse Virgo."
                assemblyName assemblyName_
            })

            // hook them to the built-in "assemble" task
            def assembleTask = project.tasks.findByName("assemble")
            if (assembleTask != null) {
                project.logger.info(String.format("Found task 'assemble', making it depend on task '%s'.", assemblyTaskName))
                assembleTask.dependsOn assemblyTaskName
            } else {
                project.logger.info(String.format("No task named 'assemble' found, cannot make it depend on task '%s'.", assemblyTaskName))
            }
        }
    }
}
