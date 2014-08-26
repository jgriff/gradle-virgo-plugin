package com.jgriff.gradle.plugins.virgo.extensions

import com.jgriff.gradle.plugins.virgo.internal.Utils

/**
 * DSL extension for representing a virgo plan file.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class Plan extends ArtifactListing {
    String name
    String version
    def into
    boolean scoped
    boolean atomic

    Plan() {}
    Plan(String name) { this.name = name }

    Plan name(String name) { this.name = name; this }
    Plan version(String version) { this.version = version; this }
    Plan scoped(boolean scoped) { this.scoped = scoped; this }
    Plan atomic(boolean atomic) { this.atomic = atomic; this }

    String filename() {
        (Utils.hasLength(name) ? name.trim() : "") +
        (Utils.hasLength(name) && Utils.hasLength(version) ? "-" : "") +
        (Utils.hasLength(version) ? version.trim() : "") + ".plan"
    }

    boolean isVersionSet() {
        return Utils.hasLength(version)
    }

    @Override
    String defaultVersion() { version }
}
