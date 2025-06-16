package cn.enaium.joe.dialog.search;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.dialog.SearchDialog;
import cn.enaium.joe.gui.panel.search.ResultNode;
import cn.enaium.joe.util.task.tasks.SearchOpcodeTask;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.asm.OpcodeUtil;

import javax.swing.*;
import java.awt.*;

public class SearchInstructionDialog extends SearchDialog {
    public SearchInstructionDialog(){
        setTitle(LangUtil.i18n("search.opcode.title"));
        add(new JPanel(new FlowLayout()) {{
            JComboBox<String> opcodeBox = new JComboBox<>(OpcodeUtil.OPCODE.values().toArray(new String[0]));
            add(opcodeBox);
            add(new JButton(LangUtil.i18n("button.search")) {{
                addActionListener(e -> {
                    if (opcodeBox.getSelectedItem() != null) {
                        ((DefaultListModel<ResultNode>) resultList.getModel()).clear();
                        JavaOctetEditor.getInstance().TASKS
                                .submit(new SearchOpcodeTask(JavaOctetEditor.getInstance().getJar(), (String) opcodeBox.getSelectedItem()))
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
}
