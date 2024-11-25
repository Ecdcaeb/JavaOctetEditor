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
import cn.enaium.joe.Instance;
import cn.enaium.joe.event.events.EditSaveSuccessEvent;
import cn.enaium.joe.util.LangUtil;
import org.objectweb.asm.tree.ClassNode;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.WeakHashMap;

/**
 * @author Enaium
 */
public class ClassTabPanel extends JPanel {

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
        jTabbedPane.addTab(LangUtil.i18n("class.tab.decompileEdit"), decompileTabPanel = new DecompileTabPanel(classNode));
        jTabbedPane.addTab(LangUtil.i18n("class.tab.visitorEdit"), asmTablePanel = new ASMifierTablePanel(classNode));
        jTabbedPane.addTab(LangUtil.i18n("class.tab.infoEdit"), classInfoTabPanel = new ClassInfoTabPanel(classNode));
        jTabbedPane.setSelectedIndex(Instance.INSTANCE.classTabIndex);
        internalName2panel.put(classNode.name, this);
        jTabbedPane.addChangeListener(e -> {
            Instance.INSTANCE.classTabIndex = jTabbedPane.getSelectedIndex();
            for (ClassTabPanel panel : internalName2panel.values()){
                if (panel != null){
                    panel.jTabbedPane.setSelectedIndex(Instance.INSTANCE.classTabIndex);
                }
            }
        });
        add(this.jTabbedPane = jTabbedPane);
    }

    public void update(){
        traceBytecodeTabPanel.update();
        decompileTabPanel.update();
        asmTablePanel.update();
    }

    static {
        JavaOctetEditor.getInstance().event.register(EditSaveSuccessEvent.class, event -> {
            if (internalName2panel.containsKey(event.classInternalName())){
                ClassTabPanel panel = internalName2panel.get(event.classInternalName());
                if (panel != null){
                    panel.update();
                }
            }
        });
    }
}