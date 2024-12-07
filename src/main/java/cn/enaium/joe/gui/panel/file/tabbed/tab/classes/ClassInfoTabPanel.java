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


import cn.enaium.joe.dialog.AnnotationListDialog;
import cn.enaium.joe.event.events.EditSaveSuccessEvent;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.StringUtil;
import cn.enaium.joe.util.classes.ClassNode;
import net.miginfocom.swing.MigLayout;
import org.benf.cfr.reader.util.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Enaium
 * @since 0.6.0
 */
public class ClassInfoTabPanel extends ClassNodeTabPanel {
    protected JTextField name, sourceFile, debugFile, access, version, signature, superName, interfaces, outerClass, outerMethod, outerMethodDescription;

    public ClassInfoTabPanel(ClassNode classNode) {
        super(classNode);
        setLayout(new MigLayout("fillx", "[fill][fill]"));
        add(new JLabel(LangUtil.i18n("class.info.name")));
        JTextField name = this.name = new JTextField(classNode.getInternalName());
        add(name, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.sourceFile")));
        JTextField sourceFile = new JTextField(classNode.getClassNode().sourceFile);
        add(sourceFile, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.debugFile")));
        JTextField sourceDebug = new JTextField(classNode.getClassNode().sourceDebug);
        add(sourceDebug, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.access")));
        JTextField access = new JTextField(String.valueOf(classNode.getClassNode().access));
        add(access, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.version")));
        JTextField version = new JTextField(String.valueOf(classNode.getClassNode().version));
        add(version, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.signature")));
        JTextField signature = new JTextField(classNode.getClassNode().signature);
        add(signature, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.superName")));
        JTextField superName = new JTextField(classNode.getClassNode().superName);
        add(superName, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.interfaces")));
        JTextField interfaces = new JTextField(StringUtils.join(classNode.getClassNode().interfaces, ";"));
        add(interfaces, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.outerClass")));
        JTextField outerClass = new JTextField(classNode.getClassNode().outerClass);
        add(outerClass, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.outerMethod")));
        JTextField outerMethod = new JTextField(classNode.getClassNode().outerMethod);
        add(outerMethod, "wrap");
        add(new JLabel(LangUtil.i18n("class.info.outerMethodDescription")));
        JTextField outerMethodDesc = new JTextField(classNode.getClassNode().outerMethodDesc);
        add(outerMethodDesc, "wrap");
        add(new JLabel("Visible Annotation:"));
        add(new JButton(LangUtil.i18n("button.edit")) {{
            addActionListener(e -> {
                if (classNode.getClassNode().visibleAnnotations != null) {
                    new AnnotationListDialog(classNode.getClassNode().visibleAnnotations).setVisible(true);
                }
            });
        }}, "wrap");
        add(new JLabel("Invisible Annotation:"));
        add(new JButton(LangUtil.i18n("button.edit")) {{
            addActionListener(e -> {
                if (classNode.getClassNode().invisibleAnnotations != null) {
                    new AnnotationListDialog(classNode.getClassNode().invisibleAnnotations).setVisible(true);
                }
            });
        }}, "wrap");
        add(new JButton(LangUtil.i18n("button.save")) {{
            addActionListener(e -> {

                if (!StringUtil.isBlank(name.getText())) {
                    classNode.getClassNode().name = name.getText();
                }

                if (!StringUtil.isBlank(sourceFile.getText())) {
                    classNode.getClassNode().sourceFile = sourceFile.getText();
                } else {
                    classNode.getClassNode().sourceFile = null;
                }

                if (!StringUtil.isBlank(sourceDebug.getText())) {
                    classNode.getClassNode().sourceDebug = sourceDebug.getText();
                } else {
                    classNode.getClassNode().sourceDebug = null;
                }

                if (!StringUtil.isBlank(access.getText())) {
                    classNode.getClassNode().access = Integer.parseInt(access.getText());
                }

                if (!StringUtil.isBlank(version.getText())) {
                    classNode.getClassNode().version = Integer.parseInt(version.getText());
                }

                if (!StringUtil.isBlank(signature.getText())) {
                    classNode.getClassNode().signature = signature.getText();
                } else {
                    classNode.getClassNode().signature = null;
                }

                if (!StringUtil.isBlank(interfaces.getText())) {
                    classNode.getClassNode().interfaces = Arrays.asList(superName.getText().split(";"));
                } else {
                    classNode.getClassNode().interfaces = new ArrayList<>();
                }

                if (!StringUtil.isBlank(outerClass.getText())) {
                    classNode.getClassNode().outerClass = outerClass.getText();
                } else {
                    classNode.getClassNode().outerClass = null;
                }

                if (!StringUtil.isBlank(outerMethod.getText())) {
                    classNode.getClassNode().outerMethod = outerMethod.getText();
                } else {
                    classNode.getClassNode().outerClass = null;
                }

                if (!StringUtil.isBlank(outerMethodDesc.getText())) {
                    classNode.getClassNode().outerMethodDesc = outerMethodDesc.getText();
                } else {
                    classNode.getClassNode().outerClass = null;
                }

                JOptionPane.showMessageDialog(ClassInfoTabPanel.this, LangUtil.i18n("success"));
                EditSaveSuccessEvent.trigger(classNode.getInternalName());
            });
        }}, "span 2");
    }

    public void update(){
        this.name.setText(this.getClassNode().getInternalName());
        this.superName.setText(this.getClassNode().getClassNode().superName);
        this.debugFile.setText(this.getClassNode().getClassNode().sourceDebug);
        this.sourceFile.setText(this.getClassNode().getClassNode().sourceFile);
        this.signature.setText(this.getClassNode().getClassNode().signature);
        this.outerClass.setText(this.getClassNode().getClassNode().outerClass);
        this.outerMethod.setText(this.getClassNode().getClassNode().outerMethod);
        this.outerMethodDescription.setText(this.getClassNode().getClassNode().outerMethodDesc);
        this.access.setText(String.valueOf(this.getClassNode().getClassNode().access));
        this.interfaces.setText(StringUtils.join(this.getClassNode().getClassNode().interfaces, ";"));
        this.version.setText(String.valueOf(this.getClassNode().getClassNode().version));
    }
}
