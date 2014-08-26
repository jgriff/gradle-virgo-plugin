package com.jgriff.gradle.plugins.virgo.integ

class VirgoAssemblyBasicIntegSpec extends IntegSpec {
    def setup() {
        buildFile << applyPlugin("'com.github.jgriff.virgo.assembly'")
    }

    def "simple"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our 'from' directory remains unchanged"
        file("build/virgo-unzipped/stuff.txt").text == "some stuff"

        then: "our assembled directory now contains"
        file("build/virgo-assembled/stuff.txt").text == "some stuff"
    }

    def "multiple assemblies"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-foo-assembled'
                    }
                    bar {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-bar-assembled'
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"

        when: "the build runs"
        runTasks 'assembleFoo', 'assembleBar'

        then: "our assembled directory now contains"
        fileExists("build/virgo-foo-assembled/stuff.txt")
        fileExists("build/virgo-bar-assembled/stuff.txt")
    }

    def "target directory defaults to assembly closure name"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our 'from' directory remains unchanged"
        file("build/virgo-unzipped/stuff.txt").text == "some stuff"

        then: "our assembled directory defaults to the assembly's name"
        file("build/virgo/foo/stuff.txt").text == "some stuff"
    }

    def "multiple from's"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        from 'build/another/dir'
                        into 'build/virgo-assembled'
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"
        file("build/another/dir/other.txt") << "some other stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our 'from' directory remains unchanged"
        file("build/virgo-unzipped/stuff.txt").text == "some stuff"

        then: "our assembled directory now contains"
        file("build/virgo-assembled/stuff.txt").text == "some stuff"
        file("build/virgo-assembled/other.txt").text == "some other stuff"
    }

    def "multiple with's"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        with copySpec { from 'build/virgo-unzipped' }
                        with copySpec { from 'build/another/dir' }
                        into 'build/virgo-assembled'
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"
        file("build/another/dir/other.txt") << "some other stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our 'from' directory remains unchanged"
        file("build/virgo-unzipped/stuff.txt").text == "some stuff"

        then: "our assembled directory now contains"
        file("build/virgo-assembled/stuff.txt").text == "some stuff"
        file("build/virgo-assembled/other.txt").text == "some other stuff"
    }
}
