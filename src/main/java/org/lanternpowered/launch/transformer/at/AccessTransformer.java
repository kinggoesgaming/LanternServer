package org.lanternpowered.launch.transformer.at;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.launch.transformer.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;

public final class AccessTransformer implements ClassTransformer {

    private final ImmutableMap<String, ClassAccessModifiers> modifiers;

    public AccessTransformer()  {
        this.modifiers = AccessTransformers.build();
    }

    @Override
    @Nullable
    public byte[] transform(ClassLoader loader, String className, byte[] byteCode) {
        final ClassAccessModifiers modifiers = this.modifiers.get(className);
        if (modifiers == null) {
            return byteCode;
        }

        final ClassReader reader = new ClassReader(byteCode);
        final ClassWriter writer = new ClassWriter(reader, 0);
        reader.accept(new AccessTransformingClassAdapter(writer, modifiers), 0);
        return writer.toByteArray();
    }

    private static class AccessTransformingClassAdapter extends ClassVisitor {

        private final ClassAccessModifiers modifiers;
        @Nullable private String name;

        AccessTransformingClassAdapter(ClassVisitor cv, ClassAccessModifiers modifiers) {
            super(ASM5, cv);
            this.modifiers = modifiers;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.name = name;
            if (this.modifiers.modifier != null) {
                access = this.modifiers.modifier.apply(access);
            }
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            final AccessModifier modifier = this.modifiers.getField(name);
            if (modifier != null) {
                access = modifier.apply(access);
            }
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            final AccessModifier modifier = this.modifiers.getMethod(name, desc);
            if (modifier != null) {
                access = modifier.apply(access);
            }
            return new AccessTransformingMethodAdapter(super.visitMethod(access, name, desc, signature, exceptions), this);
        }
    }

    private static class AccessTransformingMethodAdapter extends MethodVisitor {

        private final AccessTransformingClassAdapter owner;

        AccessTransformingMethodAdapter(MethodVisitor mv, AccessTransformingClassAdapter owner) {
            super(ASM5, mv);
            this.owner = owner;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (!itf && opcode == INVOKESPECIAL && owner.equals(this.owner.name) && !name.equals("<init>")) {
                // Replace INVOKESPECIAL with INVOKEVIRTUAL to allow overriding (if we change the access)
                final AccessModifier modifier = this.owner.modifiers.getMethod(name, desc);
                if (modifier != null && modifier.hasAccessChange()) {
                    opcode = INVOKEVIRTUAL;
                }
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

}
