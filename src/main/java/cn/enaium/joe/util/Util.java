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

package cn.enaium.joe.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Enaium
 */
public class Util {
    @SuppressWarnings("unchecked")
    public static<T> T cast(Object obj) {
        return (T)obj;
    }

    public static int countFiles(ZipFile zipFile) {
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        int c = 0;
        while (entries.hasMoreElements()) {
            entries.nextElement();
            ++c;
        }
        return c;
    }

    public static boolean isText(byte[] bytes) {
        int total = bytes.length;
        if (total >= 8000) {
            total = 8000;
        }
        for (int i = 0; i < total; i++) {
            if (((char) bytes[i]) == '\0') {
                return false;
            }
        }
        return true;
    }

    public static Dimension screenSize(int width, int height) {
        return screenSize(new Dimension(width, height));
    }

    public static Dimension screenSize(Dimension dimension) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension((int) (dimension.width * screenSize.getWidth() / 1920), (int) (dimension.height * screenSize.getHeight() / 1080));
    }

    public static AbstractAction ofAction(Runnable runnable) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        };
    }
}
