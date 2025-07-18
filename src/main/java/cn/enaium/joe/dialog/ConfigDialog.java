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
import cn.enaium.joe.util.config.NoUI;
import cn.enaium.joe.util.config.Config;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.Util;
import cn.enaium.joe.util.config.value.Value;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.Field;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class ConfigDialog extends Dialog {
    public ConfigDialog(Config config) {
        super(LangUtil.i18n("menu.config"));

        setContentPane(new JScrollPane(new JPanel(new MigLayout("fillx", "[fill][fill]")) {{
            try {
                for (Field declaredField : config.getClass().getDeclaredFields()) {
                    declaredField.setAccessible(true);

                    if (declaredField.isAnnotationPresent(NoUI.class)) {
                        continue;
                    }

                    Object o = declaredField.get(config);

                    if (o instanceof Value) {
                        Value<?> value = Util.cast(o);

                        add(new JLabel(value.getName()) {{
                            setToolTipText(value.getDescription());
                        }});

                        add(JavaOctetEditor.getInstance().CONFIG.createGuiComponent(value), "wrap");
                    }
                }
            } catch (IllegalAccessException e) {
                MessageUtil.error(e);
            }
        }}){{
            this.getVerticalScrollBar().setUnitIncrement(16);
        }});
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JavaOctetEditor.getInstance().CONFIG.save();
            }
        });
    }
}
