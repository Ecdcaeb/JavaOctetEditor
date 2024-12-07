package cn.enaium.joe.util.classes;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ClassNode {
    String getInternalName();
    String getCanonicalName();
    byte[] getClassBytes();
    org.objectweb.asm.tree.ClassNode getClassNode();
    void accept(ClassNode newNode);
    void updateBytes();
    default String getSimpleName(){
        return this.getInternalName().substring(this.getInternalName().lastIndexOf('/') + 1);
    }

    default String getCanonicalPackageName(){
        return this.getCanonicalName().substring(0, this.getInternalName().lastIndexOf('.') - 1);
    }

    default String getInternalPackageName(){
        return this.getInternalName().substring(0, this.getInternalName().lastIndexOf('/') - 1);
    }

    default String getSuperClass(){
        return this.getClassNode().superName;
    }

    default String[] getInterfaces(){
        return this.getClassNode().interfaces.toArray(String[]::new);
    }

    default int getAccess(){
        return this.getClassNode().access;
    }

    default int getVersion(){
        return this.getClassNode().version;
    }

    default MethodNode[] getMethods(){
        return this.getClassNode().methods.toArray(MethodNode[]::new);
    }

    default FieldNode[] getFields(){
        return this.getClassNode().fields.toArray(FieldNode[]::new);
    }

    default void trace(TraceClassVisitor traceClassVisitor){
        this.getClassNode().accept(traceClassVisitor);
    }

    default void analyzeVisitor(ClassVisitor classVisitor){
        this.getClassNode().accept(classVisitor);
    }

    default void editVisitor(ClassVisitor classVisitor){
        this.getClassNode().accept(classVisitor);
        this.updateBytes();
    }

    default Set<String> getParents(){
        Set<String> parent = new HashSet<>();
        if (this.getSuperClass() != null && !"java/lang/Object".equals(this.getSuperClass())) {
            parent.add(this.getSuperClass());
        }
        parent.addAll(List.of(this.getInterfaces()));
        return parent;
    }

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
        public void updateBytes() {ClassWriter classWriter = new ClassWriter(0);
            getClassNode().accept(classWriter);
            this.clazz = classWriter.toByteArray();
        }

        @Override
        public void accept(ClassNode newNode) {
            this.internalName = newNode.getInternalName();
            this.canonicalName = newNode.getCanonicalName();
            this.clazz = newNode.getClassBytes();
            this.node = newNode.getClassNode();
        }

        @Override
        public ClassNode clone() {
            return ClassNode.of(this.getClassBytes().clone());
        }
    }
}
