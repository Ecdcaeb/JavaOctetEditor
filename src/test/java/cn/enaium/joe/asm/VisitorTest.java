package cn.enaium.joe.asm;

import cn.enaium.joe.util.compiler.Compiler;
import cn.enaium.joe.util.ASMUtil;
import cn.enaium.joe.util.ImagineBreakerHelper;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertNull;

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
        Compiler compiler = new Compiler();
        String name = "asm." + this.getClass().getName() + "Dump";
        compiler.addSource(name, stringWriter.toString());
        assertNull(compiler.compile());
        ClassNode classNode = ASMUtil.acceptClassNode(new ClassReader(compiler.getClasses().get(name)));
        StringWriter out = new StringWriter();
        classNode.accept(new TraceClassVisitor(new PrintWriter(out)));
        System.out.println(out);
    }
}