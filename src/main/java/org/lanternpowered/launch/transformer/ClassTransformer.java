package org.lanternpowered.launch.transformer;

@FunctionalInterface
public interface ClassTransformer {

    byte[] transform(ClassLoader loader, String className, byte[] byteCode);
}
