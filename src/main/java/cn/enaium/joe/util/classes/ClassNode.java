package cn.enaium.joe.util.classes;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public interface ClassNode {
    String getInternalName();
    String getCanonicalName();
    byte[] getClassBytes();
    org.objectweb.asm.tree.ClassNode getClassNode();
    void accept(ClassNode newNode);

    static ClassNode of(final byte[] classIn){
        ClassReader classReader = new ClassReader(classIn);
        final org.objectweb.asm.tree.ClassNode classNode = new org.objectweb.asm.tree.ClassNode();
        classReader.accept(classNode, 0);

        final String internalName = classNode.name;
        final String canonicalName = internalName.replace('/', '.');

        return new Impl(internalName, canonicalName, classIn, classNode);
    }

    static ClassNode of(final org.objectweb.asm.tree.ClassNode classNode){
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        final byte[] bytes = classWriter.toByteArray();

        final String internalName = classNode.name;
        final String canonicalName = internalName.replace('/', '.');

        return new Impl(internalName, canonicalName, bytes, classNode);
    }

    class Impl implements ClassNode{
        String internalName; String canonicalName; byte[] clazz; org.objectweb.asm.tree.ClassNode node;
        public Impl(String internalName, String canonicalName, byte[] clazz, org.objectweb.asm.tree.ClassNode node){
            this.internalName = internalName;
            this.canonicalName = canonicalName;
            this.clazz = clazz;
            this.node = node;
        }

        @Override
        public String getInternalName() {
            return this.internalName;
        }

        @Override
        public String getCanonicalName() {
            return this.canonicalName;
        }

        @Override
        public byte[] getClassBytes() {
            return this.clazz;
        }

        @Override
        public org.objectweb.asm.tree.ClassNode getClassNode() {
            return this.node;
        }

        @Override
        public void accept(ClassNode newNode) {
            this.internalName = newNode.getInternalName();
            this.canonicalName = newNode.getCanonicalName();
            this.clazz = newNode.getClassBytes();
            this.node = newNode.getClassNode();
        }
    }
}
