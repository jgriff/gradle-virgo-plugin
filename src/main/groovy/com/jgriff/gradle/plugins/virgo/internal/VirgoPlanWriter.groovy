package com.jgriff.gradle.plugins.virgo.internal

import com.jamesmurty.utils.XMLBuilder
import com.jgriff.gradle.plugins.virgo.extensions.Plan

/**
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class VirgoPlanWriter {
    private XMLBuilder planBuilder

    /**
     * Attempts to build the xml plan file for the specified plan.  If this
     * method returns successfully, the plan passed to this method will be
     * what is written out in the next call to {@link #writeTo(java.io.File)}.
     * <p>
     * If unsuccessful (ie, raises exception), the internal builder is left
     * unchanged such that the last successful plan given to this method would
     * be the next one written by {@link #writeTo(java.io.File)}.
     *
     * @param plan The plan POJO to capture the file elements from.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoPlanWriter plan(Plan plan) {
        XMLBuilder builder = newBuilder()

        if (Utils.hasLength(plan.name)) { builder.a("name", plan.name) }
        if (Utils.hasLength(plan.version)) { builder.a("version", plan.version) }
        builder.a("scoped", String.valueOf(plan.scoped)).a("atomic", String.valueOf(plan.atomic))

        plan.artifacts.each {
            builder.e("artifact").a("type", it.type).a("name", it.name).a("version", it.version)
        }

        this.planBuilder = builder
        this
    }

    /**
     * Writes the last (successful) {@link #plan(com.jgriff.gradle.plugins.virgo.extensions.Plan)}
     * to the specified file.  If no previously successful call was made to {@link #plan(com.jgriff.gradle.plugins.virgo.extensions.Plan)},
     * this will raise an {@link IllegalStateException}.
     *
     * @param file The file to write to.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoPlanWriter writeTo(File file) {
        if (file == null) throw new IllegalArgumentException("A file is required.")
        if (planBuilder == null) throw new IllegalStateException("You must specify the plan to use by calling 'plan(Plan)'.")

        file.withWriter('UTF-8') { fileWriter ->
            planBuilder.toWriter(fileWriter, new Properties([indent: "yes"]))
        }
    }

    private XMLBuilder newBuilder() {
        XMLBuilder.create("plan")
            .a("xmlns", "http://www.eclipse.org/virgo/schema/plan")
            .a("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
            .a("xsi:schemaLocation", "http://www.eclipse.org/virgo/schema/plan " +
                                     "http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd")
    }
}
