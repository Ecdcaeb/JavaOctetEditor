package cn.enaium.joe.asm;

import cn.enaium.joe.util.compiler.Compiler;
import cn.enaium.joe.util.ASMUtil;
import cn.enaium.joe.util.ImagineBreakerHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Enaium
 */
class VisitorTest {
    static {
        ImagineBreakerHelper.boot();
    }

    @Test
    public void test() throws IOException {
        StringWriter stringWriter = new StringWriter();
        ClassReader classReader = new ClassReader(this.getClass().getName());
        classReader.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(stringWriter)), 0);
        String name = "asm." + this.getClass().getName() + "Dump";
        StringWriter errorTracer = new StringWriter();
        byte[] clazz = Compiler.compileSingle(name, stringWriter.toString(), errorTracer);
        if (!errorTracer.toString().isEmpty()) {
            System.out.println(errorTracer);
        }
        Assertions.assertNotNull(clazz);
        ClassNode classNode = ASMUtil.acceptClassNode(new ClassReader(clazz));
        StringWriter out = new StringWriter();
        classNode.accept(new TraceClassVisitor(new PrintWriter(out)));
        System.out.println(out);
    }
}