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
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.classes.JarHelper;
import cn.enaium.joe.util.config.util.CachedGlobalValue;
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.tinylog.Logger;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Manifest;

/**
 * @author Enaium
 * @since 1.0.0
 */
public class VineFlowerDecompiler extends IFernflowerLogger implements IDecompiler, IResultSaver, IContextSource, IContextSource.IOutputSink {
    private String returned;
    private HashMap<String, ClassNode> activeClass;
    public static final CachedGlobalValue<Map<String, Object>> customProperties = new CachedGlobalValue<>(config -> {
        Map<String, String> map = JavaOctetEditor.getInstance().CONFIG.getConfigMapStrings(config);
        HashMap<String, Object> hashMap = new HashMap<>(map.size());
        for(Map.Entry<String, String> entry : map.entrySet()){
            String v = entry.getValue();
            if (v.equals("true")) {
                v = "1";
            } else if (v.equals("false")) {
                v = "0";
            }
            hashMap.put(entry.getKey(), v);
        }
        return Collections.unmodifiableMap(hashMap);});

    @Override
    public String decompile(final ClassNode classNode) {
        returned = null;
        activeClass = JarHelper.getAllNodes(classNode);
        BaseDecompiler baseDecompiler = new BaseDecompiler(this, customProperties.getValue(), this);
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
        return new Entries(activeClass.keySet().stream().map(Entry::atBase).toList(), List.of(), List.of());
    }

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public InputStream getInputStream(String resource) {
        if (activeClass.containsKey(resource)) return new ByteArrayInputStream(activeClass.get(resource).getClassBytes());
        return null;
    }

    @Override
    public byte[] getClassBytes(String className) {
        if (activeClass.containsKey(className)) return activeClass.get(className).getClassBytes();
        return null;
    }

    @Override
    public boolean hasClass(String className) {
        return activeClass.containsKey(className);
    }

    @Override
    public IOutputSink createOutputSink(IResultSaver saver) {
        return this;
    }

    @Override
    public void acceptClass(String qualifiedName, String fileName, String content, int[] mapping) {
        this.saveClassFile(null, null, null, content, mapping);
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