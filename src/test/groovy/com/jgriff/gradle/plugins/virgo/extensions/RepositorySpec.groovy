package com.jgriff.gradle.plugins.virgo.extensions

import spock.lang.Specification


class RepositorySpec extends Specification {
    Repository sut

    def setup() {
        sut = new Repository()
    }

    def "default values"() {
        expect:
        "ext" == sut.getName()
        "external" == sut.getType()
        "repository/ext/{artifact}" == sut.getSearchPattern()
        "repository/ext" == sut.getWatchDirectory()
        -1 == sut.getWatchInterval()
        null == sut.getUri()
        -1 == sut.getIndexRefreshInterval()
    }

    def "default 'searchPattern' adjusts to changing repository name"() {
        expect:
        sut.setName(name)
        searchPattern == sut.getSearchPattern()

        where:
        name   | searchPattern
        "foo"  | "repository/foo/{artifact}"
        "bar"  | "repository/bar/{artifact}"
    }

    def "default 'watchDirectory' adjusts to changing repository name"() {
        expect:
        sut.setName(name)
        watchDirectory == sut.getWatchDirectory()

        where:
        name   | watchDirectory
        "foo"  | "repository/foo"
        "bar"  | "repository/bar"
    }

    def "validate() is OK with missing 'name' (uses default 'ext')"() {
        given:
        sut.setName(null)

        when:
        sut.validate()

        then:
        notThrown(AssertionError)
        "ext" == sut.getName()
    }

    def "validate() is OK with missing 'type' (uses default 'external')"() {
        given:
        sut.setType(null)

        when:
        sut.validate()

        then:
        notThrown(AssertionError)
        "external" == sut.getType()
    }

    def "validate() is OK with missing 'watchDirectory' for repository type 'watched' (uses default)"() {
        given:
        sut.type = "watched"
        sut.watchDirectory = null

        when:
        sut.validate()

        then:
        notThrown(AssertionError)
        "repository/ext" == sut.getWatchDirectory()
    }

    def "validate() is OK with missing 'watchInterval' for repository type 'watched'"() {
        given:
        sut.type = "watched"
        sut.watchDirectory = "foo"
        sut.watchInterval = -1

        when:
        sut.validate()

        then:
        notThrown(AssertionError)
    }

    def "validate() asserts missing 'uri' for repository type 'remote'"() {
        given:
        sut.type = "remote"
        sut.uri = null

        when:
        sut.validate()

        then:
        final AssertionError error = thrown()
        error.message.startsWith("Missing required 'uri' for repository type (remote).")
    }

    def "validate() is OK with missing 'indexRefreshInterval' for repository type 'remote'"() {
        given:
        sut.type = "remote"
        sut.uri = "foo"
        sut.indexRefreshInterval = -1

        when:
        sut.validate()

        then:
        notThrown(AssertionError)
    }
}
