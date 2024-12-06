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
import cn.enaium.joe.annotation.NoUI;
import cn.enaium.joe.config.Config;
import cn.enaium.joe.config.value.*;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.MessageUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class ConfigDialog extends Dialog {
    public ConfigDialog(final Config config) {
        super(LangUtil.i18n("menu.config"));

        setContentPane(new JScrollPane(new JPanel(new MigLayout("fillx", "[fill][fill]")) {{
            try {
                for (Field declaredField : config.getClass().getDeclaredFields()) {
                    declaredField.setAccessible(true);

                    if (declaredField.isAnnotationPresent(NoUI.class)) {
                        continue;
                    }

                    Object o = declaredField.get(config);

                    if (o instanceof Value<?> value) {
                        add(new JLabel(value.getName()) {{
                            setToolTipText(value.getDescription());
                        }});
                        switch (value) {
                            case StringValue stringValue -> add(new JTextField(25) {{
                                JTextField jTextField = this;
                                jTextField.setText(stringValue.getValue());
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
                            }}, "wrap");
                            case IntegerValue integerValue -> add(new JSpinner() {{
                                setValue(integerValue.getValue());
                                addChangeListener(e -> integerValue.setValue(Integer.parseInt(getValue().toString())));
                            }}, "wrap");
                            case EnableValue enableValue -> add(new JCheckBox() {{
                                JCheckBox jCheckBox = this;
                                setHorizontalAlignment(JCheckBox.RIGHT);
                                setSelected(enableValue.getValue());
                                addActionListener(e -> {
                                    enableValue.setValue(jCheckBox.isSelected());
                                });
                            }}, "wrap");
                            case ModeValue modeValue -> add(new JComboBox<String>(new DefaultComboBoxModel<>()) {{
                                JComboBox<String> jComboBox = this;
                                for (String s : modeValue.getMode()) {
                                    DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
                                    model.addElement(s);
                                    model.setSelectedItem(modeValue.getValue());
                                    jComboBox.addActionListener(e -> {
                                        modeValue.setValue(model.getSelectedItem().toString());
                                    });
                                }
                            }}, "wrap");
                            case KeyValue keyValue -> add(new JButton(keyValue.getValue().toString()) {{
                                addKeyListener(new KeyAdapter() {
                                    @Override
                                    public void keyPressed(KeyEvent e) {
                                        if (e.getKeyChar() != 65535) {
                                            KeyStroke newKey = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiersEx());
                                            keyValue.setValue(newKey);
                                            setText(newKey.toString());
                                        }
                                    }
                                });
                            }}, "wrap");
                            case ConfigValue<?> configValue -> add(new JButton(configValue.getName()) {
                                {
                                    addMouseListener(new MouseAdapter() {
                                        @Override
                                        public void mouseClicked(MouseEvent e) {
                                            new ConfigDialog(configValue.getValue()).setVisible(true);
                                        }
                                    });
                                }
                            }, "wrap");
                            default -> {
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                MessageUtil.error(e);
            }
        }}));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JavaOctetEditor.getInstance().config.save();
            }
        });
    }
}
