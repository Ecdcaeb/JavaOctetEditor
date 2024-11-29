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

import cn.enaium.joe.util.compiler.environment.RecompileEnvironment;
import cn.enaium.joe.util.compiler.virtual.VirtualFileManager;
import cn.enaium.joe.util.compiler.virtual.VirtualJavaFileObject;

import javax.tools.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        compiler.compile();
        return compiler.getClasses().get(canonicalName);
    }

    @SuppressWarnings("unchecked")
    public boolean compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        VirtualFileManager fileManager = new VirtualFileManager(compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8), this.javaFileObjectMap, RecompileEnvironment.getEnvironment());
        try {
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, ((DiagnosticListener<? super JavaFileObject>) (Object) listener), null, null, javaFileObjectMap.values());
            Boolean b = task.call();
            if (b != null && b){
                this.results = fileManager.getClasses();
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void setListener(DiagnosticListener<VirtualJavaFileObject> listener) {
        this.listener = listener;
    }


}
