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

import cn.enaium.joe.config.util.CachedConfigValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LangUtil {
    public enum Lang{
        EN_US("en_US"),
        ZH_CN("zh_CN"),
        SYSTEM("System");

        private final String value;
        Lang(String value){this.value = value;}
        public String getValue(){return this.value;}
    }
    public static CachedConfigValue<Map<String, String>, Lang> locales = new CachedConfigValue<>((s, s2) -> {
        Map<String, String> locales = new HashMap<>();
        String lang;
        if (Lang.SYSTEM == s2){
            Locale locale = Locale.getDefault();
            lang = locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toUpperCase();
        } else lang = s2.getValue();

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

        return locales;
    });

    public static String i18n(String key, Object... args) {
        if (locales.getValue().containsKey(key)) {
            return String.format(locales.getValue().get(key), args);
        } else return key;
    }

    static {
        locales.update(null, Lang.SYSTEM, Lang.SYSTEM);
    }
}