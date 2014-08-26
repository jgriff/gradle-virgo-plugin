package com.jgriff.gradle.plugins.virgo.extensions

import com.jgriff.gradle.plugins.virgo.internal.CopySpecRecorder
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.CopySpec

/**
 * Extension to the DSL for expressing an assembly of Eclipse Virgo.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class VirgoAssemblyDescriptor extends CopySpecRecorder {
    final String name
    final Project project

    String repositoryChain
    boolean repositoryChainOverwrite = false
    NamedDomainObjectContainer<Repository> repositories
    NamedDomainObjectContainer<Plan> plans
    UserRegionConfig userRegion

    VirgoAssemblyDescriptor(String name, Project project, NamedDomainObjectContainer<Repository> repositories, NamedDomainObjectContainer<Plan> plans) {
        this.name = name
        this.project = project
        this.repositories = repositories
        this.plans = plans
    }

    void repositories(Closure closure) {
        repositories.configure(closure)
    }

    void plans(Closure closure) {
        plans.configure(closure)
    }

    void userRegion(Closure closure) {
        userRegion = new UserRegionConfig(project)
        project.configure(userRegion, closure)
    }

    @Override
    CopySpecRecorder applyTo(CopySpec target) {
        // before we apply, make sure we have an 'into' (or default it)
        if (into == null && intoTwoArgs == null) {
            into = new File(project.buildDir, 'virgo/' + name)
            project.logger.info(String.format("No destination directory specified for Virgo assembly %s, using default destination directory: %s", name, String.valueOf(into)))
        }
        return super.applyTo(target)
    }
}
