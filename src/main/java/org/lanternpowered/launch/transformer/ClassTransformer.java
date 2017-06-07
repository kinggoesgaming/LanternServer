package org.lanternpowered.launch.transformer;

public interface ClassTransformer {

    void visit(ClassLoader loader, String className, Class<?> superClass, Class<?>[] interfaces);

    byte[] transform(ClassLoader loader, String className, byte[] byteCode);
}
