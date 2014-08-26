package com.jgriff.gradle.plugins.virgo

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class TemporaryFolderSpec extends Specification {
    @Rule
    TemporaryFolder tempDir = new TemporaryFolder()

    File file(path) {
        File file = new File(tempDir.getRoot(), path.toString())
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
        }
        file
    }

    void fileExists(path) {
        File file = new File(tempDir.getRoot(), path.toString())
        assert file.exists()
    }

    void fileDoesNotExist(path) {
        File file = new File(tempDir.getRoot(), path.toString())
        assert !file.exists()
    }
}
