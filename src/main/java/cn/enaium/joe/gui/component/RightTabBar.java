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

package cn.enaium.joe.gui.component;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.event.events.FileTabbedSelectEvent;
import cn.enaium.joe.gui.panel.file.tabbed.tab.classes.ClassTabPanel;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.Pair;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author Enaium
 * @since 1.3.0
 */
public class RightTabBar extends TabbedPane {
    public RightTabBar() {
        super(TabbedPane.RIGHT);
        addTab(LangUtil.i18n("sideTab.member"), new FlatSVGIcon("icons/structure.svg"), new MemberList() {{
            JavaOctetEditor.getInstance().event.register(FileTabbedSelectEvent.class, (Consumer<FileTabbedSelectEvent>) event -> {
                if (event.getSelect() instanceof ClassTabPanel) {
                    ClassTabPanel select = (ClassTabPanel) event.getSelect();
                    ClassNode classNode = select.classNode;
                    setModel(new DefaultListModel<Pair<ClassNode, Object>>() {{
                        for (FieldNode field : classNode.fields) {
                            addElement(new Pair<>(classNode, field));
                        }

                        for (MethodNode method : classNode.methods) {
                            addElement(new Pair<>(classNode, method));
                        }
                    }});
                }
            });
        }});
        addTab(LangUtil.i18n("sideTab.hierarchy"), new FlatSVGIcon("icons/hierarchy.svg"), new InheritanceTree());
        cancelSelect();
        setVerticalLabel();
    }
}