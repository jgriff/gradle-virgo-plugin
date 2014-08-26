package com.jgriff.gradle.plugins.virgo.integ

import com.jgriff.gradle.plugins.virgo.internal.Virgo

class VirgoAssemblyRepositoryIntegSpec extends IntegSpec {
    def setup() {
        buildFile << applyPlugin("'com.github.jgriff.virgo.assembly'")
    }

    def "from scratch"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        repositories {
                            stdExt {
                                name 'ext' // note: you cannot name the closure "ext" b/c that's eaten by gradle as "extension properties" (took a while to figure that one out!)
                                artifacts {
                                    from 'build/add-to-ext'
                                }
                            }
                            myext {
                                artifacts {
                                    from 'build/add-to-my-ext'
                                }
                            }
                            mywatched {
                                type 'watched'
                                artifacts {
                                    from 'build/add-to-my-watched'
                                }
                            }
                            myremote {
                                type 'remote'
                                uri 'http://somewhere.net/repository'
                            }
                        }
                        repositoryChain 'ext,myext,mywatched,myremote' // otherwise they're listed alphabetical (gradle doesn't preserve order)
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"

        and: "our bundles we're adding"
        file("build/add-to-ext/bundle-foo-1.0.0.jar") << "bundle foo stuff"
        file("build/add-to-my-ext/bundle-bar-1.0.0.jar") << "bundle bar stuff"
        file("build/add-to-my-watched/bundle-baz-1.0.0.jar") << "bundle baz stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our assembled directory now contains"
        fileExists("build/virgo-assembled/repository/ext/bundle-foo-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/myext/bundle-bar-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/mywatched/bundle-baz-1.0.0.jar")

        fileDoesNotExist("build/virgo-assembled/repository/myext/bundle-foo-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myext/bundle-baz-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/mywatched/bundle-foo-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/mywatched/bundle-bar-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myremote")

        then: "our repository config file contains"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.REPOSITORY_CONFIG).newDataInputStream())

        repoProps.getProperty("ext.type") == "external"
        repoProps.getProperty("ext.searchPattern") == "repository/ext/{artifact}"
        repoProps.getProperty("myext.type") == "external"
        repoProps.getProperty("myext.searchPattern") == "repository/myext/{artifact}"
        repoProps.getProperty("mywatched.type") == "watched"
        repoProps.getProperty("mywatched.watchDirectory") == "repository/mywatched"
        repoProps.getProperty("myremote.type") == "remote"
        repoProps.getProperty("myremote.uri") == "http://somewhere.net/repository"
        repoProps.getProperty("chain") == "ext,myext,mywatched,myremote"
    }

    def "with existing repositories"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        repositories {
                            stdExt {
                                name 'ext' // note: you cannot name the closure "ext" b/c that's eaten by gradle as "extension properties" (took a while to figure that one out!)
                                artifacts {
                                    from 'build/add-to-ext'
                                }
                            }
                            myext {
                                artifacts {
                                    from 'build/add-to-my-ext'
                                }
                            }
                            mywatched {
                                type 'watched'
                                artifacts {
                                    from 'build/add-to-my-watched'
                                }
                            }
                            myremote {
                                type 'remote'
                                uri 'http://somewhere.net/repository'
                            }
                        }
                        repositoryChain 'myext,mywatched,myremote' // otherwise they're appended alphabetical (gradle doesn't preserve order)
                    }
                }
            }
        """

        and: "existing repositories"
        file(("build/virgo-unzipped/" + Virgo.REPOSITORY_CONFIG)) << """
                ext.type=external
                ext.searchPattern=repository/ext/{artifact}
                usr.type=watched
                usr.watchDirectory=repository/usr
                chain=ext,usr
        """
        file("build/virgo-unzipped/repository/ext/bundle-abc-1.0.0.jar") << "bundle abc stuff"
        file("build/virgo-unzipped/repository/usr/bundle-def-1.0.0.jar") << "bundle def stuff"

        and: "our bundles we're adding"
        file("build/add-to-ext/bundle-foo-1.0.0.jar") << "bundle foo stuff"
        file("build/add-to-my-ext/bundle-bar-1.0.0.jar") << "bundle bar stuff"
        file("build/add-to-my-watched/bundle-baz-1.0.0.jar") << "bundle baz stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our assembled directory now contains"
        fileExists("build/virgo-assembled/repository/ext/bundle-abc-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/usr/bundle-def-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/ext/bundle-foo-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/myext/bundle-bar-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/mywatched/bundle-baz-1.0.0.jar")

        fileDoesNotExist("build/virgo-assembled/repository/ext/bundle-bar-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/ext/bundle-baz-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myext/bundle-foo-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myext/bundle-baz-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/mywatched/bundle-foo-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/mywatched/bundle-bar-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myremote")

        then: "our repository config file contains"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.REPOSITORY_CONFIG).newDataInputStream())

        repoProps.getProperty("ext.type") == "external"
        repoProps.getProperty("ext.searchPattern") == "repository/ext/{artifact}"
        repoProps.getProperty("usr.type") == "watched"
        repoProps.getProperty("usr.watchDirectory") == "repository/usr"
        repoProps.getProperty("myext.type") == "external"
        repoProps.getProperty("myext.searchPattern") == "repository/myext/{artifact}"
        repoProps.getProperty("mywatched.type") == "watched"
        repoProps.getProperty("mywatched.watchDirectory") == "repository/mywatched"
        repoProps.getProperty("myremote.type") == "remote"
        repoProps.getProperty("myremote.uri") == "http://somewhere.net/repository"
        repoProps.getProperty("chain") == "ext,usr,myext,mywatched,myremote"
    }

    def "overwrite chain"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        repositories {
                            stdExt {
                                name 'ext' // note: you cannot name the closure "ext" b/c that's eaten by gradle as "extension properties" (took a while to figure that one out!)
                                artifacts {
                                    from 'build/add-to-ext'
                                }
                            }
                            myext {
                                artifacts {
                                    from 'build/add-to-my-ext'
                                }
                            }
                            mywatched {
                                type 'watched'
                                artifacts {
                                    from 'build/add-to-my-watched'
                                }
                            }
                            myremote {
                                type 'remote'
                                uri 'http://somewhere.net/repository'
                            }
                        }
                        repositoryChain 'myext,mywatched,myremote' // otherwise they're listed alphabetical (gradle doesn't preserve order)
                        repositoryChainOverwrite true
                    }
                }
            }
        """

        and: "existing repositories"
        file(("build/virgo-unzipped/" + Virgo.REPOSITORY_CONFIG)) << """
                ext.type=external
                ext.searchPattern=repository/ext/{artifact}
                usr.type=watched
                usr.watchDirectory=repository/usr
                chain=ext,usr
        """
        file("build/virgo-unzipped/repository/ext/bundle-abc-1.0.0.jar") << "bundle abc stuff"
        file("build/virgo-unzipped/repository/usr/bundle-def-1.0.0.jar") << "bundle def stuff"

        and: "our bundles we're adding"
        file("build/add-to-ext/bundle-foo-1.0.0.jar") << "bundle foo stuff"
        file("build/add-to-my-ext/bundle-bar-1.0.0.jar") << "bundle bar stuff"
        file("build/add-to-my-watched/bundle-baz-1.0.0.jar") << "bundle baz stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our assembled directory now contains"
        fileExists("build/virgo-assembled/repository/ext/bundle-abc-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/usr/bundle-def-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/ext/bundle-foo-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/myext/bundle-bar-1.0.0.jar")
        fileExists("build/virgo-assembled/repository/mywatched/bundle-baz-1.0.0.jar")

        fileDoesNotExist("build/virgo-assembled/repository/ext/bundle-bar-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/ext/bundle-baz-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myext/bundle-foo-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myext/bundle-baz-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/mywatched/bundle-foo-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/mywatched/bundle-bar-1.0.0.jar")
        fileDoesNotExist("build/virgo-assembled/repository/myremote")

        then: "our repository config file contains"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.REPOSITORY_CONFIG).newDataInputStream())

        repoProps.getProperty("ext.type") == "external"
        repoProps.getProperty("ext.searchPattern") == "repository/ext/{artifact}"
        repoProps.getProperty("usr.type") == "watched"
        repoProps.getProperty("usr.watchDirectory") == "repository/usr"
        repoProps.getProperty("myext.type") == "external"
        repoProps.getProperty("myext.searchPattern") == "repository/myext/{artifact}"
        repoProps.getProperty("mywatched.type") == "watched"
        repoProps.getProperty("mywatched.watchDirectory") == "repository/mywatched"
        repoProps.getProperty("myremote.type") == "remote"
        repoProps.getProperty("myremote.uri") == "http://somewhere.net/repository"
        repoProps.getProperty("chain") == "myext,mywatched,myremote"
    }
}
