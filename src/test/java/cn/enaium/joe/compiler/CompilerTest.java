/*
 * Copyright 2022 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.joe.compiler;

import cn.enaium.joe.util.classes.ASMClassLoader;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.compiler.Compiler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Enaium
 */
class CompilerTest {
    @Test
    public void compile() {
        StringWriter stringWriter = new StringWriter();
        final byte[] clazz = Compiler.compileSingle("CompilerTestFooClass", "public class CompilerTestFooClass { public static boolean foo() { return CompilerTestFooClass.class.getName().equals(\"CompilerTestFooClass\"); } }", stringWriter);
        if (!stringWriter.toString().isEmpty()) {
            System.out.println(stringWriter);
        }
        Assertions.assertNotNull(clazz);
        StringWriter out = new StringWriter();
        ClassNode.of(clazz).trace(new TraceClassVisitor(new PrintWriter(out)));
        Assertions.assertTrue(() -> {
            try {
                return (boolean) new ASMClassLoader().defineClass("CompilerTestFooClass", clazz).getMethod("foo").invoke(null);
            } catch (Throwable throwable) {
                StringWriter stringWriter1 = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stringWriter1));
                System.out.println(stringWriter1);
                return false;
            }
        });
    }
}