package com.jgriff.gradle.plugins.virgo.extensions

import org.gradle.api.NamedDomainObjectContainer

/**
 * The root "virgo" extension object.
 *
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class VirgoExtension {

    NamedDomainObjectContainer<VirgoAssemblyDescriptor> assemblies

    VirgoExtension(NamedDomainObjectContainer<VirgoAssemblyDescriptor> assemblies) {
        this.assemblies = assemblies
    }

    void assemblies(Closure closure) {
        assemblies.configure(closure)
    }
}
