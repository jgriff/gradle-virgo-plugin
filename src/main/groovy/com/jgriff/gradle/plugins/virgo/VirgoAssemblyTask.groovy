package com.jgriff.gradle.plugins.virgo

import com.jgriff.gradle.plugins.virgo.extensions.Plan
import com.jgriff.gradle.plugins.virgo.extensions.Repository
import com.jgriff.gradle.plugins.virgo.extensions.VirgoAssemblyDescriptor
import com.jgriff.gradle.plugins.virgo.internal.Virgo
import com.jgriff.gradle.plugins.virgo.internal.VirgoPlanWriter
import com.jgriff.gradle.plugins.virgo.internal.VirgoRepositoryPropertiesWriter
import com.jgriff.gradle.plugins.virgo.internal.VirgoUserRegionPropertiesWriter
import org.gradle.api.tasks.Copy

/**
 * Extension of the {@link Copy} task that adds custom support for assembling
 * a directory containing Virgo.  The copy task's execution happens first,
 * then our custom logic happens on top of the target directory.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class VirgoAssemblyTask extends Copy {
    def assemblyName

    public VirgoAssemblyTask() {
        super()
        project.afterEvaluate { getDescriptor().applyTo(this) }
    }

    public VirgoAssemblyDescriptor getDescriptor() { project.virgo.assemblies.findByName(assemblyName) }

    @Override
    protected void copy() {
        logger.info("VirgoAssembly task running for '" + assemblyName + "'...")
        super.copy()

        VirgoAssemblyDescriptor descriptor = getDescriptor();

        handleRepositories(descriptor)
        handlePlans(descriptor)
        handleUserRegion(descriptor)
    }

    private void handleRepositories(VirgoAssemblyDescriptor descriptor) {
        if (descriptor.repositories != null) {

            final File configFile = new File(project.file(descriptor.into), Virgo.REPOSITORY_CONFIG)
            VirgoRepositoryPropertiesWriter propertiesWriter
            if (configFile.exists()) {
                propertiesWriter = new VirgoRepositoryPropertiesWriter(configFile)
            } else {
                propertiesWriter = new VirgoRepositoryPropertiesWriter()
                configFile.parentFile.mkdirs()
                configFile.createNewFile()
            }

            final String originalChain = propertiesWriter.property("chain")
            final String newChain = descriptor.getRepositoryChain()

            descriptor.repositories.all { Repository repo ->
                // repo config
                logger.info(String.format("Capturing repository configuration for repository '%s' ('%s').", repo.getName(), repo.getType()))
                propertiesWriter.add(repo)

                // repo content
                if (repo.artifacts != null) {
                    File target = new File(new File(project.file(descriptor.into), "repository"), repo.name);
                    target.mkdirs()
                    logger.info(String.format("Populating repository: %s", target.getAbsolutePath()))
                    project.copy( {
                        into target
                        with repo.artifacts
                    })
                } else if (!"remote".equalsIgnoreCase(repo.type)) {
                    logger.warn(String.format("No 'artifacts' specified for Virgo assembly '%s' in repository '%s'.", descriptor.name, repo.name))
                }
            }

            if (newChain != null) {
                logger.info(String.format("Explicitly setting the repository chain to '%s' (overwrite=%b).", newChain, descriptor.isRepositoryChainOverwrite()))
                // we are being explicit about the chain, reset it to it's original state and apply
                propertiesWriter.chain(originalChain != null ? originalChain : "", true)
                propertiesWriter.chain(newChain, descriptor.isRepositoryChainOverwrite())
            }

            logger.info(String.format("Writing out repository configuration for assembly '%s' to '%s' using: %s", descriptor.getName(), configFile.getAbsolutePath(), propertiesWriter.toString()))
            propertiesWriter.writeTo(configFile)
        } else {
            logger.info(String.format("No repositories specified for Virgo assembly '%s'.", descriptor.name))
        }
    }

    private void handlePlans(VirgoAssemblyDescriptor descriptor) {
        if (descriptor.plans != null) {
            // by default, we drop plans into /pickup
            final File defaultPlanDir = new File(project.file(descriptor.into), Virgo.PICKUP_DIR)
            defaultPlanDir.mkdirs()

            descriptor.plans.all { Plan plan ->
                // if we need to default the plan's version to project.version
                if (!plan.isVersionSet()) { plan.version = project.version }

                // figure out the target dir to drop the plan
                File targetDir = defaultPlanDir
                if (plan.into != null) {
                    // alternate directory specified
                    targetDir = new File(project.file(descriptor.into), plan.into)
                    targetDir.mkdirs()
                }
                File planFile = new File(targetDir, plan.filename())
                planFile.createNewFile()

                logger.info(String.format("Writing plan file '%s' into '%s'.", plan.filename(), defaultPlanDir.getAbsolutePath()))
                new VirgoPlanWriter().plan(plan).writeTo(planFile)
            }
        } else {
            logger.info(String.format("No plan files specified for Virgo assembly '%s'.", descriptor.name))
        }
    }

    private void handleUserRegion(VirgoAssemblyDescriptor descriptor) {
        if (descriptor.userRegion == null) return

        final File userRegionConfigFile = new File(project.file(descriptor.into), Virgo.USER_REGION_CONFIG)
        VirgoUserRegionPropertiesWriter writer = userRegionConfigFile.exists() ? new VirgoUserRegionPropertiesWriter(userRegionConfigFile) : new VirgoUserRegionPropertiesWriter()
        if (!userRegionConfigFile.exists()) {
            userRegionConfigFile.createNewFile()
        }

        writer.add(descriptor.userRegion).writeTo(userRegionConfigFile)
    }
}
