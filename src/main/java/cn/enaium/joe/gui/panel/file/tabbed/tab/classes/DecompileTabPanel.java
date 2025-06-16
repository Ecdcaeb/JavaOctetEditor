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
import cn.enaium.joe.util.config.extend.ApplicationConfig;
import cn.enaium.joe.util.KeyStrokeUtil;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.compiler.Compiler;
import cn.enaium.joe.util.event.events.EditSaveSuccessEvent;
import cn.enaium.joe.gui.panel.CodeAreaPanel;
import cn.enaium.joe.util.task.tasks.DecompileTask;

import java.awt.*;
import java.io.StringWriter;

/**
 * @author Enaium
 */
public class DecompileTabPanel extends ClassNodeTabPanel {
    protected CodeAreaPanel codeAreaPanel;
    public DecompileTabPanel(ClassNode classNode) {
        super(classNode);
        setLayout(new BorderLayout());
        CodeAreaPanel codeAreaPanel = this.codeAreaPanel = new CodeAreaPanel() {{
            KeyStrokeUtil.register(getTextArea(), JavaOctetEditor.getInstance().CONFIG.getByClass(ApplicationConfig.class).keymap.getValue().save.getValue(), () -> {
                if (ClassTabPanel.classTabIndex == 1) {
                    try {
                        StringWriter tracer = new StringWriter();
                        byte[] clazz = Compiler.compileSingle(classNode.getCanonicalName(), getTextArea().getText(), tracer);
                        if (clazz == null) {
                            MessageUtil.error("Compile failed \n" + tracer);
                        }
                        classNode.accept(ClassNode.of(clazz));
                        MessageUtil.info(LangUtil.i18n("success"));
                        EditSaveSuccessEvent.trigger(classNode.getInternalName());
                    } catch (Throwable e) {
                        MessageUtil.error(e);
                    }
                }
            });
        }};
        codeAreaPanel.getTextArea().setSyntaxEditingStyle("text/java");
        update();
        codeAreaPanel.getTextArea().setCaretPosition(0);
        add(codeAreaPanel);
    }

    public void update(){
        JavaOctetEditor.getInstance().TASKS.submit(new DecompileTask(this.getClassNode())).thenAccept(it -> {
            codeAreaPanel.getTextArea().setText(it);
        });
    }
}
