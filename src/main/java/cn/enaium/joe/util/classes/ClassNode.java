package cn.enaium.joe.util.classes;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public interface ClassNode {
    String getInternalName();
    String getCanonicalName();
    byte[] getClassBytes();
    org.objectweb.asm.tree.ClassNode getClassNode();

    static ClassNode of(final byte[] classIn){
        ClassReader classReader = new ClassReader(classIn);
        final org.objectweb.asm.tree.ClassNode classNode = new org.objectweb.asm.tree.ClassNode();
        classReader.accept(classNode, 0);

        final String internalName = classNode.name;
        final String canonicalName = internalName.replace('/', '.');

        return new ClassNode() {
            @Override
            public String getInternalName() {
                return internalName;
            }

            @Override
            public String getCanonicalName() {
                return canonicalName;
            }

            @Override
            public byte[] getClassBytes() {
                return classIn;
            }

            @Override
            public org.objectweb.asm.tree.ClassNode getClassNode() {
                return classNode;
            }
        };
    }

    static ClassNode of(final org.objectweb.asm.tree.ClassNode classNode){
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        final byte[] bytes = classWriter.toByteArray();

        final String internalName = classNode.name;
        final String canonicalName = internalName.replace('/', '.');

        return new ClassNode() {
            @Override
            public String getInternalName() {
                return internalName;
            }

            @Override
            public String getCanonicalName() {
                return canonicalName;
            }

            @Override
            public byte[] getClassBytes() {
                return bytes;
            }

            @Override
            public org.objectweb.asm.tree.ClassNode getClassNode() {
                return classNode;
            }
        };
    }
}
