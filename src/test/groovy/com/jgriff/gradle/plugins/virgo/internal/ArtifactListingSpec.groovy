package com.jgriff.gradle.plugins.virgo.internal

import com.jgriff.gradle.plugins.virgo.extensions.Artifact
import com.jgriff.gradle.plugins.virgo.extensions.ArtifactListing
import spock.lang.Specification

class ArtifactListingSpec extends Specification {
    ArtifactListing sut

    def setup() {
        sut = new ArtifactListing()
    }

    def "update version when duplicate type/name is encountered"() {
        given: sut.artifact(new Artifact("bundle", "com.acme", "1.0"))

        expect:
        sut.artifact(update)
        sut.artifacts.size() == size
        sut.artifacts.get(artfactAt).type == type
        sut.artifacts.get(artfactAt).name == name
        sut.artifacts.get(artfactAt).version == version


        where:
        update                                                      | size | artfactAt | type     | name          | version
        // when type and name match, any new version will overwrite existing
        new Artifact("bundle", "com.acme", "1.0.UPDATED")           | 1    | 0         | "bundle" | "com.acme"    | "1.0.UPDATED"

        // matches ignore case
        new Artifact("bundle", "COM.ACME", "1.0.UPDATED")           | 1    | 0         | "bundle" | "com.acme"    | "1.0.UPDATED"
        new Artifact("BUNDLE", "com.acme", "1.0.UPDATED")           | 1    | 0         | "bundle" | "com.acme"    | "1.0.UPDATED"
        // ...and leading and trailing whitespace
        new Artifact("  bundle  ", "com.acme", "1.0.UPDATED")       | 1    | 0         | "bundle" | "com.acme"    | "1.0.UPDATED"

        // if the new one doesn't have a version, the existing's version is cleared
        new Artifact("bundle", "com.acme")                          | 1    | 0         | "bundle" | "com.acme"    | null

        // merges only occur if type and name match, otherwise the new artifact is added
        new Artifact("plan", "com.acme", "1.0.PLAN")                | 2    | 0         | "bundle" | "com.acme"    | "1.0"
        new Artifact("plan", "com.acme", "1.0.PLAN")                | 2    | 1         | "plan"   | "com.acme"    | "1.0.PLAN"

    }
}
