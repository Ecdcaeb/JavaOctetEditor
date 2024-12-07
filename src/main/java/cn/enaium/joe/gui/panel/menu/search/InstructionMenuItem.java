package cn.enaium.joe.gui.panel.menu.search;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.dialog.search.SearchInstructionDialog;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.LangUtil;

import javax.swing.*;

public class InstructionMenuItem extends JMenuItem {
    public InstructionMenuItem() {
        super(LangUtil.i18n("menu.search.opcode"));
        addActionListener(e -> {
            Jar jar = JavaOctetEditor.getInstance().getJar();
            if (jar == null) {
                return;
            }
            new SearchInstructionDialog().setVisible(true);
        });
    }
}
