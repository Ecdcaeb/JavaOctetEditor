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
import cn.enaium.joe.task.SearchLdcTask;
import cn.enaium.joe.util.LangUtil;
import org.objectweb.asm.tree.LdcInsnNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Enaium
 */
public class SearchLdcDialog extends SearchDialog {
    protected JTextField jTextField;
    protected JButton jButton;
    public SearchLdcDialog() {
        setTitle(LangUtil.i18n("search.ldc.title"));
        add(new JPanel(new FlowLayout()) {{
            JTextField text = jTextField =  new JTextField(15);
            add(text);
            add(jButton = new JButton(LangUtil.i18n("button.search")) {{
                addActionListener(e -> {
                    if (!text.getText().isEmpty()) {
                        ((DefaultListModel<ResultNode>) resultList.getModel()).clear();
                        JavaOctetEditor.getInstance().task
                                .submit(new SearchLdcTask(JavaOctetEditor.getInstance().getJar(), text.getText()))
                                .thenAccept(it -> {
                                    ((DefaultListModel<ResultNode>) resultList.getModel()).clear();
                                    for (ResultNode resultNode : it) {
                                        ((DefaultListModel<ResultNode>) resultList.getModel()).addElement(resultNode);
                                    }
                                });
                    }
                });
            }});
        }}, BorderLayout.SOUTH);
    }

    public SearchLdcDialog(LdcInsnNode ldcInsnNode) {
        this();
        this.jTextField.setText(String.valueOf(ldcInsnNode.cst));
        for(ActionListener listener : this.jButton.getListeners(ActionListener.class)){
            listener.actionPerformed(null);
        }
    }
}
