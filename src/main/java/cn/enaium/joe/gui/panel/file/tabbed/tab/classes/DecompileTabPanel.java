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
import cn.enaium.joe.util.compiler.Compiler;
import cn.enaium.joe.config.extend.KeymapConfig;
import cn.enaium.joe.event.events.EditSaveSuccessEvent;
import cn.enaium.joe.gui.panel.CodeAreaPanel;
import cn.enaium.joe.task.DecompileTask;
import cn.enaium.joe.util.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.awt.*;

/**
 * @author Enaium
 */
public class DecompileTabPanel extends ClassNodeTabPanel {
    protected CodeAreaPanel codeAreaPanel;
    public DecompileTabPanel(ClassNode classNode) {
        super(classNode);
        setLayout(new BorderLayout());
        CodeAreaPanel codeAreaPanel = this.codeAreaPanel = new CodeAreaPanel() {{
            KeyStrokeUtil.register(getTextArea(), JavaOctetEditor.getInstance().config.getByClass(KeymapConfig.class).save.getValue(), () -> {
                try {
                    Compiler compiler = new Compiler();
                    compiler.addSource(ASMUtil.getReferenceName(classNode), getTextArea().getText());
                    compiler.compile();
                    if (compiler.getClasses().get(ASMUtil.getReferenceName(classNode)).length != 0) {
                        ReflectUtil.copyAllMember(classNode, ASMUtil.acceptClassNode(new ClassReader(compiler.getClasses().get(ASMUtil.getReferenceName(classNode)))));
                        MessageUtil.info(LangUtil.i18n("success"));
                        EditSaveSuccessEvent.trigger(classNode.name);
                    } else {
                        MessageUtil.error(new RuntimeException("Compile failed,Please check console"));
                    }
                } catch (Throwable e) {
                    MessageUtil.error(e);
                }
            });
        }};
        codeAreaPanel.getTextArea().setSyntaxEditingStyle("text/java");
        update();
        codeAreaPanel.getTextArea().setCaretPosition(0);
        add(codeAreaPanel);
    }

    public void update(){
        JavaOctetEditor.getInstance().task.submit(new DecompileTask(this.getClassNode())).thenAccept(it -> {
            codeAreaPanel.getTextArea().setText(it);
        });
    }
}
