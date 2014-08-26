package com.jgriff.gradle.plugins.virgo.internal

import org.gradle.api.Action
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileTreeElement
import org.gradle.api.specs.Spec
import spock.lang.Specification

import java.util.regex.Pattern

class CopySpecRecorderSpec extends Specification {
    CopySpecRecorder sut
    CopySpec mockTarget
    Action mockAction
    CopySpec mockCopySpec
    Object mockObj
    Closure mockClosure
    Spec mockSpec
    Pattern mockPattern
    Map mockMap

    def setup() {
        sut = new CopySpecRecorder()
        mockTarget = Mock()
        mockAction = Mock()
        mockCopySpec = Mock()
        mockObj = new Object()
        mockClosure = {}
        mockSpec = Mock()
        mockPattern = Pattern.compile(".")
        mockMap = Mock()
    }

    def "setCaseSensitive(boolean)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.setCaseSensitive(_)

        and: sut.setCaseSensitive(true)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setCaseSensitive(true)

        and: sut.setCaseSensitive(false)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setCaseSensitive(false)
    }

    def "setIncludeEmptyDirs(boolean)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.setIncludeEmptyDirs(_)

        and: sut.setIncludeEmptyDirs(true)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setIncludeEmptyDirs(true)

        and: sut.setIncludeEmptyDirs(false)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setIncludeEmptyDirs(false)
    }

    def "setDuplicatesStrategy(DuplicatesStrategy)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.setDuplicatesStrategy(_)

        and: sut.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE)
    }

    def "filesMatching(String,Action)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.filesMatching(_,_)

        and: sut.filesMatching("foo", mockAction)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.filesMatching("foo", mockAction)
    }

    def "filesNotMatching(String,Action)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.filesNotMatching(_,_)

        and: sut.filesNotMatching("foo", mockAction)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.filesNotMatching("foo", mockAction)
    }

    def "with(CopySpec...)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.with(_)

        and: sut.with(mockCopySpec, mockCopySpec)
        when: sut.applyTo(mockTarget)
        then: 2 * mockTarget.with(mockCopySpec)
    }

    def "from(Object...)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.from(_)

        and: sut.from(mockObj)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.from(mockObj)
    }

    def "from(Object, Closure)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.from(_,_)

        and: sut.from(mockObj, mockClosure)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.from(mockObj, mockClosure)
    }

    def "set/get includes()"() {
        Set<String> includes = new HashSet<String>()
        includes.add("include-1")
        includes.add("include-2")

        when: sut.applyTo(mockTarget)
        then: mockTarget.getIncludes() == null

        and: sut.setIncludes(includes)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setIncludes( {it.containsAll(includes)} )
    }

    def "set/get excludes()"() {
        Set<String> excludes = new HashSet<String>()
        excludes.add("exclude-1")
        excludes.add("exclude-2")

        when: sut.applyTo(mockTarget)
        then: mockTarget.getExcludes() == null

        and: sut.setExcludes(excludes)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setExcludes( {it.containsAll(excludes)} )
    }

    def "include(String...)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.include(_)

        and: sut.include("one", "two")
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.include("one")
              1 * mockTarget.include("two")
    }

    def "include(Iterable<String>)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.include(_)

        and: sut.include(Arrays.asList("one", "two"))
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.include("one")
              1 * mockTarget.include("two")
    }

    def "include(Spec<FileTreeElement>)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.include(_)

        and: sut.include(mockSpec)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.include(mockSpec)
    }

    def "include(Closure)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.include(_)

        and: sut.include(mockClosure)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.include(mockClosure)
    }

    def "exclude(String...)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.exclude(_)

        and: sut.exclude("one", "two")
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.exclude("one")
              1 * mockTarget.exclude("two")
    }

    def "exclude(Iterable<String>)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.exclude(_)

        and: sut.exclude(Arrays.asList("one", "two"))
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.exclude("one")
              1 * mockTarget.exclude("two")
    }

    def "exclude(Spec<FileTreeElement>)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.exclude(_)

        and: sut.exclude(mockSpec)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.exclude(mockSpec)
    }

    def "exclude(Closure)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.exclude(_)

        and: sut.exclude(mockClosure)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.exclude(mockClosure)
    }

    def "into(Object)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.into(_)

        and: sut.into(mockObj)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.into(mockObj)
    }

    def "into(Object, Closure)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.into(_,_)

        and: sut.into(mockObj, mockClosure)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.into(mockObj, mockClosure)
    }
    
    def "rename(Closure)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.rename(_)
    
        and: sut.rename(mockClosure)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.rename(mockClosure)
    }

    def "rename(String, String)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.rename(_)

        and: sut.rename("one", "two")
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.rename("one", "two")
    }

    def "rename(Pattern, String)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.rename(_)

        and: sut.rename(mockPattern, "str")
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.rename(mockPattern, "str")
    }

    def "setFileMode(Integer)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.setFileMode(_)

        and: sut.setFileMode(4)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setFileMode(4)
    }

    def "setDirMode(Integer)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.setDirMode(_)

        and: sut.setDirMode(6)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.setDirMode(6)
    }

    def "filter(Map, Class)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.filter(_)

        and: sut.filter(mockMap, String.class)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.filter(mockMap, String.class)
    }

    def "filter(Class)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.filter(_)

        and: sut.filter(FilterReader.class)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.filter(FilterReader.class)
    }

    def "filter(Closure)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.filter(_)

        and: sut.filter(mockClosure)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.filter(mockClosure)
    }

    def "expand(Map)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.expand(_)

        and: sut.expand(mockMap)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.expand(mockMap)
    }

    def "eachFile(Action)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.eachFile(_)

        and: sut.eachFile(mockAction)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.eachFile(mockAction)
    }

    def "eachFile(Closure)"() {
        when: sut.applyTo(mockTarget)
        then: 0 * mockTarget.eachFile(_)

        and: sut.eachFile(mockClosure)
        when: sut.applyTo(mockTarget)
        then: 1 * mockTarget.eachFile(mockClosure)
    }
}
