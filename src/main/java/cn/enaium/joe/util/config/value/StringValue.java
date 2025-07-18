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

package cn.enaium.joe.util.config.value;

import com.google.gson.JsonElement;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author Enaium
 * @since 0.7.0
 */
public final class StringValue extends Value<String> {
    public StringValue(String name, String value, String description) {
        super(String.class, name, value, description);
    }

    @Override
    public void decode(JsonElement jsonElement) {
        this.setValue(jsonElement.getAsString());
    }

    public static Component createGui(StringValue stringValue){
        return new JTextField(25) {{
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
        }};
    }
}
