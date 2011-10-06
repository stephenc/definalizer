package com.github.stephenc.definalizer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Modifies the bytecode of test <code>.class</code> files to remove the final modifier from the class.
 *
 * @goal test-definalize
 * @phase process-test-classes
 */
public final class DefinalizeTestMojo extends AbstractDefinalizeMojo {

    /**
     * The directory for compiled classes.
     *
     * @parameter default-value="${project.build.testOutputDirectory}"
     * @required
     * @readonly
     */
    private File testOutputDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        process(testOutputDirectory);
    }

}
