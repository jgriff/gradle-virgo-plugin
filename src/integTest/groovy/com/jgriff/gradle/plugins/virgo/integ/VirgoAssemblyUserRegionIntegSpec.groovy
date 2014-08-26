package com.jgriff.gradle.plugins.virgo.integ

import com.jgriff.gradle.plugins.virgo.internal.Virgo
import org.custommonkey.xmlunit.XMLUnit

class VirgoAssemblyUserRegionIntegSpec extends IntegSpec {
    def setup() {
        XMLUnit.setIgnoreAttributeOrder(true)
        XMLUnit.setIgnoreWhitespace(true)
        buildFile << applyPlugin("'com.github.jgriff.virgo.assembly'")
    }

    def "empty config (from scratch)"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        userRegion { }
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our assembled directory now contains"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.USER_REGION_CONFIG).newDataInputStream())

        repoProps.getProperty("initialArtifacts") == null
    }

    def "empty config (on top of existing config)"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        userRegion { }
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/" + Virgo.USER_REGION_CONFIG) << """
baseBundles = file:plugins/org.eclipse.equinox.cm_1.0.400.v20120319-2029.jar@start,file:plugins/org.eclipse.virgo.kernel.userregion_3.6.3.RELEASE.jar@start

bundleImports = org.eclipse.osgi;bundle-version="0"

packageImports = org.eclipse.virgo.kernel.artifact.*;version="0",org.eclipse.virgo.nano.core;version="0"

serviceImports = org.eclipse.equinox.region.Region,org.eclipse.equinox.region.RegionDigraph

serviceExports = org.eclipse.virgo.kernel.install.artifact.InstallArtifactLifecycleListener,org.eclipse.virgo.kernel.install.artifact.ArtifactIdentityDeterminer

initialArtifacts=repository:plan/org.eclipse.virgo.kernel.userregion.blueprint,repository:plan/org.eclipse.virgo.web.tomcat
"""

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our assembled directory now contains"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.USER_REGION_CONFIG).newDataInputStream())

        repoProps.getProperty("baseBundles") == "file:plugins/org.eclipse.equinox.cm_1.0.400.v20120319-2029.jar@start,file:plugins/org.eclipse.virgo.kernel.userregion_3.6.3.RELEASE.jar@start"
        repoProps.getProperty("bundleImports") == "org.eclipse.osgi;bundle-version=\"0\""
        repoProps.getProperty("packageImports") == "org.eclipse.virgo.kernel.artifact.*;version=\"0\",org.eclipse.virgo.nano.core;version=\"0\""
        repoProps.getProperty("serviceImports") == "org.eclipse.equinox.region.Region,org.eclipse.equinox.region.RegionDigraph"
        repoProps.getProperty("serviceExports") == "org.eclipse.virgo.kernel.install.artifact.InstallArtifactLifecycleListener,org.eclipse.virgo.kernel.install.artifact.ArtifactIdentityDeterminer"
        repoProps.getProperty("initialArtifacts") == "repository:plan/org.eclipse.virgo.kernel.userregion.blueprint,repository:plan/org.eclipse.virgo.web.tomcat"
    }

    def "initialArtifacts (from scratch)"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        userRegion {
                            initialArtifacts {
                                // the following are the different ways you can express the artifacts

                                // type = "bundle"
                                bundle name: 'com.acme.bundle.one', version: '1.2.3.ONE'
                                bundle name: 'com.acme.bundle.one.noversion' // if you omit a version, it will be omitted in the userregion.properties
                                bundle 'com.acme.bundle.two:1.2.3.TWO',
                                       'com.acme.bundle.three:1.2.3.THREE',
                                       'com.acme.bundle.three.noversion'
                                bundle(
                                    [name: 'com.acme.bundle.four', version: '1.2.3.FOUR'],
                                    [name: 'com.acme.bundle.five', version: '1.2.3.FIVE'],
                                    [name: 'com.acme.bundle.five.noversion']
                                )

                                // type = "configuration"
                                configuration name: 'com.acme.config.one', version: '1.2.3.ONE'
                                configuration name: 'com.acme.config.one.noversion' // if you omit a version, it will be omitted in the userregion.properties
                                configuration 'com.acme.config.two:1.2.3.TWO',
                                       'com.acme.config.three:1.2.3.THREE',
                                       'com.acme.config.three.noversion'
                                configuration(
                                    [name: 'com.acme.config.four', version: '1.2.3.FOUR'],
                                    [name: 'com.acme.config.five', version: '1.2.3.FIVE'],
                                    [name: 'com.acme.config.five.noversion']
                                )

                                // type = "plan"
                                plan name: 'com.acme.plan.one', version: '1.2.3.ONE'
                                plan name: 'com.acme.plan.one.noversion' // if you omit a version, it will be omitted in the userregion.properties
                                plan 'com.acme.plan.two:1.2.3.TWO',
                                       'com.acme.plan.three:1.2.3.THREE',
                                       'com.acme.plan.three.noversion'
                                plan(
                                    [name: 'com.acme.plan.four', version: '1.2.3.FOUR'],
                                    [name: 'com.acme.plan.five', version: '1.2.3.FIVE'],
                                    [name: 'com.acme.plan.five.noversion']
                                )

                                // type = "par"
                                par name: 'com.acme.par.one', version: '1.2.3.ONE'
                                par name: 'com.acme.par.one.noversion' // if you omit a version, it will be omitted in the userregion.properties
                                par 'com.acme.par.two:1.2.3.TWO',
                                       'com.acme.par.three:1.2.3.THREE',
                                       'com.acme.par.three.noversion'
                                par(
                                    [name: 'com.acme.par.four', version: '1.2.3.FOUR'],
                                    [name: 'com.acme.par.five', version: '1.2.3.FIVE'],
                                    [name: 'com.acme.par.five.noversion']
                                )
                            }
                        }
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/stuff.txt") << "some stuff"

        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our assembled directory now contains"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.USER_REGION_CONFIG).newDataInputStream())

        repoProps.getProperty("initialArtifacts") ==
            "repository:bundle/com.acme.bundle.one/1.2.3.ONE," +
            "repository:bundle/com.acme.bundle.one.noversion," +
            "repository:bundle/com.acme.bundle.two/1.2.3.TWO," +
            "repository:bundle/com.acme.bundle.three/1.2.3.THREE," +
            "repository:bundle/com.acme.bundle.three.noversion," +
            "repository:bundle/com.acme.bundle.four/1.2.3.FOUR," +
            "repository:bundle/com.acme.bundle.five/1.2.3.FIVE," +
            "repository:bundle/com.acme.bundle.five.noversion," +

            "repository:configuration/com.acme.config.one/1.2.3.ONE," +
            "repository:configuration/com.acme.config.one.noversion," +
            "repository:configuration/com.acme.config.two/1.2.3.TWO," +
            "repository:configuration/com.acme.config.three/1.2.3.THREE," +
            "repository:configuration/com.acme.config.three.noversion," +
            "repository:configuration/com.acme.config.four/1.2.3.FOUR," +
            "repository:configuration/com.acme.config.five/1.2.3.FIVE," +
            "repository:configuration/com.acme.config.five.noversion," +

            "repository:plan/com.acme.plan.one/1.2.3.ONE," +
            "repository:plan/com.acme.plan.one.noversion," +
            "repository:plan/com.acme.plan.two/1.2.3.TWO," +
            "repository:plan/com.acme.plan.three/1.2.3.THREE," +
            "repository:plan/com.acme.plan.three.noversion," +
            "repository:plan/com.acme.plan.four/1.2.3.FOUR," +
            "repository:plan/com.acme.plan.five/1.2.3.FIVE," +
            "repository:plan/com.acme.plan.five.noversion," +

            "repository:par/com.acme.par.one/1.2.3.ONE," +
            "repository:par/com.acme.par.one.noversion," +
            "repository:par/com.acme.par.two/1.2.3.TWO," +
            "repository:par/com.acme.par.three/1.2.3.THREE," +
            "repository:par/com.acme.par.three.noversion," +
            "repository:par/com.acme.par.four/1.2.3.FOUR," +
            "repository:par/com.acme.par.five/1.2.3.FIVE," +
            "repository:par/com.acme.par.five.noversion"
    }

    def "initialArtifacts (on top of existing config)"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        userRegion {
                            initialArtifacts {
                                bundle name: 'com.acme.bundle.one', version: '1.2.3.ONE'
                            }
                        }
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/" + Virgo.USER_REGION_CONFIG) << """
initialArtifacts=repository:plan/org.eclipse.virgo.kernel.userregion.blueprint
"""
        when: "the build runs"
        runTasks 'assembleFoo'

        then: "our assembled directory now contains"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.USER_REGION_CONFIG).newDataInputStream())

        repoProps.getProperty("initialArtifacts") ==
            "repository:plan/org.eclipse.virgo.kernel.userregion.blueprint," +
            "repository:bundle/com.acme.bundle.one/1.2.3.ONE"
    }

    def "initialArtifacts (overwrite)"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        userRegion {
                            initialArtifacts {
                                overwrite true
                                bundle name: 'com.acme.bundle.one', version: '1.2.3.ONE'
                            }
                        }
                    }
                }
            }
        """

        and: "existing content"
        file("build/virgo-unzipped/" + Virgo.USER_REGION_CONFIG) << """
initialArtifacts=repository:plan/org.eclipse.virgo.kernel.userregion.blueprint
"""
        when: "the build runs"
        runTasks 'assembleFoo'

        then: "we overwrite the 'initialArtifacts' with just ours"
        Properties repoProps = new Properties()
        repoProps.load(file("build/virgo-assembled/" + Virgo.USER_REGION_CONFIG).newDataInputStream())

        repoProps.getProperty("initialArtifacts") ==
            "repository:bundle/com.acme.bundle.one/1.2.3.ONE"
    }
}
