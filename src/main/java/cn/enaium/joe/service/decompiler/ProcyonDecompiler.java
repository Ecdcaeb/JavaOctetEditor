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

package cn.enaium.joe.service.decompiler;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.config.extend.ProcyonConfig;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.reflection.FieldAccessor;
import cn.enaium.joe.util.reflection.ReflectionHelper;
import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;
import org.pmw.tinylog.Logger;

import java.io.StringWriter;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class ProcyonDecompiler implements IDecompiler {
    public static JavaFormattingOptions cachedFormattingOptions;

    public static void create() {
        JavaFormattingOptions aDefault = JavaFormattingOptions.createDefault();

        JavaOctetEditor.getInstance().config.getConfigMap(ProcyonConfig.class).forEach(
                (s, value) -> {
                    try {
                        FieldAccessor<Object, JavaFormattingOptions> f = ReflectionHelper.getFieldAccessor(JavaFormattingOptions.class, value.getName());
                        Object defaultValue = f.get(aDefault);
                        if (defaultValue instanceof Enum<?>) {
                            f.set(aDefault, Enum.valueOf(((Enum<?>) defaultValue).getDeclaringClass(), (String) value.getValue()));
                        } else {
                            f.set(aDefault, value.getValue());
                        }
                    } catch (Throwable e) {
                        Logger.error(e);
                    }
                }
        );
        cachedFormattingOptions = aDefault;
    }

    @Override
    public String decompile(ClassNode classNode) {
        DecompilerSettings decompilerSettings = new DecompilerSettings();
        MetadataSystem metadataSystem = new MetadataSystem(new ITypeLoader() {
            private final InputTypeLoader backLoader = new InputTypeLoader();

            @Override
            public boolean tryLoadType(String s, Buffer buffer) {
                if (s.equals(classNode.getInternalName())) {
                    byte[] b = classNode.getClassBytes();
                    buffer.putByteArray(b, 0, b.length);
                    buffer.position(0);
                    return true;
                } else {
                    return backLoader.tryLoadType(s, buffer);
                }
            }
        });
        StringWriter stringwriter = new StringWriter();
        decompilerSettings.getLanguage().decompileType(metadataSystem.lookupType(classNode.getCanonicalName()).resolve(), new PlainTextOutput(stringwriter), new DecompilationOptions(){{
            setFullDecompilation(true);
            DecompilerSettings settings = DecompilerSettings.javaDefaults();
            settings.setJavaFormattingOptions(cachedFormattingOptions);
            setSettings(settings);
        }});
        return stringwriter.toString();
    }

    static {
        create();
    }
}
