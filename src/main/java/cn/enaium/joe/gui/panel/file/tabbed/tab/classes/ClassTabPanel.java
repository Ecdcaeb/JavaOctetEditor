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

package cn.enaium.joe.gui.panel.file.tabbed.tab.classes;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.event.events.EditSaveSuccessEvent;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.classes.ClassNode;

import javax.swing.*;
import java.awt.*;
import java.util.WeakHashMap;

/**
 * @author Enaium
 */
public class ClassTabPanel extends JPanel {
    public static int classTabIndex = 0;

    protected TraceBytecodeTabPanel traceBytecodeTabPanel;
    protected DecompileTabPanel decompileTabPanel;
    protected ASMifierTablePanel asmTablePanel;
    protected ClassInfoTabPanel classInfoTabPanel;

    private static final WeakHashMap<String, ClassTabPanel> internalName2panel = new WeakHashMap<>();

    public final ClassNode classNode;
    protected final JTabbedPane jTabbedPane;

    public ClassTabPanel(ClassNode classNode) {
        super(new BorderLayout());
        this.classNode = classNode;
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        jTabbedPane.addTab(LangUtil.i18n("class.tab.bytecodeView"), traceBytecodeTabPanel = new TraceBytecodeTabPanel(classNode));
        jTabbedPane.addTab(LangUtil.i18n("class.tab.decompileView"), decompileTabPanel = new DecompileTabPanel(classNode));
        jTabbedPane.addTab(LangUtil.i18n("class.tab.visitorEdit"), asmTablePanel = new ASMifierTablePanel(classNode));
        jTabbedPane.addTab(LangUtil.i18n("class.tab.infoEdit"), classInfoTabPanel = new ClassInfoTabPanel(classNode));
        jTabbedPane.setSelectedIndex(classTabIndex);
        internalName2panel.put(classNode.getInternalName(), this);
        jTabbedPane.addChangeListener(e -> {
            classTabIndex = jTabbedPane.getSelectedIndex();
            for (ClassTabPanel panel : internalName2panel.values()){
                if (panel != null){
                    panel.jTabbedPane.setSelectedIndex(classTabIndex);
                }
            }
        });
        add(this.jTabbedPane = jTabbedPane);
    }

    public void update(boolean forced){
        if (forced || classTabIndex != 0) traceBytecodeTabPanel.update();
        if (forced || classTabIndex != 1) decompileTabPanel.update();
        if (forced || classTabIndex != 2) asmTablePanel.update();
        if (forced || classTabIndex != 3) classInfoTabPanel.update();
    }

    static {
        JavaOctetEditor.getInstance().EVENTS.register(EditSaveSuccessEvent.class, event -> {
            if (internalName2panel.containsKey(event.classInternalName())){
                ClassTabPanel panel = internalName2panel.get(event.classInternalName());
                if (panel != null){
                    panel.update(false);
                }
            }
        });
    }
}