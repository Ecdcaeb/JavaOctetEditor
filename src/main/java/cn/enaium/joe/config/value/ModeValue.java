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

import cn.enaium.joe.util.Util;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;

/**
 * @author Enaium
 * @since 0.7.0
 */
public final class ModeValue<T extends Enum<T>> extends Value<T>{

    @Expose(deserialize = false)
    private EnumSet<T> mode;
    private transient final Class<T> enumClass;

    public ModeValue(String name, T value, String description, EnumSet<T> mode) {
        super(value.getClass(), name, value, description);
        this.mode = mode;
        this.enumClass = Util.cast(value.getClass());
    }

    public EnumSet<T> getMode() {
        return mode;
    }

    public void setMode(EnumSet<T> mode) {
        this.mode = mode;
    }

    @Override
    public void decode(JsonElement jsonElement) {
        T enum_ = Enum.valueOf(enumClass, jsonElement.getAsString());
        if (this.mode.contains(enum_)) {
            this.setValue(enum_);
        }
    }

    public static Component createGui(ModeValue<?> modeValue){
        return new JComboBox<>(new DefaultComboBoxModel<>()) {{
            JComboBox<Object> jComboBox = this;
            for (Object s : modeValue.getMode()) {
                DefaultComboBoxModel<?> model = Util.cast(getModel());
                model.addElement(Util.cast(s));
                model.setSelectedItem(modeValue.getValue());
                jComboBox.addActionListener(e -> {
                    modeValue.setValue(Util.cast(model.getSelectedItem()));
                });
            }
        }};
    }

    public interface DisplayableEnum{ default String getDisplayName(){ return String.valueOf(this); } }
}
