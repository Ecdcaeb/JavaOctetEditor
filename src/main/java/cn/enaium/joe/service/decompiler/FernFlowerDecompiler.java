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

package cn.enaium.joe.service.decompiler;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.config.extend.FernFlowerConfig;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.ReflectUtil;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.util.DataInputFullStream;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

/**
 * @author Enaium
 * @since 1.0.0
 */
public class FernFlowerDecompiler extends IFernflowerLogger implements IDecompiler, IResultSaver {

    private String returned;
    public static Map<String, Object> customProperties;

    public static void updateCustomProperties(){
        customProperties = Collections.unmodifiableMap(new HashMap<>() {{
            JavaOctetEditor.getInstance().config.getConfigMapStrings(FernFlowerConfig.class).forEach((k, v) -> {
                if (v.equals("true")) {
                    v = "1";
                } else if (v.equals("false")) {
                    v = "0";
                }
                this.put(k, v);
            });
        }});
    }

    @Override
    public String decompile(ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);

        Fernflower fernflower = new Fernflower(this, FernFlowerDecompiler.customProperties, this);

        try {
            //TODO : make faster
            Map<String, StructClass> loader = ReflectUtil.getFieldValue(ReflectUtil.getFieldValue(fernflower, "structContext"), "classes");
            StructClass structClass = StructClass.create(new DataInputFullStream(classWriter.toByteArray()), true);
            loader.put(classNode.name, structClass);
            fernflower.decompileContext();
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            MessageUtil.error(e);
        }
        return returned;
    }

    @Override
    public void saveFolder(String path) {

    }

    @Override
    public void copyFile(String source, String path, String entryName) {

    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        returned = content;
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {

    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {

    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {

    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {

    }

    @Override
    public void closeArchive(String path, String archiveName) {

    }

    @Override
    public void writeMessage(String message, Throwable t) {
        MessageUtil.error(message, t);
    }

    @Override
    public void writeMessage(String message, Severity severity) {
        MessageUtil.error(severity.prefix + message);
    }

    @Override
    public void writeMessage(String message, Severity severity, Throwable t) {
        MessageUtil.error(message, t);
    }

    static {
        updateCustomProperties();
    }
}
