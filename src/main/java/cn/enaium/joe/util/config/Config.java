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

package cn.enaium.joe.util.config;

import cn.enaium.joe.util.config.value.Value;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class Config {
    private final String name;

    private transient final Set<Consumer<Config>> listeners;
    private transient Map<String, Value<?>> values = null;

    public Config(String name) {
        this(name, Collections.emptySet());
    }

    public Config(String name, Set<Consumer<Config>> listeners ) {
        this.name = name;
        this.listeners = listeners;
    }

    // Must call this in the end of constructor
    protected void postInit(){
        Class<?> thisClass = this.getClass();
        Map<String, Value<?>> map = new HashMap<>();
        for (Field declaredField : thisClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if (declaredField.getType().isAssignableFrom(Value.class)) {
                try {
                    Object o = declaredField.get(this);
                    if (o instanceof Value<?>) {
                        map.put(declaredField.getName(), (Value<?>) o);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.values = map;
    }

    public Map<String, Value<?>> getConfigMap(){
        return values;
    }

    public void update(){
        for(Consumer<Config> consumer : listeners){
            consumer.accept(this);
        }
    }

    public String getName() {
        return name;
    }

}
