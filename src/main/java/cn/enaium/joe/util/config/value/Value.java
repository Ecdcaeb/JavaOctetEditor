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

import cn.enaium.joe.util.config.util.ConfigValueListener;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Enaium
 * @since 0.7.0
 */
public abstract class Value<T> {
    private transient final Type type;
    private final String name;
    private T value;
    private final String description;
    private transient final Set<ConfigValueListener<T>> listeners = new HashSet<>();

    public Value(Type type, String name, T value, String description) {
        this.type = type;
        this.name = name;
        this.setValue(value);
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;
        for(ConfigValueListener<T> listener : this.listeners){
            listener.update(this, oldValue, value);
        }
    }

    public abstract void decode(JsonElement jsonElement);

    public void addListener(ConfigValueListener<T> listener) {
        this.listeners.add(listener);
    }
}
