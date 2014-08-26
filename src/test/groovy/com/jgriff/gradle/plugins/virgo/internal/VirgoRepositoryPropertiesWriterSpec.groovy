package com.jgriff.gradle.plugins.virgo.internal

import com.jgriff.gradle.plugins.virgo.extensions.Repository
import com.jgriff.gradle.plugins.virgo.TemporaryFolderSpec

class VirgoRepositoryPropertiesWriterSpec extends TemporaryFolderSpec {

    File getRepoProps() {
        file("repository.properties")
    }

    def "write nothing"() {
        given: "existing content in properties file"
        repoProps << "foo=bar"
        VirgoRepositoryPropertiesWriter sut = new VirgoRepositoryPropertiesWriter(repoProps)

        when: "nothing to write"
        sut.writeTo(repoProps)

        then: "original file remains unchanged"
        repoProps.text.trim() == "foo=bar"
    }

    def "write from scratch"() {
        when: "a writer flushes new repositories to a new file"
        new VirgoRepositoryPropertiesWriter()
            .add(new Repository()
                .setName("my-external"))
            .add(new Repository()
                .setName("my-watched")
                .setType("watched"))
            .add(new Repository()
                .setName("my-remote")
                .setType("remote")
                .setUri("http://localhost/some/uri"))
            .writeTo(repoProps)

        then: "resulting properties file new properties"
        Properties resultingProps = new Properties()
        resultingProps.load(repoProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                              | value
        "my-external.type"               | "external"
        "my-external.searchPattern"      | "repository/my-external/{artifact}"
        "my-watched.type"                | "watched"
        "my-watched.watchDirectory"      | "repository/my-watched"
        "my-watched.watchInterval"       | null
        "my-remote.type"                 | "remote"
        "my-remote.uri"                  | "http://localhost/some/uri"
        "my-remote.indexRefreshInterval" | null
        "chain"                          | "my-external,my-watched,my-remote"
    }

    def "write new repositories (minimal)"() {
        given: "file contains existing repository configuration"
        repoProps  <<
                "ext.type=external\n" +
                "ext.searchPattern=repository/ext/{artifact}\n" +
                "usr.type=watched\n" +
                "usr.watchDirectory=repository/usr\n" +
                "chain=ext,usr"

        when: "a writer flushes new repositories to the file"
        new VirgoRepositoryPropertiesWriter(repoProps)
            .add(new Repository()
                .setName("my-external"))
            .add(new Repository()
                .setName("my-watched")
                .setType("watched"))
            .add(new Repository()
                .setName("my-remote")
                .setType("remote")
                .setUri("http://localhost/some/uri"))
            .writeTo(repoProps)

        then: "resulting properties file is a combination of existing + new"
        Properties resultingProps = new Properties()
        resultingProps.load(repoProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                              | value
        "ext.type"                       | "external"
        "ext.searchPattern"              | "repository/ext/{artifact}"
        "usr.type"                       | "watched"
        "usr.watchDirectory"             | "repository/usr"
        "my-external.type"               | "external"
        "my-external.searchPattern"      | "repository/my-external/{artifact}"
        "my-watched.type"                | "watched"
        "my-watched.watchDirectory"      | "repository/my-watched"
        "my-watched.watchInterval"       | null
        "my-remote.type"                 | "remote"
        "my-remote.uri"                  | "http://localhost/some/uri"
        "my-remote.indexRefreshInterval" | null
        "chain"                          | "ext,usr,my-external,my-watched,my-remote"
    }

    def "write new repositories (full)"() {
        given: "file contains existing repository configuration"
        repoProps  <<
                "ext.type=external\n" +
                "ext.searchPattern=repository/ext/{artifact}\n" +
                "usr.type=watched\n" +
                "usr.watchDirectory=repository/usr\n" +
                "chain=ext,usr"

        when: "a writer flushes new repositories to the file"
        new VirgoRepositoryPropertiesWriter(repoProps)
            .add(new Repository()
                .setName("my-external")
                .setType("external")
                .setSearchPattern("external/foo"))
            .add(new Repository()
                .setName("my-watched")
                .setType("watched")
                .setWatchDirectory("watched/foo")
                .setWatchInterval(3))
            .add(new Repository()
                .setName("my-remote")
                .setType("remote")
                .setUri("http://localhost/some/uri")
                .setIndexRefreshInterval(4))
            .writeTo(repoProps)

        then: "resulting properties file is a combination of existing + new"
        Properties resultingProps = new Properties()
        resultingProps.load(repoProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                              | value
        "ext.type"                       | "external"
        "ext.searchPattern"              | "repository/ext/{artifact}"
        "usr.type"                       | "watched"
        "usr.watchDirectory"             | "repository/usr"
        "my-external.type"               | "external"
        "my-external.searchPattern"      | "external/foo"
        "my-watched.type"                | "watched"
        "my-watched.watchDirectory"      | "watched/foo"
        "my-watched.watchInterval"       | "3"
        "my-remote.type"                 | "remote"
        "my-remote.uri"                  | "http://localhost/some/uri"
        "my-remote.indexRefreshInterval" | "4"
        "chain"                          | "ext,usr,my-external,my-watched,my-remote"
    }

    def "overwrite existing repository configurations"() {
        given: "file contains existing repository configuration"
        repoProps  <<
                "ext.type=external\n" +
                "ext.searchPattern=repository/ext/{artifact}\n" +
                "usr.type=watched\n" +
                "usr.watchDirectory=repository/usr\n" +
                "usr.watchInterval=3\n" +
                "remote.type=remote\n" +
                "remote.uri=http://localhost/some/uri\n" +
                "remote.indexRefreshInterval=10\n" +
                "chain=ext,usr,remote"

        when: "a writer flushes repository names that overwrite existing names"
        new VirgoRepositoryPropertiesWriter(repoProps)
            .add(new Repository()
                .setName("ext")
                .setSearchPattern("repository/ext-2/{artifact}"))
            .add(new Repository()
                .setName("usr")
                .setType("watched")
                .setWatchDirectory("repository/usr-2")
                .setWatchInterval(4))
            .add(new Repository()
                .setName("remote")
                .setType("remote")
                .setUri("http://localhost/some/other/uri")
                .setIndexRefreshInterval(11))
            .writeTo(repoProps)

        then: "resulting properties file will be overwritten"
        Properties resultingProps = new Properties()
        resultingProps.load(repoProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                              | value
        "ext.type"                       | "external"
        "ext.searchPattern"              | "repository/ext-2/{artifact}"
        "usr.type"                       | "watched"
        "usr.watchDirectory"             | "repository/usr-2"
        "usr.watchInterval"              | "4"
        "remote.type"                    | "remote"
        "remote.uri"                     | "http://localhost/some/other/uri"
        "remote.indexRefreshInterval"    | "11"
        "chain"                          | "ext,usr,remote"
    }

    def "write 'chain' only (append)"() {
        given: "file contains existing repository configuration"
        repoProps  <<
                "ext.type=external\n" +
                "ext.searchPattern=repository/ext/{artifact}\n" +
                "usr.type=watched\n" +
                "usr.watchDirectory=repository/usr\n" +
                "chain=ext,usr"

        when: "a writer overwrites the 'chain'"
        new VirgoRepositoryPropertiesWriter(repoProps)
            .chain("foo,bar", false)
            .writeTo(repoProps)

        then: "resulting properties file has the overwritten 'chain' value"
        Properties resultingProps = new Properties()
        resultingProps.load(repoProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                          | value
        "ext.type"                   | "external"
        "ext.searchPattern"          | "repository/ext/{artifact}"
        "usr.type"                   | "watched"
        "usr.watchDirectory"         | "repository/usr"
        "chain"                      | "ext,usr,foo,bar"
    }

    def "write 'chain' only (overwrite)"() {
        given: "file contains existing repository configuration"
        repoProps  <<
                "ext.type=external\n" +
                "ext.searchPattern=repository/ext/{artifact}\n" +
                "usr.type=watched\n" +
                "usr.watchDirectory=repository/usr\n" +
                "chain=ext,usr"

        when: "a writer overwrites the 'chain'"
        new VirgoRepositoryPropertiesWriter(repoProps)
            .chain("foo,bar", true)
            .writeTo(repoProps)

        then: "resulting properties file has the overwritten 'chain' value"
        Properties resultingProps = new Properties()
        resultingProps.load(repoProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                          | value
        "ext.type"                   | "external"
        "ext.searchPattern"          | "repository/ext/{artifact}"
        "usr.type"                   | "watched"
        "usr.watchDirectory"         | "repository/usr"
        "chain"                      | "foo,bar"
    }

    def "overwrite 'chain' to empty string"() {
        given: "file contains existing repository configuration"
        repoProps  <<
                "ext.type=external\n" +
                "ext.searchPattern=repository/ext/{artifact}\n" +
                "usr.type=watched\n" +
                "usr.watchDirectory=repository/usr\n" +
                "chain=ext,usr"

        when: "a writer overwrites the 'chain' but omits the chain value"
        new VirgoRepositoryPropertiesWriter(repoProps)
            .chain(null, true)
            .writeTo(repoProps)

        then: "resulting properties file has the overwritten 'chain' value as empty string"
        Properties resultingProps = new Properties()
        resultingProps.load(repoProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                          | value
        "ext.type"                   | "external"
        "ext.searchPattern"          | "repository/ext/{artifact}"
        "usr.type"                   | "watched"
        "usr.watchDirectory"         | "repository/usr"
        "chain"                      | ""
    }

    def "repository properties file written alphabetically"() {
        given: "file contains existing repository configuration"
        repoProps  <<
                "ext.type=external\n" +
                "ext.searchPattern=repository/ext/{artifact}\n" +
                "usr.type=watched\n" +
                "usr.watchDirectory=repository/usr\n" +
                "chain=ext,usr"

        when: "a writer flushes new repositories to the file"
        new VirgoRepositoryPropertiesWriter(repoProps)
            .add(new Repository()
                .setName("my-external"))
            .writeTo(repoProps)

        then: "resulting properties file listed alphabetically, whith 'chain' always at the end"
        List<String> lines = repoProps.text.trim().readLines();
        int idx = 0;
        assert lines[idx++].startsWith("ext.searchPattern=")
        assert lines[idx++].startsWith("ext.type=")
        assert lines[idx++].startsWith("my-external.searchPattern=")
        assert lines[idx++].startsWith("my-external.type=")
        assert lines[idx++].startsWith("usr.type=")
        assert lines[idx++].startsWith("usr.watchDirectory=")
        assert lines[idx++].startsWith("chain=")
    }
}
