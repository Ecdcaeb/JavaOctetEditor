package cn.enaium.joe.util.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ClassVersionVisitor extends ClassVisitor {
    public final int version;
    public ClassVersionVisitor(int jdkVersion) {
        super(Opcodes.ASM9);
        this.version = jdkVersion;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(this.version, access, name, signature, superName, interfaces);
    }

    public static byte[] processVersion(byte[] clazz, int version){
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(new ClassVersionVisitor(version), 0);
        ClassWriter writer = new ClassWriter(classReader, 0);
        return writer.toByteArray();
    }
}
