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

package cn.enaium.joe.gui.panel;

import javax.swing.*;

/**
 * @author Enaium
 * @since 1.3.0
 */
public class HierarchyPanel extends BorderPanel {
    public HierarchyPanel() {
        setCenter(new JTabbedPane() {{
            putClientProperty("JTabbedPane.tabAreaAlignment", "fill");

            addTab("Inherit", new InheritancePanel());
            addTab("Call", new JPanel());
        }});
    }
}
