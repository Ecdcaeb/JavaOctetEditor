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

package cn.enaium.joe.gui.panel.file.tree;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.gui.component.RightTabBar;
import cn.enaium.joe.gui.panel.BorderPanel;
import cn.enaium.joe.gui.panel.LeftPanel;
import cn.enaium.joe.gui.panel.file.FileDropTarget;
import cn.enaium.joe.util.mapping.MappingParser;
import cn.enaium.joe.util.task.tasks.InputJarTask;
import cn.enaium.joe.util.task.tasks.RemappingTask;
import net.fabricmc.mappingio.format.MappingFormat;

import javax.swing.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.File;

/**
 * @author Enaium
 * @since 1.2.0
 */
public class CenterPanel extends BorderPanel {
    public CenterPanel() {
        setCenter(new CenterPane());
        setRight(new RightPane());
    }

    public static class CenterPane extends JSplitPane{
        public CenterPane(){
            setLeftComponent(new LeftPanel());
            setRightComponent(JavaOctetEditor.getInstance().fileTabbedPanel);
            setDividerLocation(200);
            new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new FileDropTarget(
                    (file) -> {
                        if (file.isDirectory()) return true;
                        else {
                            String name = file.getName().toLowerCase();
                            return name.endsWith(".jar") || name.endsWith(".zip") || MappingParser.isAnyMatched(name) != null;
                        }
                    }
                    , files -> {

                if (!files.isEmpty()) {
                    File file = files.getFirst();
                    if (file.isDirectory()) {
                        JavaOctetEditor.getInstance().TASKS.submit(new InputJarTask(file));
                    } else {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".jar") || name.endsWith(".zip")){
                            JavaOctetEditor.getInstance().TASKS.submit(new InputJarTask(file));
                        } else {
                            MappingFormat mappingFormat = MappingParser.isAnyMatched(name);
                            if (mappingFormat != null) {
                                JavaOctetEditor.getInstance().TASKS.submit(new RemappingTask(file, MappingFormat.SRG));
                            }
                        }
                    }
                }
            }), true);
        }
    }

    public static class RightPane extends BorderPanel{
        public RightPane(){
            JViewport jViewport = new JViewport();
            setCenter(jViewport);
            setRight(new RightTabBar() {{
                addChangeListener(e -> {
                    jViewport.setView(getSelectedComponent());
                });
            }});
        }
    }
}
