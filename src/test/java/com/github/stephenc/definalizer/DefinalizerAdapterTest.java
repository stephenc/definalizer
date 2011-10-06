package com.github.stephenc.definalizer;

import org.codehaus.plexus.util.IOUtil;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class DefinalizerAdapterTest {
    @Test
    public void smokes() throws Exception {
        ClassReader r = new ClassReader(getBytes(getClass()));
        DefinalizeAdapter definalizeAdapter = new DefinalizeAdapter(new ClassWriter(0));
        assertThat(definalizeAdapter.getChanged(), is(nullValue()));
        r.accept(definalizeAdapter, 0);
        assertThat(definalizeAdapter.getChanged(), is(Boolean.FALSE));

        final class Foo {
        }
        r = new ClassReader(getBytes(Foo.class));
        definalizeAdapter = new DefinalizeAdapter(new ClassWriter(0));
        assertThat(definalizeAdapter.getChanged(), is(nullValue()));
        r.accept(definalizeAdapter, 0);
        assertThat(definalizeAdapter.getChanged(), is(Boolean.TRUE));
    }

    private byte[] getBytes(Class aClass) throws IOException {
        InputStream stream =
                getClass().getClassLoader().getResourceAsStream(Type.getType(aClass).getInternalName() + ".class");
        try {
            return IOUtil.toByteArray(stream);
        } finally {
            IOUtil.close(stream);
        }
    }


}
