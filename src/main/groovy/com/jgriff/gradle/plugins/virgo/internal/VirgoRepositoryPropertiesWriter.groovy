package com.jgriff.gradle.plugins.virgo.internal

import com.jgriff.gradle.plugins.virgo.extensions.Repository

/**
 * Reads, parses, and writes a file that can be used as Virgo's repository configuration properties
 * file (which is usually located at "<code>configuration/org.eclipse.virgo.repository.properties</code>").
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
class VirgoRepositoryPropertiesWriter {
    private Properties props

    /**
     * Constructs this instance with an empty properties object (blank slate).
     *
     * @since 0.1
     */
    VirgoRepositoryPropertiesWriter() {
        props = new Properties()
    }

    /**
     * Constructs this instance by seeding it with the specified properties.
     *
     * @param seed The properties to use in this object (object retained by this object).
     * @since 0.1
     */
    VirgoRepositoryPropertiesWriter(Properties seed) {
        props = seed
    }

    /**
     * Constructs this instance by seeding it with the properties in the specified properties file.
     *
     * @param propertiesFile The properties file to load from
     * @since 0.1
     */
    VirgoRepositoryPropertiesWriter(File propertiesFile) {
        props = new Properties()
        props.load(propertiesFile.newDataInputStream())
    }

    /**
     * Adds a repository configuration.  This also implicitly appends the repository to the {@link #chain(java.lang.String, boolean) chain}.
     *
     * @param repo The configuration to add.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoRepositoryPropertiesWriter add(Repository repo) {
        repo.validate()
        final String name = repo.getName().toLowerCase()
        final String type = repo.getType().toLowerCase()

        // what kind of repo is this?
        switch(type) {
            case "external":
                props.setProperty(name + ".type", "external")
                props.setProperty(name + ".searchPattern", repo.getSearchPattern())
                break
            case "watched":
                props.setProperty(name + ".type", "watched")
                props.setProperty(name + ".watchDirectory", repo.getWatchDirectory())
                int interval = repo.getWatchInterval()
                if (interval > -1) {
                    props.setProperty(name + ".watchInterval", String.valueOf(interval))
                }
                break
            case "remote":
                props.setProperty(name + ".type", "remote")
                props.setProperty(name + ".uri", repo.getUri())
                int interval = repo.getIndexRefreshInterval()
                if (interval > -1) {
                    props.setProperty(name + ".indexRefreshInterval", String.valueOf(interval))
                }
                break;
        }

        appendToChain(name)
        this
    }

    /**
     * Set the "chain" property.
     *
     * @param chain The value to set for the chain.
     * @param overwrite Whether to overwrite the chain (<code>true</code>) or to append to any existing value (<code>false</code>)
     * any existing value with the one you supply.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoRepositoryPropertiesWriter chain(String chain, boolean overwrite) {
        if (overwrite) {
            props.setProperty("chain", Utils.hasLength(chain) ? chain.trim() : "")
        } else {
            chain.tokenize(",").each { appendToChain(it) }
        }
        this
    }

    /**
     * Retrieves a property from the underlying {@link Properties} store (which is written
     * out during {@link VirgoRepositoryPropertiesWriter#writeTo(java.io.File)}).
     *
     * @param key The property to retrieve.
     * @return The value of the property, or <code>null</code> if it doesn't exist.
     * @since 0.1
     */
    String property(String key) { props.getProperty(key) }

    /**
     * Writes the current state of the configuration properties in this object to the specified file.
     *
     * @param file The file to write to.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoRepositoryPropertiesWriter writeTo(File file) {
        if (file != null && props != null) {
            file.withWriter('UTF-8') { fileWriter ->
                // sort without "chain" (leaving it for last)
                final def chainValue = props.remove("chain")
                def sorted = props.sort()*.key

                sorted.each { key ->
                    fileWriter.writeLine "$key=" + props.getProperty(key)
                }

                if (chainValue != null) {
                    fileWriter.writeLine "chain=" + chainValue
                }

                fileWriter.writeLine ''
            }
        }
    }

    private VirgoRepositoryPropertiesWriter appendToChain(String repo) {
        String chain = props.getProperty("chain")
        if (chain != null && !chain.trim().isEmpty()) {
            if (null == chain.tokenize(",").find { it.trim().equalsIgnoreCase(repo) }) {
                // didn't find it, append it now
                chain = chain + "," + repo
            }
        } else {
            chain = repo // first in the list
        }

        props.setProperty("chain", chain)
        this
    }

    @Override
    public String toString() {
        return "VirgoRepositoryPropertiesWriter{" +
                "props=" + props +
                '}';
    }
}
