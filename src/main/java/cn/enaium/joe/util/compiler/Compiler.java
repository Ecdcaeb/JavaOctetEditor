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

package cn.enaium.joe.util.compiler;

import cn.enaium.joe.Main;
import cn.enaium.joe.util.Util;
import cn.enaium.joe.util.compiler.virtual.MemoryClassLoader;
import cn.enaium.joe.util.compiler.virtual.VirtualFileManager;
import cn.enaium.joe.util.compiler.virtual.VirtualJavaFileObject;

import javax.tools.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Enaium
 * @since 1.4.0
 */
public class Compiler {

    private final Map<String, VirtualJavaFileObject> javaFileObjectMap = new HashMap<>();
    private Map<String, byte[]> results = new HashMap<>();

    private DiagnosticListener<VirtualJavaFileObject> listener;


    public void addSource(String name, String content) {
        javaFileObjectMap.put(name, new VirtualJavaFileObject(name, content));
    }

    public Map<String, byte[]> getClasses() {
        return results;
    }

    public static byte[] compileSingle(String canonicalName, String text){
        Compiler compiler = new Compiler();
        compiler.addSource(canonicalName, text);
        CompileError compileError = compiler.compile();
        if (compileError != null) return null;
        return compiler.getClasses().get(canonicalName);
    }

    public static byte[] compileSingle(String canonicalName, String text, Writer errorTracer){
        Compiler compiler = new Compiler();
        compiler.addSource(canonicalName, text);
        CompileError compileError = compiler.compile();
        if (compileError != null) {
            compileError.printStackTrace(new PrintWriter(errorTracer));
            return null;
        }
        return compiler.getClasses().get(canonicalName);
    }

    public CompileError compile() {
        MemoryClassLoader memoryClassLoader = new MemoryClassLoader(Main.classLoader, null);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        VirtualFileManager fileManager = new VirtualFileManager(standardJavaFileManager, this.javaFileObjectMap, memoryClassLoader);
        try {
            StringWriter stringWriter = new StringWriter();
            JavaCompiler.CompilationTask task = compiler.getTask(stringWriter, fileManager, Util.cast(listener), null, null, javaFileObjectMap.values());
            if (task.call() == Boolean.TRUE) {
                this.results = fileManager.getClasses();
                return null;
            } else return new CompileError(stringWriter.toString());
        } catch (Exception e) {
            return new CompileError(e);
        }
    }

    public void setListener(DiagnosticListener<VirtualJavaFileObject> listener) {
        this.listener = listener;
    }


}
