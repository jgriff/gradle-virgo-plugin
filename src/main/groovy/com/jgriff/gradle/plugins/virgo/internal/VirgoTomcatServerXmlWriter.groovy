package com.jgriff.gradle.plugins.virgo.internal

import com.jamesmurty.utils.XMLBuilder

/**
 * @author <a href="mailto:justin.griffin@ngc.com">Justin Griffin</a>
 * @since 0.1
 */
class VirgoTomcatServerXmlWriter {
    private final XMLBuilder builder

    /**
     * Constructs this instance with an initially empty "<code>&lt;Server&gt;</code>" document.
     *
     * @since 0.1
     */
    VirgoTomcatServerXmlWriter() {
        builder = XMLBuilder.create("Server");
    }

    VirgoTomcatServerXmlWriter(File file) {
        builder = XMLBuilder.parse(file)
    }

    /**
     * Writes the current state of the "<code>&lt;Server&gt;</code>" document in this object to
     * the specified file.
     *
     * @param file The file to write to.
     * @return this instance, for method chaining
     * @since 0.1
     */
    VirgoTomcatServerXmlWriter writeTo(File file) {
        if (file != null && builder != null) {
            file.withWriter('UTF-8') { fileWriter ->
                builder.toWriter(fileWriter, new Properties(["indent": "yes"]))
            }
        }
    }
}
