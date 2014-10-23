# Gradle Virgo Plugin

A [Gradle](www.gradle.org) plugin for easily assembling your own custom distribution of [Eclipse Virgo](http://www.eclipse.org/virgo/).  Essentially, this plugin copies a base Virgo directory (you supply) and adds/modifies it based on your configuration(s).  Handy for distributing your application prepackaged with Virgo.

[![Build Status](https://drone.io/github.com/jgriff/gradle-virgo-plugin/status.png)](https://drone.io/github.com/jgriff/gradle-virgo-plugin/latest)
[![semver](http://img.shields.io/:semver-0.0.2-blue.svg) ](http://semver.org)
[![Download](https://api.bintray.com/packages/jgriff/maven/gradle-virgo-plugin/images/download.png) ](https://bintray.com/jgriff/maven/gradle-virgo-plugin/_latestVersion)
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/jgriff/gradle-virgo-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Plugin ID | Description
--------- | ------------
`com.github.jgriff.virgo.assembly` | Assemble a Virgo distribution directory by adding/customizing repositories, plans, and configuring the user region.


## Basic Example
```groovy
apply plugin: 'com.github.jgriff.virgo.assembly'

buildscript {
    dependencies {
        classpath("com.github.jgriff.gradle:gradle-virgo-plugin:0.0.2")
    }
}

virgo {
    assemblies {
        acmeVirgo {
            from unzipVirgo
            into "$buildDir/prepare/virgo"

            // add artifacts to Virgo repositories
            repositories {
                stdExt {
                    name 'ext'
                    artifacts {
                        from configurations.myBundles
                    }
                }
            }

            // create plan files
            plans {
                acme {
                    name 'com.acme'
                    version project.version

                    configuration name: "com.acme.config", version: "0"
                    bundle "com.acme.api"
                    bundle "com.acme.impl"
                }
            }
        }
    }
}
```
The plugin creates a task named `assemble<AssemblyName>` for each assembly declared, and adds it as a dependency to the built-in `assemble` task.   For example, the above configuration creates a task named `assembleAcmeVirgo`.

You must provide the base content to copy from (usually a freshly unzipped distribution of Virgo), and (optionally) a destination directory to assemble into.  If you do not specify a destination, it will default to `project.buildDir/virgo/<assemblyName>`.  The plugin adds all additional content and customizations to the destination directory.

**Note:** *The task does not zip the destination directory, that is left to you.*

You can configure any number of assemblies in the `assemblies` closure.

## Assembly Base Content

As mentioned, you are responsible for providing the base Virgo content for which this plugin *adds* to.
Each assembly configuration implements all of the [`CopySpec`](http://www.gradle.org/docs/current/javadoc/org/gradle/api/file/CopySpec.html) operations.  That is, whatever you can declare in a [`Copy`](http://www.gradle.org/docs/current/dsl/org.gradle.api.tasks.Copy.html) task, you can declare at the root of an assembly.

```groovy
virgo {
    assemblies {
        acmeVirgo {
            from unzipVirgo // another task in our build that unzips Virgo

            // include some other content
            from ('build/some/other/dir') {
               include '**/*.properties'
               include '**/*.xml'
               filter(ReplaceTokens, tokens: [version: '2.3.1'])
            }

            // inline a child CopySpec (or reference one defined elsewhere)
            with copySpec { from 'build/using/child/copyspec' }

            // destination
            into "$buildDir/prepare/virgo"

            // ...and so on (see CopySpec for full detail)
            exclude '**/*.bak'
            includeEmptyDirs = false
        }
    }
}
```

## Declaring Repositories

The `repositories` closure defines all of the repositories you want to add content to.  It also handles updating the required `configuration/org.eclipse.virgo.repository.properties` file to tell Virgo which repositories to use.

Each repository closure takes the form:
```groovy
repoName {
    name 'repoName' (optional, defaults to closure label)
    type ['external'|'watched'|'remote'] (optional, defaults to 'external')
    uri 'http://somewhere.net/other/virgo/hosted/repository' (only applies to 'remote' repos)

    // CopySpec closure, defines artifacts to add to the repository directory (does not apply to 'remote' repos)
    artifacts {
        from 'build/add-to-my-watched'
    }
}
```

You can add to Virgo's existing repositories simply by using the same name of the existing repo (ie, 'ext').  *However, the 'ext' closure is special to Gradle so you must use the `name` property to declare it (as below).*
Any repositories that do not already exist are created and configured for you.
```groovy
virgo {
    assemblies {
        acmeVirgo {
            from unzipVirgo
            into 'build/prepare/virgo'

            repositories {
                stdExt {
                    name 'ext' // note: you cannot name the closure "ext" b/c that's eaten by gradle as "extension properties"
                    artifacts {
                        from 'build/add-to-ext'
                    }
                }
                myext { // this creates a 'external' repository named 'myext'
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
        }
    }
}
```

### Repository Chain
The repository `chain` property in the `configuration/org.eclipse.virgo.repository.properties` file will be *automatically* appended with any new repositories you declare.  Since gradle doesn't preserve order of the closures, they will be appended in alphabetical order (for reproduceability).  

However, you can explicitly control which repositories are listed and their order in the chain using the `repositoryChain` property.  The example below will append `myext,mywatched,myremote` to the end of the chain.
```groovy
virgo {
    assemblies {
        acmeVirgo {
            ...
            repositories {
              ...
            }
            repositoryChain 'myext,mywatched,myremote'
        }
    }
}
```
By default, your repositories are appended to the end of the existing `chain`.  However, if you want to overwrite the entire `chain`, you can set the `repositoryChainOverwrite` property to `true` (default is `false`).  This is useful if you want to insert your repositories in between existing ones, or otherwise reorder the chain (ex: put all "external" repositories in front of "watched" and "remote" repositories).

```groovy
virgo {
    assemblies {
        acmeVirgo {
            ...
            repositories {
              ...
            }
            repositoryChain 'ext,myext,mywatched,usr,myremote'
            repositoryChainOverwrite true // overwrite chain with above (default is to append, 'false')
        }
    }
}
```
**Note:** *This will overwrite the chain completely.*

## Creating Plans

The plugin can also create Virgo `plan` files.  The files are named `"<name>-<version>.plan"`  Each `plan` closure takes the form:

```groovy
planLabel {
    name 'com.acme' // plan 'name' attribute (required)
    version project.version // plan 'version' attribute (optional, defaults to 'project.version')
    scoped true // plan 'scoped' attribute (optional, defaults to 'false')
    atomic true // plan 'atomic' attribute (optional, defaults to 'false')

    // directory relative to the assembly's destination root directory to drop this plan (optional, defaults to '/pickup' directory)
    into 'some/other/dir'

    // the rest is an ordered list of artifacts to include in the plan (can be any of ['configuration'|'bundle'|'plan'|'par'])
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
```

### Example
Here's a simple example, leveraging the sensible defaults used by the plugin.
```groovy
version 1.0.0.RELEASE

virgo {
    assemblies {
        acmeVirgo {
            ...
            plans {
                web {
                    name 'com.acme.plan'

                    configuration name: "com.acme.config", version: "0"
                    bundle "com.acme.api"
                    bundle "com.acme.core"
                    bundle "com.acme.web"
                }
            }
        }
    }
}
```
This produces the following plan file named `com.acme.plan-1.0.0.RELEASE.plan` located in the `/pickup` directory of the assembly's destination directory.

```xml
<plan name="com.acme.plan" scoped="false" atomic="false"
      xmlns="http://www.eclipse.org/virgo/schema/plan"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.eclipse.org/virgo/schema/plan
                          http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd"/>

    <artifact type="configuration" name="com.acme.config" version="0"/>
    <artifact type="bundle" name="com.acme.api" version="1.0.0.RELEASE"/>
    <artifact type="bundle" name="com.acme.core" version="1.0.0.RELEASE"/>
    <artifact type="bundle" name="com.acme.web" version="1.0.0.RELEASE"/>
</plan>
```
You can create any number of plans in the `plans { ... }` closure.

## Configuring the User Region

The plugin also supports easily modifying the user region configuration file `configuration/org.eclipse.virgo.kernel.userregion.properties` with the `userRegion` closure.
```groovy
userRegion {
  ...
}

```
Currently, only `initialArtifacts` are supported.
### Initial Artifacts
To add artifacts to the `initialArtifacts` list, you declare them using the same syntax and full range of options available for declaring `plan` files (above).  

For example, here we make sure the Virgo Snaps plan is added to the `initialArtifacts`.

```groovy
userRegion {
    initialArtifacts {
        plan "org.eclipse.virgo.snaps"
    }
}

```
The plugin will take care of preventing duplicate entries, so if `repository:plan/org.eclipse.virgo.snaps` was already listed it won't be added a second time.

Also, if you are declaring an artifact that already exists in the file, the version (or omission thereof) you specify will override whatever current version is specified for the artifact.  In other words, you declarations will override any already existing ones (but does not change their order in the list).

#### Overwriting the Initial Artifacts
Just like the **repository chain** supports overwriting, you can set the `overwrite` property on the `initialArtifacts` closure to have complete control over what is listed.

```groovy
userRegion {
    initialArtifacts {
        overwrite true // defaults to 'false'
        ...
    }
}

```
When `overwrite` is `true`, only the artifacts you list will appear for the key `initialArtifacts` in the `configuration/org.eclipse.virgo.kernel.userregion.properties` file.
