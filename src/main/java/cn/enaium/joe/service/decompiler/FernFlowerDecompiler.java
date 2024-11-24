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
import cn.enaium.joe.util.classes.ClassNode;
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Manifest;

/**
 * @author Enaium
 * @since 1.0.0
 */
public class FernFlowerDecompiler extends IFernflowerLogger implements IDecompiler, IResultSaver, IContextSource, IContextSource.IOutputSink {
    private String returned;
    private ClassNode activeClass;
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
    public String decompile(final ClassNode classNode) {
        returned = null;
        activeClass = classNode;
        BaseDecompiler baseDecompiler = new BaseDecompiler(this,customProperties, this);
        baseDecompiler.addSource(this);
        baseDecompiler.decompileContext();
        return returned;
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        if (returned == null){
            returned = content;
        }
    }

    @Override
    public void writeMessage(String message, Throwable t) {
        Logger.info(t, message);
    }

    @Override
    public void writeMessage(String message, Severity severity) {
        switch (severity){
            case INFO -> Logger.info(message);
            case WARN -> Logger.warn(message);
            case TRACE -> Logger.trace(message);
            case ERROR -> MessageUtil.error(message);
        }
    }

    @Override
    public void writeMessage(String message, Severity severity, Throwable t) {
        switch (severity){
            case INFO -> Logger.info(t, message);
            case WARN -> Logger.warn(t, message);
            case TRACE -> Logger.trace(t, message);
            case ERROR -> MessageUtil.error(message, t);
        }
    }

    @Override
    public String getName() {
        return JavaOctetEditor.TITLE;
    }

    @Override
    public Entries getEntries() {
        return new Entries(List.of(Entry.atBase(activeClass.getInternalName())), List.of(), List.of());
    }

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public InputStream getInputStream(String resource) {
        return new ByteArrayInputStream(activeClass.getClassBytes());
    }

    @Override
    public byte[] getClassBytes(String className) {
        return activeClass.getClassBytes();
    }

    @Override
    public boolean hasClass(String className) {
        return className.equals(activeClass.getInternalName()) || className.equals(activeClass.getCanonicalName());
    }

    @Override
    public IOutputSink createOutputSink(IResultSaver saver) {
        return this;
    }

    @Override
    public void acceptClass(String qualifiedName, String fileName, String content, int[] mapping) {
        this.saveClassFile(null, null, null, content, mapping);
    }

    static {
        updateCustomProperties();
    }

    @Override public void begin() {}
    @Override public void close() {}
    @Override public void acceptDirectory(String s) {}
    @Override public void acceptOther(String s) {}
    @Override public void saveFolder(String path) {}
    @Override public void copyFile(String source, String path, String entryName) {}
    @Override public void createArchive(String path, String archiveName, Manifest manifest) {}
    @Override public void saveDirEntry(String path, String archiveName, String entryName) {}
    @Override public void copyEntry(String source, String path, String archiveName, String entry) {}
    @Override public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {}
    @Override public void closeArchive(String path, String archiveName) {}
}
