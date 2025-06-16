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
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.config.util.CachedGlobalValue;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.classes.ClassNode;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Manifest;

/**
 * @author Enaium
 * @since 1.0.0
 */
public class VineFlowerDecompiler implements IDecompiler{

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
        try(SingleClassResultSaver saver = new SingleClassResultSaver(classNode.getCanonicalName())){
            Fernflower fernflower = new Fernflower(saver, customProperties.getValue(), CustomLogger.LOGGER);

            fernflower.addLibrary(new JarContextSource(JavaOctetEditor.getInstance().getJar()));
            fernflower.addSource(new SingleClassContextSource(classNode));
            fernflower.addWhitelist(classNode.getInternalName());
            try {
                fernflower.decompileContext();
            } finally {
                fernflower.clearContext();
            }
            return saver.saved;
        } catch (IOException e) {
            return "Decompile Failed \n" + e;
        }
    }

    private static class JarContextSource implements IContextSource {

        Jar jar;

        public JarContextSource(Jar jar){
            this.jar = jar;
        }

        @Override
        public String getName() {
            return "Jar";
        }

        @Override
        public Entries getEntries() {
            return new Entries(jar.getClasses().stream().map(ClassNode::getInternalName).map(name -> name + ".class").map(Entry::atBase).toList(), Collections.emptyList(), Collections.emptyList());
        }

        @Override
        public boolean isLazy() {
            return true;
        }

        @Override
        public InputStream getInputStream(String resource) {
            String className = resource.endsWith(".class") ? resource.substring(0, resource.length() - 6) : resource;
            return hasClass(className) ? new ByteArrayInputStream(getClassBytes(className)) : null;
        }

        @Override
        public byte[] getClassBytes(String resource) {
            String className = resource.endsWith(".class") ? resource.substring(0, resource.length() - 6) : resource;
            return jar.getClassNode(className).getClassBytes();
        }

        @Override
        public boolean hasClass(String resource) {
            String className = resource.endsWith(".class") ? resource.substring(0, resource.length() - 6) : resource;
            return jar.hasClass(className);
        }

        @Override
        public IOutputSink createOutputSink(IResultSaver saver) {
            return new IOutputSink() {
                @Override
                public void begin() {

                }

                @Override
                public void acceptClass(String s, String s1, String s2, int[] ints) {

                }

                @Override
                public void acceptDirectory(String s) {

                }

                @Override
                public void acceptOther(String s) {

                }

                @Override
                public void close() {

                }
            };
        }
    }

    private record SingleClassContextSource(ClassNode targetClasses) implements IContextSource {
        @Override
        public String getName() {
            return JavaOctetEditor.TITLE;
        }

        @Override
        public Entries getEntries() {
            return new Entries(List.of(Entry.atBase(targetClasses.getInternalName() + ".class")), Collections.emptyList(), Collections.emptyList());
        }

        @Override
        public boolean isLazy() {
            return true;
        }

        @Override
        public InputStream getInputStream(String resource) {
            String className = resource.endsWith(".class")
                    ? resource.substring(0, resource.length() - 6).replace('/', '.')
                    : resource.replace('/', '.');
            return hasClass(className) ? new ByteArrayInputStream(Objects.requireNonNull(getClassBytes(className))) : null;
        }

        @Override
        public byte[] getClassBytes(String className) {
            if (className.equals(targetClasses.getInternalName())) {
                return targetClasses.getClassBytes();
            } else return null;
        }

        @Override
        public boolean hasClass(String className) {
            return className.equals(targetClasses.getInternalName());
        }

        @Override
        public IOutputSink createOutputSink(IResultSaver saver) {
            SingleClassResultSaver singleClassResultSaver = saver instanceof SingleClassResultSaver ? (SingleClassResultSaver) saver : null;
            return new IOutputSink() {
                @Override
                public void begin() {

                }

                @Override
                public void acceptClass(String s, String s1, String s2, int[] ints) {
                    if (singleClassResultSaver != null) {
                        singleClassResultSaver.saveClassFile(null, s, s1, s2, ints);
                    }
                }

                @Override
                public void acceptDirectory(String s) {

                }

                @Override
                public void acceptOther(String s) {

                }

                @Override
                public void close() {

                }
            };
        }
    }

    private static class SingleClassResultSaver implements IResultSaver {
        public final String targetClassName;
        public String saved;

        public SingleClassResultSaver(String targetClassName) {
            this.targetClassName = targetClassName;
            this.saved = null;
        }

        @Override
        public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
            if (qualifiedName.equals(targetClassName) && saved == null) {
                saved = content;
            }
        }

        @Override public void saveFolder(String path) {}
        @Override public void copyFile(String source, String path, String entryName) {}
        @Override public void createArchive(String path, String archiveName, Manifest manifest) {}
        @Override public void saveDirEntry(String path, String archiveName, String entryName) {}
        @Override public void copyEntry(String source, String path, String archiveName, String entry) {}
        @Override public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {}
        @Override public void closeArchive(String path, String archiveName) {}
    }

    private static class CustomLogger extends IFernflowerLogger {

        private static final CustomLogger LOGGER = new CustomLogger();

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
    }
}
