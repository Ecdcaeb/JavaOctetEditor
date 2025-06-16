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

package cn.enaium.joe.util.task.tasks;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.config.extend.ApplicationConfig;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.IOUtil;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.Util;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.task.AbstractTask;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Enaium
 * @since 0.10.0
 */
public class InputJarTask extends AbstractTask<Jar> {
    private final File file;

    public InputJarTask(File file) {
        super("InputJar");
        this.file = file;
    }

    @Override
    public Jar get() {
        Logger.info("LOAD:{}", file.getAbsolutePath());
        Jar jar = new Jar();
        try {
            if (file.isFile()){
                ZipFile jarFile = new ZipFile(file);
                float loaded = 0;
                float files = Util.countFiles(jarFile);


                var entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().endsWith(".class")) {
                        jar.putClassNode(ClassNode.of(IOUtil.getBytes(jarFile.getInputStream(jarEntry))));
                    } else if (!jarEntry.isDirectory()) {
                        jar.putResource(jarEntry.getName(), IOUtil.getBytes(jarFile.getInputStream(jarEntry)));
                    }
                    setProgress((int) ((loaded++ / files) * 100f));
                }
                jarFile.close();
            } else {
                Path root = file.toPath();
                try (Stream<Path> walkStream = Files.walk(root)){
                    Iterator<Path> itr = walkStream.filter(Files::isReadable).iterator();
                    List<Path> paths = new LinkedList<>();
                    while (itr.hasNext()) {
                        paths.add(itr.next());
                    }
                    float loaded = 0;
                    float files = paths.size();
                    for (Path path : paths) {
                        String relative = root.relativize(path).toString();
                        if (relative.endsWith(".class")) {
                            jar.putClassNode(ClassNode.of(IOUtil.getBytes(Files.newInputStream(path))));
                        } else if (!Files.isDirectory(path)) {
                            jar.putResource(relative, IOUtil.getBytes(Files.newInputStream(path)));
                        }
                        setProgress((int) ((loaded++ / files) * 100f));
                    }
                }
            }
        } catch (IOException e) {
            MessageUtil.error("Could not input jar!", e);
            return null;
        }

        JavaOctetEditor.getInstance().CONFIG.getByClass(ApplicationConfig.class).loadRecent.getValue().add(file.getAbsolutePath());
        JavaOctetEditor.getInstance().setJar(jar);
        JavaOctetEditor.getInstance().window.setTitle(JavaOctetEditor.TITLE + "-" + file.getName());
        return jar;
    }
}
