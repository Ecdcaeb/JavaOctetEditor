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

package cn.enaium.joe.config;

import cn.enaium.joe.config.extend.*;
import cn.enaium.joe.config.value.*;
import cn.enaium.joe.util.MessageUtil;
import com.google.gson.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class ConfigManager {
    private final Map<Class<? extends Config>, Config> configMap = new LinkedHashMap<>();

    public ConfigManager() {
        addByInstance(new ApplicationConfig());
        addByInstance(new CFRConfig());
        addByInstance(new FernFlowerConfig());
        addByInstance(new ProcyonConfig());
        addByInstance(new KeymapConfig());
    }

    @SuppressWarnings("unchecked")
    public <T> T getByClass(Class<T> klass) {
        if (configMap.containsKey(klass)) {
            return (T) configMap.get(klass);
        } else {
            throw new RuntimeException("Not found " + klass);
        }
    }

    public <T extends Config> void addByClass(Class<T> config) {
        try {
            configMap.put(config, config.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw MessageUtil.runtimeException("Could not setup Config for " + config, e);
        }
    }

    public void addByInstance(Config config) {
        configMap.put(config.getClass(), config);
    }

    public Map<Class<? extends Config>, Config> getConfig() {
        return configMap;
    }

    public Map<String, String> getConfigMapStrings(Class<? extends Config> config) {
        return getConfigMap(config).entrySet().stream()
                .filter(entry -> entry.getValue().getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue().toString()));
    }

    public Map<String, Value<?>> getConfigMap(Class<? extends Config> config) {
        Map<String, Value<?>> map = new HashMap<>();
        for (Field declaredField : config.getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                Object o = declaredField.get(getByClass(config));
                if (o instanceof Value<?>) {
                    map.put(declaredField.getName(), (Value<?>)o);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private Gson gson() {
        return GSON;
    }

    public void load() {
        for (Map.Entry<Class<? extends Config>, Config> classConfigEntry : configMap.entrySet()) {

            Class<? extends Config> klass = classConfigEntry.getKey();
            Config config = classConfigEntry.getValue();
            try {
                File file = new File(System.getProperty("."), config.getName() + ".json");
                if (file.exists()) {
                    JsonObject jsonObject = gson().fromJson(Files.readString(file.toPath()), JsonObject.class);
                    for (Field configField : klass.getDeclaredFields()) {
                        configField.setAccessible(true);
                        if (!jsonObject.has(configField.getName())) {
                            continue;
                        }

                        if (!jsonObject.get(configField.getName()).isJsonObject()) {
                            continue;
                        }

                        if (!jsonObject.get(configField.getName()).getAsJsonObject().has("value")) {
                            continue;
                        }

                        JsonElement valueJsonElement = jsonObject.get(configField.getName()).getAsJsonObject().get("value");

                        Object valueObject = configField.get(config);
                        if (valueObject instanceof Value<?>) {
                            Value<?> value = (Value<?>) valueObject;
                            value.decode(valueJsonElement);
                        }
                    }
                }
            } catch (Throwable e) {
                MessageUtil.error(e);
            }
        }
    }

    public void save() {
        for (Config value : configMap.values()) {
            try {
                Files.writeString(new File(System.getProperty("."), value.getName() + ".json").toPath(), gson().toJson(value));
                value.update();
            } catch (IOException e) {
                MessageUtil.error(e);
            }
        }
    }
}
