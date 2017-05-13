package org.lanternpowered.launch.transformer.at;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import javax.annotation.Nullable;

enum AccessModifier {
    REMOVE_FINAL(0, true, null),
    PROTECTED_REMOVE_FINAL(ACC_PROTECTED, true, null),
    PROTECTED(ACC_PROTECTED, false, PROTECTED_REMOVE_FINAL),
    PUBLIC_REMOVE_FINAL(ACC_PUBLIC, true, null),
    PUBLIC(ACC_PUBLIC, false, PUBLIC_REMOVE_FINAL);

    private final int flag;
    private final boolean removeFinal;
    private final AccessModifier finalVariant;

    AccessModifier(int flag, boolean removeFinal, @Nullable AccessModifier finalVariant) {
        this.flag = flag;
        this.removeFinal = removeFinal;
        this.finalVariant = finalVariant != null ? finalVariant : this;
    }

    boolean hasAccessChange() {
        return this.flag != 0;
    }

    AccessModifier removeFinal() {
        return this.finalVariant;
    }

    AccessModifier merge(@Nullable AccessModifier modifier) {
        if (modifier == null || this == modifier) {
            return this;
        }

        if (this.flag == modifier.flag) {
            return this.removeFinal ? modifier.removeFinal() : modifier;
        }

        AccessModifier base = this.compareTo(modifier) > 0 ? this : modifier;
        return this.removeFinal ? base.removeFinal() : base;
    }

    int apply(int access) {
        if (hasAccessChange()) {
            // Don't allow lowering the access
            if (this.flag != ACC_PROTECTED || (access & ACC_PUBLIC) == 0) {
                // First remove the old access modifier, then add our new one
                access = ((access & ~7) | this.flag);
            }
        }

        if (this.removeFinal) {
            // Remove the final bit
            access &= ~ACC_FINAL;
        }

        return access;
    }

}
