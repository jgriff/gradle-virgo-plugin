package com.jgriff.gradle.plugins.virgo.internal

import org.gradle.api.Action
import org.gradle.api.Nullable
import org.gradle.api.file.CopyProcessingSpec
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileTreeElement
import org.gradle.api.specs.Spec

import java.util.regex.Pattern

/**
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class CopySpecRecorder implements CopySpec {

    Boolean caseSensitive, includeEmptyDirs
    DuplicatesStrategy duplicatesStrategy
    List<TwoArgs> filesMatchingTwoArgs = new ArrayList<TwoArgs>()
    List<TwoArgs> filesNotMatchingTwoArgs = new ArrayList<TwoArgs>()
    List<CopySpec> with = new ArrayList<CopySpec>()
    List<Object> from = new ArrayList<Object>()
    List<TwoArgs> fromTwoArgs = new ArrayList<TwoArgs>()
    Set<String> includes
    Set<String> excludes
    List<String> includeStrs = new ArrayList<String>()
    List<String> excludeStrs = new ArrayList<String>()
    List<Object> includeObjs = new ArrayList<Object>()
    List<Object> excludeObjs = new ArrayList<Object>()
    Object into
    TwoArgs intoTwoArgs
    List<Object> rename = new ArrayList<Object>()
    List<TwoArgs> renameTwoArgs = new ArrayList<TwoArgs>()
    Integer fileMode, dirMode
    List<Object> filter = new ArrayList<Object>()
    List<TwoArgs> filterTwoArgs = new ArrayList<TwoArgs>()
    List<Object> expand = new ArrayList<Object>()
    List<Object> eachFile = new ArrayList<Object>()

    CopySpecRecorder applyTo(final CopySpec target) {
        if (caseSensitive != null) target.setCaseSensitive(caseSensitive)
        if (includeEmptyDirs != null) target.setIncludeEmptyDirs(includeEmptyDirs)
        if (duplicatesStrategy != null) target.setDuplicatesStrategy(duplicatesStrategy)
        filesMatchingTwoArgs.each { target.filesMatching(it.arg1, it.arg2) }
        filesNotMatchingTwoArgs.each { target.filesNotMatching(it.arg1, it.arg2) }
        with.each { target.with(it) }
        from.each { target.from(it) }
        fromTwoArgs.each { target.from(it.arg1, it.arg2) }
        if (includes != null) target.setIncludes(includes)
        if (excludes != null) target.setExcludes(excludes)
        includeStrs.each { target.include(it) }
        includeObjs.each { target.include(it) }
        excludeStrs.each { target.exclude(it) }
        excludeObjs.each { target.exclude(it) }
        if (into != null) { target.into(into) }
        if (intoTwoArgs != null) { target.into(intoTwoArgs.arg1, intoTwoArgs.arg2) }
        rename.each { target.rename(it) }
        renameTwoArgs.each { target.rename(it.arg1, it.arg2) }
        if (fileMode != null) target.setFileMode(fileMode)
        if (dirMode != null) target.setDirMode(dirMode)
        filterTwoArgs.each { target.filter(it.arg1, it.arg2) }
        filter.each { target.filter(it) }
        expand.each { target.expand(it) }
        eachFile.each { target.eachFile(it) }

        this
    }

    @Override boolean isCaseSensitive() { caseSensitive }
    @Override void setCaseSensitive(boolean b) { this.caseSensitive = b }
    @Override boolean getIncludeEmptyDirs() { includeEmptyDirs }
    @Override void setIncludeEmptyDirs(boolean b) { this.includeEmptyDirs = b }
    @Override DuplicatesStrategy getDuplicatesStrategy() { duplicatesStrategy }
    @Override void setDuplicatesStrategy(@Nullable DuplicatesStrategy duplicatesStrategy) { this.duplicatesStrategy = duplicatesStrategy }
    @Override CopySpec filesMatching(String s, Action<? super FileCopyDetails> action) { this.filesMatchingTwoArgs.add(new TwoArgs(s, action)); this }
    @Override CopySpec filesNotMatching(String s, Action<? super FileCopyDetails> action) { this.filesNotMatchingTwoArgs.add(new TwoArgs(s, action)); this }
    @Override CopySpec with(CopySpec... copySpecs) { copySpecs.each {this.with.add(it)}; this }
    @Override CopySpec from(Object... objects) { objects.each {this.from.add(it)}; this }
    @Override CopySpec from(Object o, Closure closure) { this.fromTwoArgs.add(new TwoArgs(o, closure)); this }
    @Override Set<String> getIncludes() { includes }
    @Override Set<String> getExcludes() { excludes }
    @Override CopySpec setIncludes(Iterable<String> strings) { this.includes = strings.asList().toSet(); this }
    @Override CopySpec setExcludes(Iterable<String> strings) { this.excludes = strings.asList().toSet(); this }
    @Override CopySpec include(String... strings) { this.includeStrs.addAll(strings); this }
    @Override CopySpec include(Iterable<String> strings) { this.includeStrs.addAll(strings.asList()); this }
    @Override CopySpec include(Spec<FileTreeElement> fileTreeElementSpec) { this.includeObjs.add(fileTreeElementSpec); this }
    @Override CopySpec include(Closure closure) { this.includeObjs.add(closure); this }
    @Override CopySpec exclude(String... strings) { this.excludeStrs.addAll(strings); this }
    @Override CopySpec exclude(Iterable<String> strings) { this.excludeStrs.addAll(strings.asList()); this }
    @Override CopySpec exclude(Spec<FileTreeElement> fileTreeElementSpec) { this.excludeObjs.add(fileTreeElementSpec); this }
    @Override CopySpec exclude(Closure closure) { this.excludeObjs.add(closure); this }
    @Override CopySpec into(Object into) { this.into = into; this }
    @Override CopySpec into(Object into, Closure closure) { this.intoTwoArgs = new TwoArgs(into, closure); this}
    @Override CopySpec rename(Closure closure) { this.rename.add(closure); this }
    @Override CopySpec rename(String s, String s2) { this.renameTwoArgs.add(new TwoArgs(s, s2)); this }
    @Override CopyProcessingSpec rename(Pattern pattern, String s) { this.renameTwoArgs.add(new TwoArgs(pattern, s)); this }
    @Override Integer getFileMode() { fileMode }
    @Override CopyProcessingSpec setFileMode(Integer fileMode) { this.fileMode = fileMode; this }
    @Override Integer getDirMode() { dirMode }
    @Override CopyProcessingSpec setDirMode(Integer dirMode) { this.dirMode = dirMode; this }
    @Override CopySpec filter(Map<String, ?> stringMap, Class<? extends FilterReader> aClass) { this.filterTwoArgs.add(new TwoArgs(stringMap, aClass)); this }
    @Override CopySpec filter(Class<? extends FilterReader> aClass) { this.filter.add(aClass); this }
    @Override CopySpec filter(Closure closure) { this.filter.add(closure); this }
    @Override CopySpec expand(Map<String, ?> stringMap) { this.expand.add(stringMap); this }
    @Override CopySpec eachFile(Action<? super FileCopyDetails> action) { this.eachFile.add(action); this }
    @Override CopySpec eachFile(Closure closure) { this.eachFile.add(closure); this }

    private class TwoArgs {
        def arg1, arg2

        TwoArgs(arg1, arg2) {
            this.arg1 = arg1
            this.arg2 = arg2
        }
    }
}
