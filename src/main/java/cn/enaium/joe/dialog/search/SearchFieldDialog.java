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

package cn.enaium.joe.dialog.search;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.dialog.SearchDialog;
import cn.enaium.joe.gui.panel.search.ResultNode;
import cn.enaium.joe.util.task.tasks.SearchFieldTask;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.StringUtil;
import cn.enaium.joe.util.classes.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Enaium
 * @since 0.5.0
 */
public class SearchFieldDialog extends SearchDialog {
    protected JTextField owner;
    protected JTextField name;
    protected JTextField desc;
    protected JButton jButton;

    public SearchFieldDialog() {
        setTitle(LangUtil.i18n("search.field.title"));
        add(new JPanel(new FlowLayout()) {{
            add(new JLabel(LangUtil.i18n("search.owner")));
            JTextField owner = SearchFieldDialog.this.owner =  new JTextField();
            add(owner);
            add(new JLabel(LangUtil.i18n("search.name")));
            JTextField name = SearchFieldDialog.this.name = new JTextField();
            add(name);
            add(new JLabel(LangUtil.i18n("search.description")));
            JTextField description = SearchFieldDialog.this.desc =  new JTextField();
            add(description);
            add(SearchFieldDialog.this.jButton = new JButton(LangUtil.i18n("button.search")) {{
                addActionListener(e -> {

                    if (StringUtil.isBlank(owner.getText()) && StringUtil.isBlank(name.getText()) && StringUtil.isBlank(description.getText())) {
                        MessageUtil.warning("Please input");
                        return;
                    }

                    ((DefaultListModel<ResultNode>) resultList.getModel()).clear();

                    JavaOctetEditor.getInstance().TASKS
                            .submit(new SearchFieldTask(JavaOctetEditor.getInstance().getJar(), owner.getText(), name.getText(), description.getText()))
                            .thenAccept(it -> {
                                for (ResultNode resultNode : it) {
                                    ((DefaultListModel<ResultNode>) resultList.getModel()).addElement(resultNode);
                                }
                            });
                });
            }});
        }}, BorderLayout.SOUTH);
    }

    public SearchFieldDialog(FieldInsnNode fieldInsnNode){
        this();
        this.name.setText(fieldInsnNode.name);
        this.owner.setText(fieldInsnNode.owner);
        this.desc.setText(fieldInsnNode.desc);
        for(ActionListener listener : this.jButton.getListeners(ActionListener.class)){
            listener.actionPerformed(null);
        }
    }

    public SearchFieldDialog(ClassNode classNode, FieldNode fieldInsnNode){
        this();
        this.name.setText(fieldInsnNode.name);
        this.owner.setText(classNode.getInternalName());
        this.desc.setText(fieldInsnNode.desc);
        for(ActionListener listener : this.jButton.getListeners(ActionListener.class)){
            listener.actionPerformed(null);
        }
    }

}
