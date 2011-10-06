package com.github.stephenc.definalizer;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ACC_FINAL;

/**
 * Makes a final class not so final
 */
public class DefinalizeAdapter extends ClassAdapter {

    /**
     * If we made a change, or not, or did nothing.
     */
    private Boolean changed = null;

    /**
     * {@inheritDoc}
     */
    public DefinalizeAdapter(ClassWriter destination) {
        super(destination);
    }

    /**
     * {@inheritDoc}
     */
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ((access & ACC_FINAL) != 0) {
            changed = Boolean.TRUE;
            access = access & ~ACC_FINAL;
        } else {
            changed = Boolean.FALSE;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * Returns the change status, {@link Boolean#TRUE} implies we removed a final, {@link Boolean#FALSE} implies
     * we the class was not final already, and {@code null} implies we have done nothing at all.
     *
     * @return the change status, {@link Boolean#TRUE} implies we removed a final, {@link Boolean#FALSE} implies
     *         we the class was not final already, and {@code null} implies we have done nothing at all.
     */
    public Boolean getChanged() {
        return changed;
    }
}
