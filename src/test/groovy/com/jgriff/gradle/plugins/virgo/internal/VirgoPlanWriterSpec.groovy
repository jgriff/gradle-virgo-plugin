package com.jgriff.gradle.plugins.virgo.internal

import com.jgriff.gradle.plugins.virgo.extensions.Artifact
import com.jgriff.gradle.plugins.virgo.extensions.Plan
import com.jgriff.gradle.plugins.virgo.TemporaryFolderSpec
import org.custommonkey.xmlunit.XMLUnit

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class VirgoPlanWriterSpec extends TemporaryFolderSpec {
    VirgoPlanWriter sut

    def setup() {
        sut = new VirgoPlanWriter()
        XMLUnit.setIgnoreAttributeOrder(true)
        XMLUnit.setIgnoreWhitespace(true)
    }

    File getOutputFile() {
        file("test.xml")
    }

    def "no plan specified"() {
        when:
        sut.writeTo(outputFile)

        then:
        final IllegalStateException error = thrown()
        error.message.startsWith("You must specify the plan to use by calling 'plan(Plan)'.")
    }

    def "name"() {
        given:
        Plan plan = new Plan().name("com.acme.plan")

        when:
        sut.plan(plan).writeTo(outputFile)

        then:
        assertXMLEqual("""
            <plan name="com.acme.plan" scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd"/>
            """, outputFile.text)
    }

    def "name (constructor)"() {
        given:
        Plan plan = new Plan("com.acme.plan")

        when:
        sut.plan(plan).writeTo(outputFile)

        then:
        assertXMLEqual("""
            <plan name="com.acme.plan" scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd"/>
            """, outputFile.text)
    }

    def "version"() {
        given:
        Plan plan = new Plan().version("1.2.3.RELEASE")

        when:
        sut.plan(plan).writeTo(outputFile)

        then:
        assertXMLEqual("""
            <plan version="1.2.3.RELEASE" scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd"/>
            """, outputFile.text)
    }

    def "scoped"() {
        given:
        Plan plan = new Plan().scoped(true)

        when:
        sut.plan(plan).writeTo(outputFile)

        then:
        assertXMLEqual("""
            <plan scoped="true" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd"/>
            """, outputFile.text)
    }

    def "atomic"() {
        given:
        Plan plan = new Plan().atomic(true)

        when:
        sut.plan(plan).writeTo(outputFile)

        then:
        assertXMLEqual("""
            <plan scoped="false" atomic="true"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd"/>
            """, outputFile.text)
    }

    def "artifacts"() {
        given:
        Plan plan = new Plan()
                .artifact(new Artifact("bundle", "com.acme.api", "1.2.3.FOO"))
                .artifact(new Artifact("bundle", "com.acme.impl", "3.2.1.BAR"))

        when:
        sut.plan(plan).writeTo(outputFile)

        then:
        assertXMLEqual("""
            <plan scoped="false" atomic="false"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">

                <artifact type="bundle" name="com.acme.api" version="1.2.3.FOO"/>
                <artifact type="bundle" name="com.acme.impl" version="3.2.1.BAR"/>
            </plan>
            """, outputFile.text)
    }

    def "all together"() {
        given:
        Plan plan = new Plan().name("com.acme.plan").version("1.2.3.RELEASE").atomic(true).scoped(true)
                .artifact(new Artifact("bundle", "com.acme.api", "1.2.3.FOO"))
                .artifact(new Artifact("bundle", "com.acme.impl", "3.2.1.BAR"))

        when:
        sut.plan(plan).writeTo(outputFile)

        then:
        assertXMLEqual("""
            <plan name="com.acme.plan" version="1.2.3.RELEASE" scoped="true" atomic="true"
                    xmlns="http://www.eclipse.org/virgo/schema/plan"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">

                <artifact type="bundle" name="com.acme.api" version="1.2.3.FOO"/>
                <artifact type="bundle" name="com.acme.impl" version="3.2.1.BAR"/>
            </plan>
            """, outputFile.text)
    }
}
