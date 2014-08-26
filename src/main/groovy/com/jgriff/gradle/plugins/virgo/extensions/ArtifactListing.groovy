package com.jgriff.gradle.plugins.virgo.extensions

/**
 * DSL extension for representing a list of artifacts (plans, bundles, pars, configurations.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class ArtifactListing {
    List<Artifact> artifacts = new ArrayList<Artifact>()

    void bundle(Object obj) { artifact("bundle", obj) }

    void bundle(String str) { artifact("bundle", str) }

    void bundle(Object... objs) { artifacts("bundle", objs) }

    void bundle(String... strings) { artifacts("bundle", strings) }

    void configuration(Object obj) { artifact("configuration", obj) }

    void configuration(String str) { artifact("configuration", str) }

    void configuration(Object... objs) { artifacts("configuration", objs) }

    void configuration(String... strings) { artifacts("configuration", strings) }

    void plan(Object obj) { artifact("plan", obj) }

    void plan(String str) { artifact("plan", str) }

    void plan(Object... objs) { artifacts("plan", objs) }

    void plan(String... strings) { artifacts("plan", strings) }

    void par(Object obj) { artifact("par", obj) }

    void par(String str) { artifact("par", str) }

    void par(Object... objs) { artifacts("par", objs) }

    void par(String... strings) { artifacts("par", strings) }

    /**
     * Handles declaration formatted:
     * <pre>
     * bundle name: 'com.acme.bundle.one', version: '1.2.3.RELEASE'
     * </pre>
     */
    void artifact(String type, Object obj) {
        artifact(new Artifact(type, obj.name, obj.version != null ? obj.version : defaultVersion()))
    }
/**
     * Handles declaration formatted:
     * <pre>
     * bundle 'com.acme.bundle.one:1.2.3.RELEASE'
     * </pre>
     */
    void artifact(String type, String str) {
        def values = str.split(":")
        artifact(new Artifact(type, values.length > 0 ? values[0] : "", values.length > 1 ? values[1] : defaultVersion()))
    }

    /**
     * Handles declaration formatted:
     * <pre>
     * bundle(
     *       [name: 'com.acme.bundle.one', version: '1.2.3.RELEASE'],
     *       [name: 'com.acme.bundle.two', version: '1.2.3.RELEASE']
     *  )
     * </pre>
     */
    void artifacts(String type, Object... objs) {
        objs.each {
            artifact(type, it)
        }
    }

    /**
     * Handles declaration formatted:
     * <pre>
     * bundle 'com.acme.bundle.one:1.2.3.ONE',
     *        'com.acme.bundle.two:1.2.3.TWO'
     * </pre>
     */
    void artifacts(String type, String... bundles) {
        bundles.each { artifact(type, it) }
    }

    String defaultVersion() { "" }

    ArtifactListing artifact(Artifact artifact) {
        // first, see if we already have a matching type/name for this incoming artifact
        Artifact existing = artifacts.find { it.type.trim().equalsIgnoreCase(artifact.type.trim()) && it.name.trim().equalsIgnoreCase(artifact.name.trim()) }

        if (existing != null) {
            existing.setVersion(artifact.getVersion())
        } else {
            artifacts.add(artifact)
        }
        this
    }

    ArtifactListing addAll(Collection<Artifact> artifacts) {
        artifacts.each {
            artifact(it)
        }
        this
    }
}

class Artifact {
    String type
    String name
    String version

    Artifact() { }

    Artifact(String name) {
        this.name = name
    }

    Artifact(String type, String name) {
        this(name)
        this.type = type
    }

    Artifact(String type, String name, String version) {
        this(type, name)
        this.version = version
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
