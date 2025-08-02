package cn.enaium.joe.gui.panel.menu.attach;

import cn.enaium.joe.dialog.TransformClassDialog;
import cn.enaium.joe.util.LangUtil;

import javax.swing.*;

public class TransformMenuItem extends JMenuItem {
    public TransformMenuItem() {
        super(LangUtil.i18n("menu.attach.transform"));
        addActionListener(e -> {
            new TransformClassDialog().setVisible(true);
        });
    }
}
