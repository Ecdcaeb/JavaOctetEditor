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

package cn.enaium.joe.config.value;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Enaium
 * @since 0.7.0
 */
public final class ModeValue extends Value<String> {

    @Expose(deserialize = false)
    private List<String> mode;

    public ModeValue(String name, String value, String description, List<String> mode) {
        super(String.class, name, value, description);
        this.mode = mode;
    }

    public List<String> getMode() {
        return mode;
    }

    public void setMode(List<String> mode) {
        this.mode = mode;
    }

    @Override
    public void decode(JsonElement jsonElement) {
        if (this.getMode().contains(jsonElement.getAsString())) {
            this.setValue(jsonElement.getAsString());
        } else {
            this.setValue(this.getMode().getFirst());
        }
    }

    public static Component createGui(ModeValue modeValue){
        return new JComboBox<String>(new DefaultComboBoxModel<>()) {{
            JComboBox<String> jComboBox = this;
            for (String s : modeValue.getMode()) {
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
                model.addElement(s);
                model.setSelectedItem(modeValue.getValue());
                jComboBox.addActionListener(e -> {
                    modeValue.setValue(model.getSelectedItem().toString());
                });
            }
        }};
    }
}
