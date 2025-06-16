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

import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.service.DecompileService;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.task.AbstractTask;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Enaium
 * @since 0.10.0
 */
public class SaveAllSourceTask extends AbstractTask<Boolean> {
    private final Jar jar;
    private final File out;

    public SaveAllSourceTask(Jar jar, File out) {
        super("SaveAllSource");
        this.jar = jar;
        this.out = out;
    }

    @Override
    public Boolean get() {
        float loaded = 0;
        float files = jar.getClassSize() + jar.getResourceSize();
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(out.toPath()));

            for (ClassNode value : jar.getClasses()) {
                if (value.getOuterClassInternalName() != null) {
                    setProgress((int) ((loaded++ / files) * 100f));
                    continue;
                }
                String name = value.getInternalName() + ".java";
                name = "src/main/java/" + name;
                zipOutputStream.putNextEntry(new ZipEntry(name));
                zipOutputStream.write(DecompileService.getService().decompile(value).getBytes(StandardCharsets.UTF_8));
                setProgress((int) ((loaded++ / files) * 100f));
            }

            for (Map.Entry<String, byte[]> stringEntry : jar.getResources().entrySet()) {
                String name = stringEntry.getKey();
                name = "src/main/resources/" + name;
                zipOutputStream.putNextEntry(new JarEntry(name));
                zipOutputStream.write(stringEntry.getValue());
                setProgress((int) ((loaded++ / files) * 100f));
            }
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (IOException ex) {
            MessageUtil.error(ex);
            return false;
        }
        return true;
    }
}
