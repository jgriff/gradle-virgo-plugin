package com.jgriff.gradle.plugins.virgo.internal

import com.jgriff.gradle.plugins.virgo.extensions.Artifact
import com.jgriff.gradle.plugins.virgo.extensions.UserRegionConfig
import com.jgriff.gradle.plugins.virgo.TemporaryFolderSpec

class VirgoUserRegionPropertiesWriterSpec extends TemporaryFolderSpec {

    File getConfigProps() {
        file("userregion.properties")
    }

    def "write nothing"() {
        given: "existing content in properties file"
        configProps << "foo=bar"
        VirgoUserRegionPropertiesWriter sut = new VirgoUserRegionPropertiesWriter(configProps)

        when: "nothing to write"
        sut.writeTo(configProps)

        then: "original file remains unchanged"
        configProps.text.trim() == "foo=bar"
    }

    def "initialArtifacts (from scratch)"() {
        when: "a writer flushes new config to a new file"
        new VirgoUserRegionPropertiesWriter()
            .add(new UserRegionConfig()
                .initialArtifact(new Artifact("plan", "com.acme.plan"))
                .initialArtifact(new Artifact("plan", "com.acme.plan.versioned", "1.2.3.PLAN"))
                .initialArtifact(new Artifact("par", "com.acme.par"))
                .initialArtifact(new Artifact("par", "com.acme.par.versioned", "1.2.3.PAR"))
                .initialArtifact(new Artifact("bundle", "com.acme.bundle"))
                .initialArtifact(new Artifact("bundle", "com.acme.bundle.versioned", "1.2.3.BUNDLE"))
                .initialArtifact(new Artifact("configuration", "com.acme.properties"))
                .initialArtifact(new Artifact("configuration", "com.acme.properties.versioned", "1.2.3.CONFIGURATION")))
            .writeTo(configProps)

        then: "resulting properties file contains new stuff"
        Properties resultingProps = new Properties()
        resultingProps.load(configProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                 | value
        "initialArtifacts"  | "repository:plan/com.acme.plan," +
                              "repository:plan/com.acme.plan.versioned/1.2.3.PLAN," +
                              "repository:par/com.acme.par," +
                              "repository:par/com.acme.par.versioned/1.2.3.PAR," +
                              "repository:bundle/com.acme.bundle," +
                              "repository:bundle/com.acme.bundle.versioned/1.2.3.BUNDLE," +
                              "repository:configuration/com.acme.properties," +
                              "repository:configuration/com.acme.properties.versioned/1.2.3.CONFIGURATION"
    }

    def "initialArtifacts (add to existing file)"() {
        given: "file contains existing user region configuration"
        configProps << "baseBundles = file:plugins/org.eclipse.equinox.cm_1.0.400.v20120319-2029.jar@start,file:plugins/org.eclipse.virgo.kernel.userregion_3.6.3.RELEASE.jar@start" +
                "\n" +
                "bundleImports = org.eclipse.osgi;bundle-version=\"0\"" +
                "\n" +
                "packageImports = org.eclipse.virgo.kernel.artifact.*;version=\"0\",org.eclipse.virgo.nano.core;version=\"0\"" +
                "\n" +
                "serviceImports = org.eclipse.equinox.region.Region,org.eclipse.equinox.region.RegionDigraph" +
                "\n" +
                "serviceExports = org.eclipse.virgo.kernel.install.artifact.InstallArtifactLifecycleListener,org.eclipse.virgo.kernel.install.artifact.ArtifactIdentityDeterminer" +
                "\n" +
                "initialArtifacts=repository:plan/org.eclipse.virgo.kernel.userregion.blueprint,repository:plan/org.eclipse.virgo.web.tomcat"

        when: "a writer flushes new config to the file"
        new VirgoUserRegionPropertiesWriter(configProps)
            .add(new UserRegionConfig()
                .initialArtifact(new Artifact("plan", "com.acme.plan"))
                .initialArtifact(new Artifact("plan", "com.acme.plan.versioned", "1.2.3.PLAN"))
                .initialArtifact(new Artifact("par", "com.acme.par"))
                .initialArtifact(new Artifact("par", "com.acme.par.versioned", "1.2.3.PAR"))
                .initialArtifact(new Artifact("bundle", "com.acme.bundle"))
                .initialArtifact(new Artifact("bundle", "com.acme.bundle.versioned", "1.2.3.BUNDLE"))
                .initialArtifact(new Artifact("configuration", "com.acme.properties"))
                .initialArtifact(new Artifact("configuration", "com.acme.properties.versioned", "1.2.3.CONFIGURATION")))
            .writeTo(configProps)

        then: "resulting properties file is a combination of existing + new"
        Properties resultingProps = new Properties()
        resultingProps.load(configProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                 | value
                              // existing
        "baseBundles"       | "file:plugins/org.eclipse.equinox.cm_1.0.400.v20120319-2029.jar@start," +
                              "file:plugins/org.eclipse.virgo.kernel.userregion_3.6.3.RELEASE.jar@start"
        "bundleImports"     | "org.eclipse.osgi;bundle-version=\"0\""
        "packageImports"    | "org.eclipse.virgo.kernel.artifact.*;version=\"0\"," +
                              "org.eclipse.virgo.nano.core;version=\"0\""
        "serviceImports"    | "org.eclipse.equinox.region.Region," +
                              "org.eclipse.equinox.region.RegionDigraph"
        "serviceExports"    | "org.eclipse.virgo.kernel.install.artifact.InstallArtifactLifecycleListener," +
                              "org.eclipse.virgo.kernel.install.artifact.ArtifactIdentityDeterminer"
        "initialArtifacts"  | "repository:plan/org.eclipse.virgo.kernel.userregion.blueprint," +
                              "repository:plan/org.eclipse.virgo.web.tomcat," +
                              // new
                              "repository:plan/com.acme.plan," +
                              "repository:plan/com.acme.plan.versioned/1.2.3.PLAN," +
                              "repository:par/com.acme.par," +
                              "repository:par/com.acme.par.versioned/1.2.3.PAR," +
                              "repository:bundle/com.acme.bundle," +
                              "repository:bundle/com.acme.bundle.versioned/1.2.3.BUNDLE," +
                              "repository:configuration/com.acme.properties," +
                              "repository:configuration/com.acme.properties.versioned/1.2.3.CONFIGURATION"
    }

    def "initialArtifacts (overwrite)"() {
        given: "file contains existing user region configuration"
        configProps << "baseBundles = file:plugins/org.eclipse.equinox.cm_1.0.400.v20120319-2029.jar@start,file:plugins/org.eclipse.virgo.kernel.userregion_3.6.3.RELEASE.jar@start" +
                "\n" +
                "bundleImports = org.eclipse.osgi;bundle-version=\"0\"" +
                "\n" +
                "packageImports = org.eclipse.virgo.kernel.artifact.*;version=\"0\",org.eclipse.virgo.nano.core;version=\"0\"" +
                "\n" +
                "serviceImports = org.eclipse.equinox.region.Region,org.eclipse.equinox.region.RegionDigraph" +
                "\n" +
                "serviceExports = org.eclipse.virgo.kernel.install.artifact.InstallArtifactLifecycleListener,org.eclipse.virgo.kernel.install.artifact.ArtifactIdentityDeterminer" +
                "\n" +
                "initialArtifacts=repository:plan/org.eclipse.virgo.kernel.userregion.blueprint,repository:plan/org.eclipse.virgo.web.tomcat"

        when: "a writer flushes new config to the file"
        new VirgoUserRegionPropertiesWriter(configProps)
            .add(new UserRegionConfig()
                .initialArtifactsOverwrite(true)
                .initialArtifact(new Artifact("plan", "com.acme.plan"))
                .initialArtifact(new Artifact("plan", "com.acme.plan.versioned", "1.2.3.PLAN"))
                .initialArtifact(new Artifact("par", "com.acme.par"))
                .initialArtifact(new Artifact("par", "com.acme.par.versioned", "1.2.3.PAR"))
                .initialArtifact(new Artifact("bundle", "com.acme.bundle"))
                .initialArtifact(new Artifact("bundle", "com.acme.bundle.versioned", "1.2.3.BUNDLE"))
                .initialArtifact(new Artifact("configuration", "com.acme.properties"))
                .initialArtifact(new Artifact("configuration", "com.acme.properties.versioned", "1.2.3.CONFIGURATION")))
            .writeTo(configProps)

        then: "resulting properties file is a combination of existing + new"
        Properties resultingProps = new Properties()
        resultingProps.load(configProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                 | value
                              // existing
        "baseBundles"       | "file:plugins/org.eclipse.equinox.cm_1.0.400.v20120319-2029.jar@start," +
                              "file:plugins/org.eclipse.virgo.kernel.userregion_3.6.3.RELEASE.jar@start"
        "bundleImports"     | "org.eclipse.osgi;bundle-version=\"0\""
        "packageImports"    | "org.eclipse.virgo.kernel.artifact.*;version=\"0\"," +
                              "org.eclipse.virgo.nano.core;version=\"0\""
        "serviceImports"    | "org.eclipse.equinox.region.Region," +
                              "org.eclipse.equinox.region.RegionDigraph"
        "serviceExports"    | "org.eclipse.virgo.kernel.install.artifact.InstallArtifactLifecycleListener," +
                              "org.eclipse.virgo.kernel.install.artifact.ArtifactIdentityDeterminer"
        "initialArtifacts"  | // overwritten with only ours
                              "repository:plan/com.acme.plan," +
                              "repository:plan/com.acme.plan.versioned/1.2.3.PLAN," +
                              "repository:par/com.acme.par," +
                              "repository:par/com.acme.par.versioned/1.2.3.PAR," +
                              "repository:bundle/com.acme.bundle," +
                              "repository:bundle/com.acme.bundle.versioned/1.2.3.BUNDLE," +
                              "repository:configuration/com.acme.properties," +
                              "repository:configuration/com.acme.properties.versioned/1.2.3.CONFIGURATION"
    }

    def "initialArtifacts (merge entries)"() {
        given: "file contains existing user region configuration"
        configProps  <<
                "initialArtifacts=repository:plan/org.eclipse.virgo.kernel.userregion.blueprint,repository:plan/org.eclipse.virgo.web.tomcat"

        when: "a writer flushes config containing items already listed"
        new VirgoUserRegionPropertiesWriter(configProps)
            .add(new UserRegionConfig()
                .initialArtifact(new Artifact("plan", "org.eclipse.virgo.kernel.userregion.blueprint")) // this one already exists (don't duplicate)
                .initialArtifact(new Artifact("plan", "org.eclipse.virgo.web.tomcat", "1.2.3.RELEASE")) // note: with this one we specify a version not previously specified
                .initialArtifact(new Artifact("plan", "com.acme.plan"))) // something new
            .writeTo(configProps)

        then: "resulting properties file contains the merge"
        Properties resultingProps = new Properties()
        resultingProps.load(configProps.newDataInputStream())

        expect:
        resultingProps.get(key) == value

        where:
        key                 | value
        "initialArtifacts"  | "repository:plan/org.eclipse.virgo.kernel.userregion.blueprint," +
                              "repository:plan/org.eclipse.virgo.web.tomcat/1.2.3.RELEASE," + // note: version added
                              "repository:plan/com.acme.plan" // new

    }
}
