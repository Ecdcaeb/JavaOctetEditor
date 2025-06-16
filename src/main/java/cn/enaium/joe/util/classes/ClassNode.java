package cn.enaium.joe.util.classes;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ClassNode {
    default String getInternalName(){
        return this.getNodeInternal().name;
    }
    default String getCanonicalName(){
        return this.getNodeInternal().name.replace('/', '.');
    }
    default ClassNode copy() {
        return ClassNode.of(this.getClassBytes());
    }
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

    default String[] getInnerClassesInternalName(){
        return this.getNodeInternal().innerClasses.stream().map((v) -> v.name).toArray(String[]::new);
    }

    default String[] getInnerClassesCanonicalName(){
        return this.getNodeInternal().innerClasses.stream().map((v) -> v.name.replace('/', '.')).toArray(String[]::new);
    }

    default String getSuperClass(){
        return this.getNodeInternal().superName;
    }

    default String[] getInterfaces(){
        return this.getNodeInternal().interfaces.toArray(String[]::new);
    }

    default int getAccess(){
        return this.getNodeInternal().access;
    }

    default int getVersion(){
        return this.getNodeInternal().version;
    }

    default MethodNode[] getMethods(){
        return this.getClassNode().methods.toArray(MethodNode[]::new);
    }

    default FieldNode[] getFields(){
        return this.getClassNode().fields.toArray(FieldNode[]::new);
    }

    default void trace(TraceClassVisitor traceClassVisitor){
        this.getNodeInternal().accept(traceClassVisitor);
    }

    default void analyzeVisitor(ClassVisitor classVisitor){
        this.getNodeInternal().accept(classVisitor);
    }

    default void editVisitor(ClassVisitor classVisitor){
        this.getNodeInternal().accept(classVisitor);
        this.updateBytes();
    }

    void mkdir();

    org.objectweb.asm.tree.ClassNode getNodeInternal();

    default Set<String> getParents(){
        Set<String> parent = new HashSet<>();
        if (this.getSuperClass() != null && !"java/lang/Object".equals(this.getSuperClass())) {
            parent.add(this.getSuperClass());
        }
        parent.addAll(List.of(this.getInterfaces()));
        return parent;
    }

    default String getOuterClassInternalName(){
        return this.getNodeInternal().outerClass;
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

    static ClassNode of(final String internalName) {
        org.objectweb.asm.tree.ClassNode classNode = new org.objectweb.asm.tree.ClassNode();
        try {
            new ClassReader(internalName).accept(classNode, 0);
        } catch (IOException e) {
            return null;
        }
        return of(classNode);
    }

    class Impl implements ClassNode{

        boolean isDir = false;

        String internalName; String canonicalName; byte[] clazz; org.objectweb.asm.tree.ClassNode node;
        public Impl(String internalName, String canonicalName, byte[] clazz, org.objectweb.asm.tree.ClassNode node){
            this.internalName = internalName;
            this.canonicalName = canonicalName;
            this.clazz = clazz;
            this.node = node;
        }

        @Override
        public byte[] getClassBytes() {
            if (this.isDir) updateBytes();
            return this.clazz;
        }

        @Override
        public org.objectweb.asm.tree.ClassNode getClassNode() {
            this.isDir = true;
            return this.node;
        }

        @Override
        public void updateBytes() {ClassWriter classWriter = new ClassWriter(0);
            node.accept(classWriter);
            this.clazz = classWriter.toByteArray();
            this.isDir = false;
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

        @Override
        public org.objectweb.asm.tree.ClassNode getNodeInternal() {
            return this.node;
        }

        @Override
        public void mkdir() {
            this.isDir = true;
        }
    }
}
