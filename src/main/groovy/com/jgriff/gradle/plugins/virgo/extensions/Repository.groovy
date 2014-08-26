package com.jgriff.gradle.plugins.virgo.extensions

/**
 * DSL extension for representing a virgo repository.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class Repository {
    private String name = "ext"
    private String type = "external"
    private String searchPattern
    private String watchDirectory
    private int watchInterval = -1
    private String uri
    private int indexRefreshInterval = -1

    public Repository(String name) {
        this.name = name
    }

    /**
     * A CopySpec defining the artifacts to include in this repository.
     */
    def artifacts

    String getName() { thisOrDefault(name, "ext") }

    private String thisOrDefault(String thisStr, String defaultStr) { thisStr != null && !thisStr.trim().isEmpty() ? thisStr : defaultStr }

    Repository setName(String name) {
        this.name = name
        this
    }

    String getType() { thisOrDefault(type, "external") }

    Repository setType(String type) {
        this.type = type
        this
    }

    String getSearchPattern() { thisOrDefault(searchPattern, "repository/" + getName() + "/{artifact}") }

    Repository setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern
        this
    }

    String getWatchDirectory() { thisOrDefault(watchDirectory, "repository/" + getName()) }

    Repository setWatchDirectory(String watchDirectory) {
        this.watchDirectory = watchDirectory
        this
    }

    int getWatchInterval() { watchInterval }

    Repository setWatchInterval(int watchInterval) {
        this.watchInterval = watchInterval
        this
    }

    String getUri() { uri }

    Repository setUri(String uri) {
        this.uri = uri
        this
    }

    int getIndexRefreshInterval() { indexRefreshInterval }

    Repository setIndexRefreshInterval(int indexRefreshInterval) {
        this.indexRefreshInterval = indexRefreshInterval
        this
    }

    void validate() {
        final String name = getName()
        final String type = getType()

        assert name != null && !name.trim().isEmpty(), "Missing required 'name'"
        assert type != null && !type.trim().isEmpty(), "Missing required 'type' (external/watched/remote)"

        switch (type) {
            case "external":
                def sp = getSearchPattern()
                assert sp != null && !sp.trim().isEmpty(), "Missing required 'searchPattern' for repository type (external)"
                break
            case "watched":
                def wd = getWatchDirectory()
                assert wd != null && !wd.trim().isEmpty(), "Missing required 'watchDirectory' for repository type (watched)"
                break
            case "remote":
                def uri = getUri()
                assert uri != null && !uri.trim().isEmpty(), "Missing required 'uri' for repository type (remote)"
                break
        }
    }


    @Override
    public String toString() {
        return "Repository{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", searchPattern='" + searchPattern + '\'' +
                ", watchDirectory='" + watchDirectory + '\'' +
                ", watchInterval=" + watchInterval +
                ", uri='" + uri + '\'' +
                ", indexRefreshInterval=" + indexRefreshInterval +
                ", artifacts=" + artifacts +
                '}';
    }
}
