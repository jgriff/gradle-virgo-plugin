package com.jgriff.gradle.plugins.virgo.internal

import com.jgriff.gradle.plugins.virgo.extensions.Artifact
import com.jgriff.gradle.plugins.virgo.extensions.InitialArtifacts
import com.jgriff.gradle.plugins.virgo.extensions.UserRegionConfig

/**
 * Reads, parses, and writes a file that can be used as Virgo's user region properties
 * file (which is usually located at "<code>configuration/org.eclipse.virgo.kernel.userregion.properties</code>").
 * <p>
 * Reading (aka, seeding) is done at construction time.
 * <p>
 * Updating is done via:
 * <ul>
 *     <li> {@link VirgoRepositoryPropertiesWriter#add(com.jgriff.gradle.plugins.virgo.extensions.Repository)} -
 *     Adds a repository configuration to be written.  This also appends the repository to the chain.
 *     <li> {@link VirgoRepositoryPropertiesWriter#chain(java.lang.String, boolean)} - Set (or append) to the "chain"
 *     property.  Useful for re-ordering, or otherwise controlling what is in the chain.
 * </ul>
 * <p>
 * Writing is done via {@link VirgoRepositoryPropertiesWriter#writeTo(java.io.File)}.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class VirgoUserRegionPropertiesWriter {
    private static final String INITIAL_ARTIFACTS = "initialArtifacts"
    private Properties props

    /**
     * Constructs this instance with an empty properties object (blank slate).
     *
     * @since 0.1
     */
    VirgoUserRegionPropertiesWriter() {
        props = new Properties()
    }

    /**
     * Constructs this instance by seeding it with the specified properties.
     *
     * @param seed The properties to use in this object (object retained by this object).
     * @since 0.1
     */
    VirgoUserRegionPropertiesWriter(Properties seed) {
        props = seed
    }

    /**
     * Constructs this instance by seeding it with the properties in the specified properties file.
     *
     * @param propertiesFile The properties file to load from
     * @since 0.1
     */
    VirgoUserRegionPropertiesWriter(File propertiesFile) {
        props = new Properties()
        props.load(propertiesFile.newDataInputStream())
    }

    /**
     * Adds a user region configuration.
     *
     * @param repo The configuration to add.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoUserRegionPropertiesWriter add(UserRegionConfig config) {
        addInitialArtifacts(config)
    }

    private VirgoUserRegionPropertiesWriter addInitialArtifacts(UserRegionConfig config) {
        // combine current prop value with this incoming list, and rewrite to props
        InitialArtifacts allInitialArtifacts =
                (config.initialArtifacts.overwrite ? new InitialArtifacts() : InitialArtifacts.parse(props.getProperty(INITIAL_ARTIFACTS)))
                .addAll(config.initialArtifacts.getArtifacts())

        String buff = allInitialArtifacts.toString()

        if (buff.length() > 0) {
            props.setProperty(INITIAL_ARTIFACTS, buff)
        } else {
            props.remove(INITIAL_ARTIFACTS)
        }

        this
    }

    /**
     * Writes the current state of the configuration properties in this object to the specified file.
     *
     * @param file The file to write to.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoUserRegionPropertiesWriter writeTo(File file) {
        if (file != null && props != null) {
            file.withWriter('UTF-8') { fileWriter ->
                // TODO pretty print this file for readability
                props.each { key, value ->
                    fileWriter.writeLine "$key=$value"
                }

                fileWriter.writeLine ''
            }
        }
    }
}
