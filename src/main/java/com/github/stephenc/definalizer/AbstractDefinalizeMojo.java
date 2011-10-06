package com.github.stephenc.definalizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract base of the two definalizer mojos.
 */
public abstract class AbstractDefinalizeMojo extends AbstractMojo {
    /**
     * The class names to remove the final modifier from.
     *
     * @parameter
     */
    private String[] classNames;

    /**
     * @parameter expression="${classNames}"
     */
    private String commaSeparatedClassNames;

    protected void process(File outputDirectory) throws MojoExecutionException {
        Set<String> classNames = new TreeSet<String>();
        if (this.classNames != null) {
            classNames.addAll(Arrays.asList(this.classNames));
        }
        if (StringUtils.isNotEmpty(commaSeparatedClassNames)) {
            classNames.addAll(Arrays.asList(commaSeparatedClassNames.split(",")));
        }

        getLog().info("Definalizing...");

        List<String> missing = new ArrayList<String>();

        for (String className : classNames) {
            className = StringUtils.trim(className);
            File classFile = new File(outputDirectory, className.replace('.', '/') + ".class");
            if (classFile.isFile()) {
                getLog().debug("Reading " + classFile);
                byte[] byteCode;
                FileInputStream input = null;
                try {
                    input = new FileInputStream(classFile);
                    byteCode = IOUtil.toByteArray(input);
                } catch (IOException e) {
                    throw new MojoExecutionException("Could not read " + classFile, e);
                } finally {
                    IOUtil.close(input);
                }
                getLog().debug("Processing " + classFile);
                ClassReader reader = new ClassReader(byteCode);
                ClassWriter writer = new ClassWriter(0);

                String internalName = className.replace('.', '/');
                getLog().debug("Class name: " + className + " -> Internal name: " + internalName);
                DefinalizeAdapter adapter = new DefinalizeAdapter(writer);
                reader.accept(adapter, 0);
                if (Boolean.TRUE.equals(adapter.getChanged())) {
                    getLog().debug("Writing " + classFile);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(classFile);
                        IOUtil.copy(writer.toByteArray(), output);
                    } catch (IOException e) {
                        throw new MojoExecutionException("Could not read " + classFile, e);
                    } finally {
                        IOUtil.close(output);
                    }
                    getLog().info("  " + className + " definalized successfully");
                } else if (Boolean.FALSE.equals(adapter.getChanged())) {
                    getLog().info("  " + className + " definalized already");
                } else {
                    getLog().warn("  " + className + " does not appear to be a class file");
                }
                getLog().debug("Done " + classFile);
            } else {
                getLog().debug("Cannot find " + classFile);
                missing.add(className);
            }
        }
        getLog().info("Done.");
        if (!missing.isEmpty()) {
            throw new MojoExecutionException(
                    "Could not find the following in " + outputDirectory + " classes to definalize: " + missing);
        }
    }
}
