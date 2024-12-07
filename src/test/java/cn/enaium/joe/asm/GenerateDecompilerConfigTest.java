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

package cn.enaium.joe.asm;

import cn.enaium.joe.util.ImagineBreakerHelper;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Enaium
 * @since 1.1.0
 */
public class GenerateDecompilerConfigTest {

    static {
        ImagineBreakerHelper.boot();
    }

    @Test
    public void fernFlower() throws IllegalAccessException {
        System.out.println("fernFlower config");
        Map<String, Object> defaults = IFernflowerPreferences.getDefaults();
        for (Field field : IFernflowerPreferences.class.getFields()) {
            if (field.isAnnotationPresent(IFernflowerPreferences.Name.class) && field.isAnnotationPresent(IFernflowerPreferences.Description.class)) {
                try{
                    String name = field.getAnnotation(IFernflowerPreferences.Name.class).value();
                    String fieldName = field.getAnnotation(IFernflowerPreferences.ShortName.class).value();
                    String description = field.getAnnotation(IFernflowerPreferences.Description.class).value();
                    String type = field.getAnnotation(IFernflowerPreferences.Type.class).value();
                    Object o = defaults.get((String)field.get(null));

                    if (o != null) {
                        if ("bool".equals(type)){
                            if ("1".equals(o)) {
                                o = Boolean.TRUE;
                            } else if ("0".equals(o)) {
                                o = Boolean.FALSE;
                            }
                        } else if ("string".equals(type)){
                            if ("log".equals(fieldName))
                                o = IFernflowerLogger.Severity.INFO;
                            else o = String.valueOf(o);
                        } else if ("int".equals(type)){
                            o = Integer.parseInt(String.valueOf(o));
                        }
                        System.out.println(buildConfig(fieldName, name, description, o));
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static String buildConfig(String fieldName, String name, String desc, Object defaultValue){
        String type = null;
        if (defaultValue instanceof Boolean) {
            type = "EnableValue";
        } else if (defaultValue instanceof Enum<?>) {
            type = "ModeValue<" + defaultValue.getClass().getSimpleName() + ">";
        } else if (defaultValue instanceof Integer) {
            type = "IntegerValue";
        } else if (defaultValue instanceof String){
            type = "StringValue";
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("public");
        stringBuilder.append(" ");
        stringBuilder.append(type);
        stringBuilder.append(" ");
        stringBuilder.append(fieldName);
        stringBuilder.append(" = new ");
        stringBuilder.append(type);
        stringBuilder.append("(");
        stringBuilder.append("\"").append(name).append("\"").append(", ");
        if (!defaultValue.getClass().isEnum()) {
            if (defaultValue instanceof String){
                defaultValue = '\"' + String.valueOf(defaultValue).replace("\"", "\\\"") + '\"';
            }
            stringBuilder.append(defaultValue);
        } else stringBuilder.append(defaultValue.getClass().getSimpleName()).append('.').append(defaultValue);
        stringBuilder.append(", \"").append(desc).append("\"");
        if (defaultValue instanceof Enum<?>) {
            stringBuilder.append(", ");
            stringBuilder.append("EnumSet.allOf(").append(defaultValue.getClass().getSimpleName()).append(".class)");
        }
        stringBuilder.append(");");

        return stringBuilder.toString();
    }

    @Test
    public void procyon() throws IllegalAccessException {
        System.out.println("procyon config");

        JavaFormattingOptions aDefault = JavaFormattingOptions.createDefault();
        for (Field field : JavaFormattingOptions.class.getFields()) {
            Object value = field.get(aDefault);
            System.out.println(buildConfig(field.getName(), field.getName(), field.getName(), value));
        }
    }
}
