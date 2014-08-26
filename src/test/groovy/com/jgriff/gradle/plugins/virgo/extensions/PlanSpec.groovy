package com.jgriff.gradle.plugins.virgo.extensions

import spock.lang.Specification

class PlanSpec extends Specification {
    def "filename for non-configured"() {
        expect:
        new Plan(null).filename() == ".plan"
    }

    def "filename for name-only"() {
        expect:
        new Plan("com.acme").filename() == "com.acme.plan"
    }

    def "filename for version-only"() {
        expect:
        new Plan(null).version("1.2.3").filename() == "1.2.3.plan"
    }

    def "filename for fully configured"() {
        expect:
        new Plan("com.acme").version("1.2.3").filename() == "com.acme-1.2.3.plan"
    }
}
