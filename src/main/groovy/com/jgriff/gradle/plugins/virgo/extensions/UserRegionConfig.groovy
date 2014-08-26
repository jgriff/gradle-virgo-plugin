package com.jgriff.gradle.plugins.virgo.extensions

import com.jgriff.gradle.plugins.virgo.internal.Utils
import org.gradle.api.Project

/**
 * DSL extension for representing a virgo user region configuration.  Currently only
 * supports "initialArtifacts".
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class UserRegionConfig {
    final Project project
    InitialArtifacts initialArtifacts = new InitialArtifacts()

    UserRegionConfig() {}
    UserRegionConfig(Project project) { this.project = project }

    UserRegionConfig initialArtifact(Artifact initialArtifact) { initialArtifacts.artifact(initialArtifact); this }
    UserRegionConfig initialArtifacts(Closure closure) { project.configure(initialArtifacts, closure); this }
    UserRegionConfig initialArtifactsOverwrite(boolean overwrite) { initialArtifacts.overwrite(overwrite); this }
}

class InitialArtifacts extends ArtifactListing {
    boolean overwrite = false

    static InitialArtifacts parse(String str) {
        InitialArtifacts toReturn = new InitialArtifacts()
        if (Utils.hasLength(str)) {
            str.split(",").each {
                String[] parts = it.substring(it.toLowerCase().startsWith("repository:") ? "repository:".length() : 0).split("/")
                if (parts.length > 0) {
                    toReturn.artifact(new Artifact(
                            parts[0],
                            parts.length > 1 ? parts[1] : null,
                            parts.length > 2 ? parts[2] : null
                    ))
                }
            }
        }
        toReturn
    }

    void overwrite(Boolean overwrite) { this.overwrite = overwrite }

    public String toString() {
        StringBuffer buff = new StringBuffer()
        artifacts.each { Artifact artifact ->
            buff.append(",")
                .append("repository:")
                .append(artifact.type).append("/")
                .append(artifact.name);
            if (Utils.hasLength(artifact.version)) {
                buff.append("/").append(artifact.version.trim())
            }
        }

        return buff.length() == 0 ? "" : buff.toString().substring(1) // strip off first ","
    }
}
