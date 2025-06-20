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
import cn.enaium.joe.util.config.extend.ApplicationConfig;
import cn.enaium.joe.gui.panel.file.tabbed.tab.classes.ClassTabPanel;
import cn.enaium.joe.gui.panel.file.tabbed.tab.resources.FileTablePane;
import cn.enaium.joe.gui.panel.file.tree.FileTreeCellRenderer;
import cn.enaium.joe.gui.panel.file.tree.node.*;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.*;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.file.FIleTransferable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Enaium
 */
public class FileTree extends JTree {

    public enum PackagePresentation{
        HIERARCHICAL("Hierarchical"),
        FLAT("Flat");

        private final String value;
        PackagePresentation(String value){this.value = value;}
        public String getValue(){return this.value;}
    }

    public static final DefaultTreeNode classesRoot = new DefaultTreeNode("classes");
    public static final DefaultTreeNode resourceRoot = new DefaultTreeNode("resources");

    public FileTree() {
        super(new DefaultTreeNode("") {{
            add(classesRoot);
            add(resourceRoot);
        }});

        setRootVisible(false);
        setShowsRootHandles(true);
        setCellRenderer(new FileTreeCellRenderer());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addTab();
                }
            }
        });
        KeyStrokeUtil.register(this, JavaOctetEditor.getInstance().CONFIG.getByClass(ApplicationConfig.class).keymap.getValue().copy.getValue(), this::copyFile);

        JPopupMenu jPopupMenu = new JPopupMenu();


        jPopupMenu.add(new JMenuItem(LangUtil.i18n("popup.fileTree.expandAll")) {{
            addActionListener(e -> {
                JTreeUtil.setNodeExpandedState(FileTree.this, ((DefaultMutableTreeNode) Objects.requireNonNull(getSelectionPath()).getLastPathComponent()), true);
            });
        }});
        JMenuUtil.addPopupMenu(this, () -> jPopupMenu, () -> getSelectionPath() != null);
    }

    public void addTab() {
        if (getSelectionPath() == null) {
            return;
        }
        Object lastPathComponent = getSelectionPath().getLastPathComponent();
        SwingUtilities.invokeLater(() -> {
            if (lastPathComponent instanceof PackageTreeNode packageTreeNode) {
                if (packageTreeNode instanceof ClassTreeNode) {
                    ClassNode classNode = ((ClassTreeNode) packageTreeNode).classNode;
                    JavaOctetEditor.getInstance().fileTabbedPanel.addTab(classNode.getSimpleName(), new ClassTabPanel(classNode));
                }
            } else if (lastPathComponent instanceof FolderTreeNode folderTreeNode) {
                if (folderTreeNode instanceof FileTreeNode fileTreeNode) {
                    JavaOctetEditor.getInstance().fileTabbedPanel.addTab(fileTreeNode.toString().substring(fileTreeNode.toString().lastIndexOf("/") + 1), new FileTablePane(fileTreeNode));
                }
            }
            JavaOctetEditor.getInstance().fileTabbedPanel.setSelectedIndex(JavaOctetEditor.getInstance().fileTabbedPanel.getTabCount() - 1);
        });
    }

    public void copyFile() {
        if (getSelectionPath() == null) {
            return;
        }
        Object lastPathComponent = getSelectionPath().getLastPathComponent();
        SwingUtilities.invokeLater(() -> {
            if (lastPathComponent instanceof PackageTreeNode packageTreeNode) {
                if (packageTreeNode instanceof ClassTreeNode) {
                    ClassNode classNode = ((ClassTreeNode) packageTreeNode).classNode;
                    Path tempFolder = null;
                    try {
                        tempFolder = Files.createTempDirectory("cn.enaium.joe");
                        final File file = new File(tempFolder.toFile(), classNode.getSimpleName() + ".class");
                        Files.write(file.toPath(), classNode.getClassBytes());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new FIleTransferable(file), null);
                    } catch (Throwable e) {
                        MessageUtil.error("Could Not Copy", e);
                    } finally {
                        if (tempFolder != null) tempFolder.toFile().deleteOnExit();
                    }
                } else {

                }
            } else if (lastPathComponent instanceof FolderTreeNode folderTreeNode) {
                if (folderTreeNode instanceof FileTreeNode fileTreeNode) {
                    Path tempFolder = null;
                    try {
                        tempFolder = Files.createTempDirectory("cn.enaium.joe");
                        final File file = new File(tempFolder.toFile(), fileTreeNode.getName());
                        Files.write(file.toPath(), fileTreeNode.getData());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new FIleTransferable(file), null);
                    } catch (Throwable e) {
                        MessageUtil.error("Could Not Copy", e);
                    } finally {
                        if (tempFolder != null) tempFolder.toFile().deleteOnExit();
                    }
                } else {

                }
            }
        });
    }

    public void refresh(Jar jar) {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        model.reload();
        classesRoot.removeAllChildren();
        resourceRoot.removeAllChildren();

        ApplicationConfig config = JavaOctetEditor.getInstance().CONFIG.getByClass(ApplicationConfig.class);


        PackagePresentation packagePresentationValue = config.packagePresentation.getValue();

        if (packagePresentationValue == PackagePresentation.HIERARCHICAL) {
            Map<String, DefaultTreeNode> hasMap = new HashMap<>();

            for (ClassNode classNode : jar.getClasses()) {
                String[] split = classNode.getInternalName().split("/");
                DefaultTreeNode prev = null;
                StringBuilder stringBuilder = new StringBuilder();
                int i = 0;
                for (String s : split) {
                    stringBuilder.append(s);
                    PackageTreeNode packageTreeNode = new PackageTreeNode(s);

                    if (split.length == i + 1) {
                        packageTreeNode = new ClassTreeNode(classNode);
                    }

                    if (prev == null) {
                        if (!hasMap.containsKey(stringBuilder.toString())) {
                            classesRoot.add(packageTreeNode);
                            hasMap.put(stringBuilder.toString(), packageTreeNode);
                            prev = packageTreeNode;
                        } else {
                            prev = hasMap.get(stringBuilder.toString());
                        }
                    } else {
                        if (!hasMap.containsKey(stringBuilder.toString())) {
                            prev.add(packageTreeNode);
                            hasMap.put(stringBuilder.toString(), packageTreeNode);
                            prev = packageTreeNode;
                        } else {
                            prev = hasMap.get(stringBuilder.toString());
                        }
                    }
                    i++;
                }
            }
            compact(model, classesRoot);
            sort(model, classesRoot);

            hasMap.clear();

            for (Map.Entry<String, byte[]> stringEntry : jar.getResources().entrySet()) {
                String[] split = stringEntry.getKey().split("/");
                DefaultTreeNode prev = null;
                StringBuilder stringBuilder = new StringBuilder();
                int i = 0;
                for (String s : split) {
                    stringBuilder.append(s);
                    FolderTreeNode folderTreeNode = new FolderTreeNode(s);

                    if (split.length == i + 1) {
                        folderTreeNode = new FileTreeNode(s,stringEntry.getKey());
                    }

                    if (prev == null) {
                        if (!hasMap.containsKey(stringBuilder.toString())) {
                            resourceRoot.add(folderTreeNode);
                            hasMap.put(stringBuilder.toString(), folderTreeNode);
                            prev = folderTreeNode;
                        } else {
                            prev = hasMap.get(stringBuilder.toString());
                        }
                    } else {
                        if (!hasMap.containsKey(stringBuilder.toString())) {
                            prev.add(folderTreeNode);
                            hasMap.put(stringBuilder.toString(), folderTreeNode);
                            prev = folderTreeNode;
                        } else {
                            prev = hasMap.get(stringBuilder.toString());
                        }
                    }
                    i++;
                }
            }
            compact(model, classesRoot);
            sort(model, resourceRoot);
        } else if (packagePresentationValue == PackagePresentation.FLAT) {
            Map<String, DefaultTreeNode> hasMap = new HashMap<>();
            for (ClassNode value : jar.getClasses()) {
                String packageName = "";
                if (value.getInternalName().contains("/")) {
                    packageName = value.getCanonicalPackageName();
                }

                ClassTreeNode classTreeNode = new ClassTreeNode(value);

                if (packageName.isEmpty()) {
                    classesRoot.add(classTreeNode);
                } else {
                    DefaultTreeNode defaultTreeNode;
                    if (hasMap.containsKey(packageName)) {
                        defaultTreeNode = hasMap.get(packageName);
                    } else {
                        defaultTreeNode = new PackageTreeNode(packageName);
                        hasMap.put(packageName, defaultTreeNode);
                    }
                    defaultTreeNode.add(classTreeNode);
                    classesRoot.add(defaultTreeNode);
                }
            }
            hasMap.clear();

            for (Map.Entry<String, byte[]> stringEntry : jar.getResources().entrySet()) {
                String folderName = "";
                String name = stringEntry.getKey();
                if (stringEntry.getKey().contains("/")) {
                    folderName = stringEntry.getKey().substring(0, stringEntry.getKey().lastIndexOf("/")).replace("/", ".");
                    name = name.substring(name.lastIndexOf("/") + 1);
                }

                FileTreeNode classTreeNode = new FileTreeNode(name, stringEntry.getKey());

                if (folderName.isEmpty()) {
                    resourceRoot.add(classTreeNode);
                } else {
                    DefaultTreeNode defaultTreeNode;
                    if (hasMap.containsKey(folderName)) {
                        defaultTreeNode = hasMap.get(folderName);
                    } else {
                        defaultTreeNode = new FolderTreeNode(folderName);
                        hasMap.put(folderName, defaultTreeNode);
                    }
                    defaultTreeNode.add(classTreeNode);
                    resourceRoot.add(defaultTreeNode);
                }
            }
            sort(model, resourceRoot);
        }

        JavaOctetEditor.getInstance().fileTabbedPanel.removeAll();

        repaint();
    }

    public void compact(DefaultTreeModel defaultTreeModel, DefaultTreeNode defaultTreeNode) {

        if (!JavaOctetEditor.getInstance().CONFIG.getByClass(ApplicationConfig.class).compactMiddlePackage.getValue()) {
            return;
        }

        if (!defaultTreeNode.isLeaf()) {
            DefaultTreeNode parent = (DefaultTreeNode) defaultTreeNode.getParent();
            if (parent.getChildren().size() == 1 && !(parent.equals(classesRoot) || parent.equals(resourceRoot))) {
                parent.setUserObject(parent.getUserObject() + "." + defaultTreeNode.getUserObject());
                parent.getChildren().clear();
                for (DefaultTreeNode child : defaultTreeNode.getChildren()) {
                    child.setParent(parent);
                    parent.getChildren().add(child);
                }
            }


            for (int i = 0; i < defaultTreeModel.getChildCount(defaultTreeNode); i++) {
                DefaultTreeNode child = ((DefaultTreeNode) defaultTreeModel.getChild(defaultTreeNode, i));
                compact(defaultTreeModel, child);
            }
        }
    }


    public void sort(DefaultTreeModel defaultTreeModel, DefaultTreeNode defaultTreeNode) {
        if (!defaultTreeNode.isLeaf()) {
            defaultTreeNode.getChildren().sort((o1, o2) -> {
                boolean class1 = o1 instanceof ClassTreeNode;
                boolean class2 = o2 instanceof ClassTreeNode;

                boolean file1 = o1 instanceof FileTreeNode;
                boolean file2 = o2 instanceof FileTreeNode;

                if (class1 && !class2) {
                    return 1;
                }
                if (!class1 && class2) {
                    return -1;
                }

                if (file1 && !file2) {
                    return 1;
                }
                if (!file1 && file2) {
                    return -1;
                }
                return o1.toString().compareTo(o2.toString());
            });
            for (int i = 0; i < defaultTreeModel.getChildCount(defaultTreeNode); i++) {
                DefaultTreeNode child = ((DefaultTreeNode) defaultTreeModel.getChild(defaultTreeNode, i));
                sort(defaultTreeModel, child);
            }
        }
    }
}
