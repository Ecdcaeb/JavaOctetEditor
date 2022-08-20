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

package cn.enaium.joe.gui.panel.menu.file;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.config.extend.ApplicationConfig;
import cn.enaium.joe.task.InputJarTask;
import cn.enaium.joe.util.LangUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Set;

/**
 * @author Enaium
 * @since 0.9.0
 */
public class LoadRecentMenu extends JMenuItem {
    public LoadRecentMenu() {
        super(LangUtil.i18n("menu.file.loadRecent"));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JPopupMenu jPopupMenu = new JPopupMenu();
                Set<String> loadRecent = JavaOctetEditor.getInstance().config.getByClass(ApplicationConfig.class).loadRecent.getValue();
                for (String s : loadRecent) {
                    jPopupMenu.add(new JMenuItem(s) {{
                        addActionListener(e -> {
                            System.out.println(s);
                            File file = new File(s);
                            if (file.exists()) {
                                JavaOctetEditor.getInstance().task.submit(new InputJarTask(file)).thenAccept(it -> JavaOctetEditor.getInstance().fileTreePanel.refresh(it));
                            } else {
                                loadRecent.remove(s);
                            }
                        });
                    }});
                }
                jPopupMenu.show(JavaOctetEditor.getInstance().fileTreePanel, e.getX(), e.getY());
            }
        });
    }
}