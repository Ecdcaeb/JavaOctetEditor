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

package cn.enaium.joe.gui.panel;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.event.events.FileTabbedSelectEvent;
import cn.enaium.joe.gui.panel.file.tabbed.tab.classes.ClassTabPanel;
import cn.enaium.joe.gui.panel.file.tree.FileTreeCellRenderer;
import cn.enaium.joe.gui.panel.file.tree.node.ClassTreeNode;
import cn.enaium.joe.gui.panel.file.tree.node.PackageTreeNode;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.JTreeUtil;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.reflection.ReflectionHelper;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

/**
 * @author Enaium
 * @since 1.3.0
 */
public class InheritPanel extends BorderPanel {

    private ClassNode current;

    public InheritPanel() {
        JTree inheritance = new JTree() {{
            setModel(new DefaultTreeModel(null));
            setCellRenderer(new FileTreeCellRenderer());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        if (getSelectionPath() != null) {
                            Object lastPathComponent = getSelectionPath().getLastPathComponent();
                            if (lastPathComponent instanceof PackageTreeNode packageTreeNode) {
                                if (packageTreeNode instanceof ClassTreeNode) {
                                    ClassNode classNode = ((ClassTreeNode) packageTreeNode).classNode;
                                    JavaOctetEditor.getInstance().fileTabbedPanel.addTab(classNode.getSimpleName(), new ClassTabPanel(classNode));
                                }
                            }
                        }
                    }
                }
            });
        }};
        JavaOctetEditor.getInstance().EVENTS.register(FileTabbedSelectEvent.class, event -> {
            if (event.getSelect() instanceof ClassTabPanel) {
                current = ((ClassTabPanel) event.getSelect()).classNode;
                setModel(inheritance, true);
            } else {
                current = null;
                inheritance.setModel(new DefaultTreeModel(null));
            }
            repaint();
        });
        setCenter(new JScrollPane(inheritance));

        setBottom(new JToggleButton("Parent", true) {{
            addActionListener(e -> InheritPanel.this.setModel(inheritance, isSelected()));
        }});
    }

    private void setModel(JTree jTree, boolean p) {
        if (current != null) {
            jTree.setModel(new DefaultTreeModel(new ClassTreeNode(current) {{
                recursion(this, p);
            }}));
            JTreeUtil.setTreeExpandedState(jTree, true);
        }
    }

    private void recursion(ClassTreeNode classTreeNode, boolean parent) {
        ClassNode classNode = classTreeNode.classNode;
        Jar jar = JavaOctetEditor.getInstance().getJar();
        if (parent) {
            for (String s : classNode.getParents()) {
                ClassTreeNode newChild = null;
                if (jar.hasClass(s)) {
                    newChild = new ClassTreeNode(jar.getClassNode(s));
                } else if (ReflectionHelper.isClassExist(s.replace("/", "."))) {
                    ClassNode classNode1 = ClassNode.of(s);
                    if (classNode1 != null) {
                        newChild = new ClassTreeNode(classNode1);
                    }
                }
                if (newChild != null) {
                    classTreeNode.add(newChild);
                    recursion(newChild, true);
                }
            }
        } else {
            for (ClassNode value : jar.getClasses()) {
                Set<String> parentClass = value.getParents();
                if (parentClass.contains(classNode.getInternalName())) {
                    ClassTreeNode newChild = new ClassTreeNode(value);
                    classTreeNode.add(newChild);
                    recursion(newChild, false);
                }
            }
        }
    }
}
