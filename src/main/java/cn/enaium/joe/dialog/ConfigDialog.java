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

package cn.enaium.joe.dialog;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.config.Config;
import cn.enaium.joe.config.value.*;
import cn.enaium.joe.util.LangUtil;
import org.tinylog.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class ConfigDialog extends Dialog {
    public ConfigDialog(Config config) {
        super(LangUtil.i18n("menu.config"));
        setSize(new Dimension(400, getHeight()));
        JPanel names = new JPanel(new GridLayout(0, 1));
        JPanel components = new JPanel(new GridLayout(0, 1));
        try {
            for (Field declaredField : config.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                Object o = declaredField.get(config);

                if (o instanceof Value) {
                    Value<?> value = (Value<?>) o;

                    names.add(new JLabel(value.getName()) {{
                        setToolTipText(value.getDescription());
                    }}, BorderLayout.WEST);
                }

                if (o instanceof StringValue) {
                    StringValue stringValue = (StringValue) o;
                    components.add(new JTextField(stringValue.getValue()) {{
                        JTextField jTextField = this;
                        getDocument().addDocumentListener(new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                stringValue.setValue(jTextField.getText());
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                stringValue.setValue(jTextField.getText());
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                stringValue.setValue(jTextField.getText());
                            }
                        });
                    }});
                } else if (o instanceof IntegerValue) {
                    IntegerValue integerValue = (IntegerValue) o;
                    components.add(new JSpinner() {{
                        addChangeListener(e -> integerValue.setValue(Integer.parseInt(getValue().toString())));
                    }});
                } else if (o instanceof EnableValue) {
                    EnableValue enableValue = (EnableValue) o;
                    components.add(new JCheckBox() {{
                        JCheckBox jCheckBox = this;
                        setHorizontalAlignment(JCheckBox.RIGHT);
                        setSelected(enableValue.getValue());
                        addActionListener(e -> {
                            enableValue.setValue(jCheckBox.isSelected());
                        });
                    }});
                } else if (o instanceof ModeValue) {
                    components.add(new JComboBox<String>(new DefaultComboBoxModel<>()) {{
                        JComboBox<String> jComboBox = this;
                        ModeValue modeValue = (ModeValue) o;
                        for (String s : modeValue.getMode()) {
                            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
                            model.addElement(s);
                            model.setSelectedItem(modeValue.getValue());
                            jComboBox.addActionListener(e -> {
                                modeValue.setValue(model.getSelectedItem().toString());
                            });
                        }
                    }});
                }
            }
        } catch (IllegalAccessException e) {
            Logger.error(e);
        }
        add(new JScrollPane(new JPanel(new BorderLayout()) {{
            add(names, BorderLayout.WEST);
            add(components, BorderLayout.CENTER);
        }}));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JavaOctetEditor.getInstance().configManager.save();
            }
        });
    }
}
