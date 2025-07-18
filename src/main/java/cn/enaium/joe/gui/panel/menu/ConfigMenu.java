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

package cn.enaium.joe.gui.panel.menu;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.config.Config;
import cn.enaium.joe.dialog.ConfigDialog;
import cn.enaium.joe.util.LangUtil;

import javax.swing.*;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class ConfigMenu extends JMenu {
    public ConfigMenu() {
        super(LangUtil.i18n("menu.config"));
        for (Config value : JavaOctetEditor.getInstance().CONFIG.getConfig().values()) {
            add(new JMenuItem(value.getName()) {{
                addActionListener(e -> {
                    new ConfigDialog(value).setVisible(true);
                });
            }});
        }
    }
}
