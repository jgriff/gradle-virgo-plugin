package com.jgriff.gradle.plugins.virgo.integ

import org.custommonkey.xmlunit.XMLUnit

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class VirgoAssemblyPlanIntegSpec extends IntegSpec {
    def setup() {
        XMLUnit.setIgnoreAttributeOrder(true)
        XMLUnit.setIgnoreWhitespace(true)
        buildFile << applyPlugin("'com.github.jgriff.virgo.assembly'")
    }

    def "single plan"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        plans {
                            planA {
                                name 'com.acme.plan'
                                version '1.2.3.PLAN'

                                // the following are the different ways you can express the artifacts

                                // type = "bundle"
                                bundle name: 'com.acme.bundle.one', version: '1.2.3.ONE'
                                bundle name: 'com.acme.bundle.one.noversion' // if you omit a version, the plan's version will be used
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
                                configuration name: 'com.acme.config.one.noversion' // if you omit a version, the plan's version will be used
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
                                plan name: 'com.acme.plan.one.noversion' // if you omit a version, the plan's version will be used
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
                                par name: 'com.acme.par.one.noversion' // if you omit a version, the plan's version will be used
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
        assertXMLEqual("""
            <plan name="com.acme.plan" version="1.2.3.PLAN" scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">

                <artifact type="bundle" name="com.acme.bundle.one" version="1.2.3.ONE"/>
                <artifact type="bundle" name="com.acme.bundle.one.noversion" version="1.2.3.PLAN"/>
                <artifact type="bundle" name="com.acme.bundle.two" version="1.2.3.TWO"/>
                <artifact type="bundle" name="com.acme.bundle.three" version="1.2.3.THREE"/>
                <artifact type="bundle" name="com.acme.bundle.three.noversion" version="1.2.3.PLAN"/>
                <artifact type="bundle" name="com.acme.bundle.four" version="1.2.3.FOUR"/>
                <artifact type="bundle" name="com.acme.bundle.five" version="1.2.3.FIVE"/>
                <artifact type="bundle" name="com.acme.bundle.five.noversion" version="1.2.3.PLAN"/>

                <artifact type="configuration" name="com.acme.config.one" version="1.2.3.ONE"/>
                <artifact type="configuration" name="com.acme.config.one.noversion" version="1.2.3.PLAN"/>
                <artifact type="configuration" name="com.acme.config.two" version="1.2.3.TWO"/>
                <artifact type="configuration" name="com.acme.config.three" version="1.2.3.THREE"/>
                <artifact type="configuration" name="com.acme.config.three.noversion" version="1.2.3.PLAN"/>
                <artifact type="configuration" name="com.acme.config.four" version="1.2.3.FOUR"/>
                <artifact type="configuration" name="com.acme.config.five" version="1.2.3.FIVE"/>
                <artifact type="configuration" name="com.acme.config.five.noversion" version="1.2.3.PLAN"/>

                <artifact type="plan" name="com.acme.plan.one" version="1.2.3.ONE"/>
                <artifact type="plan" name="com.acme.plan.one.noversion" version="1.2.3.PLAN"/>
                <artifact type="plan" name="com.acme.plan.two" version="1.2.3.TWO"/>
                <artifact type="plan" name="com.acme.plan.three" version="1.2.3.THREE"/>
                <artifact type="plan" name="com.acme.plan.three.noversion" version="1.2.3.PLAN"/>
                <artifact type="plan" name="com.acme.plan.four" version="1.2.3.FOUR"/>
                <artifact type="plan" name="com.acme.plan.five" version="1.2.3.FIVE"/>
                <artifact type="plan" name="com.acme.plan.five.noversion" version="1.2.3.PLAN"/>

                <artifact type="par" name="com.acme.par.one" version="1.2.3.ONE"/>
                <artifact type="par" name="com.acme.par.one.noversion" version="1.2.3.PLAN"/>
                <artifact type="par" name="com.acme.par.two" version="1.2.3.TWO"/>
                <artifact type="par" name="com.acme.par.three" version="1.2.3.THREE"/>
                <artifact type="par" name="com.acme.par.three.noversion" version="1.2.3.PLAN"/>
                <artifact type="par" name="com.acme.par.four" version="1.2.3.FOUR"/>
                <artifact type="par" name="com.acme.par.five" version="1.2.3.FIVE"/>
                <artifact type="par" name="com.acme.par.five.noversion" version="1.2.3.PLAN"/>
            </plan>
            """, file("build/virgo-assembled/pickup/com.acme.plan-1.2.3.PLAN.plan").text)
    }

    def "multi-plan"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        plans {
                            planA {
                                name 'com.acme.foo.plan'
                                version '1.2.3.RELEASE'
                                bundle name: 'com.acme.a', version: '1.2.3.A'
                            }
                            planB {
                                name 'com.acme.bar.plan'
                                version '3.2.1.RELEASE'
                                bundle name: 'com.acme.b', version: '3.2.1.B'
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
        assertXMLEqual("""
            <plan name="com.acme.foo.plan" version="1.2.3.RELEASE" scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">
                <artifact type="bundle" name="com.acme.a" version="1.2.3.A"/>
            </plan>
            """, file("build/virgo-assembled/pickup/com.acme.foo.plan-1.2.3.RELEASE.plan").text)
        assertXMLEqual("""
            <plan name="com.acme.bar.plan" version="3.2.1.RELEASE" scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">
                <artifact type="bundle" name="com.acme.b" version="3.2.1.B"/>
            </plan>
            """, file("build/virgo-assembled/pickup/com.acme.bar.plan-3.2.1.RELEASE.plan").text)
    }

    def "write to alternate directory"() {
        given:
        buildFile  << """
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        plans {
                            planA {
                                name 'com.acme.plan'
                                version '1.2.3.PLAN'
                                into 'other/plan/dir'
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
        fileDoesNotExist("build/virgo-assembled/pickup/com.acme.plan-1.2.3.PLAN.plan")
        fileExists("build/virgo-assembled/other/plan/dir/com.acme.plan-1.2.3.PLAN.plan")
    }

    def "version defaults to project.version"() {
        given:
        buildFile  << """
            version '1.2.3.PROJECT'
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        plans {
                            planA {
                                name 'com.acme.plan'
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
        assertXMLEqual("""
            <plan name="com.acme.plan" version="1.2.3.PROJECT" scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">
            </plan>
            """, file("build/virgo-assembled/pickup/com.acme.plan-1.2.3.PROJECT.plan").text)
    }

    def "scoped"() {
        given:
        buildFile  << """
            version '1.2.3.PROJECT'
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        plans {
                            planA {
                                name 'com.acme.plan'
                                scoped true
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
        assertXMLEqual("""
            <plan name="com.acme.plan" version="1.2.3.PROJECT" scoped="true" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">
            </plan>
            """, file("build/virgo-assembled/pickup/com.acme.plan-1.2.3.PROJECT.plan").text)
    }

    def "atomic"() {
        given:
        buildFile  << """
            version '1.2.3.PROJECT'
            virgo {
                assemblies {
                    foo {
                        from 'build/virgo-unzipped'
                        into 'build/virgo-assembled'

                        plans {
                            planA {
                                name 'com.acme.plan'
                                atomic true
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
        assertXMLEqual("""
            <plan name="com.acme.plan" version="1.2.3.PROJECT" scoped="false" atomic="true"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">
            </plan>
            """, file("build/virgo-assembled/pickup/com.acme.plan-1.2.3.PROJECT.plan").text)
    }
}
