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

package cn.enaium.joe.util;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.config.extend.ApplicationConfig;
import cn.enaium.joe.config.value.ModeValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LangUtil {
    public static String lang = null;
    public static Map<String, String> locales = new HashMap<>();

    public static String getCurrentLang(){
        String langConfig = JavaOctetEditor.getInstance().config.getByClass(ApplicationConfig.class).language.getValue();
        if (langConfig.equals("System")) {
            Locale locale = Locale.getDefault();
            return locale.getLanguage() + "_" + locale.getCountry();
        } else {
            return langConfig;
        }
    }

    public static void reloadLang(){
        reloadLang(getCurrentLang());
    }

    public static void reloadLang(String lang){
        locales.clear();
        try (InputStream stream = LangUtil.class.getResourceAsStream("/lang/en_US.json")){
            for(Map.Entry<String, JsonElement> entry : JsonParser.parseReader(new InputStreamReader(Objects.requireNonNull(stream))).getAsJsonObject().entrySet()) {
                locales.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (IOException | NullPointerException e) {
            Logger.warn(e);
        }
        try (InputStream stream = LangUtil.class.getResourceAsStream("/lang/" + lang + ".json")){
            for(Map.Entry<String, JsonElement> entry : JsonParser.parseReader(new InputStreamReader(Objects.requireNonNull(stream))).getAsJsonObject().entrySet()) {
                locales.putIfAbsent(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (IOException | NullPointerException e) {
            Logger.warn(e);
        }
    }

    public static String i18n(String key, Object... args) {
        if (locales.containsKey(key)) {
            return String.format(locales.get(key), args);
        } else return key;
    }

    static {
        reloadLang();
    }
}