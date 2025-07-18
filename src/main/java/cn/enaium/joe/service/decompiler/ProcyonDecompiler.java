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
import cn.enaium.joe.util.config.util.CachedGlobalValue;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.classes.JarHelper;
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
import org.tinylog.Logger;

import java.io.StringWriter;
import java.util.HashMap;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class ProcyonDecompiler implements IDecompiler {
    public static final CachedGlobalValue<JavaFormattingOptions> cachedFormattingOptions = new CachedGlobalValue<>(config -> {
        JavaFormattingOptions aDefault = JavaFormattingOptions.createDefault();

        JavaOctetEditor.getInstance().CONFIG.getConfigMap(config).forEach(
                (s, value) -> {
                    try {
                        FieldAccessor<Object, JavaFormattingOptions> f = ReflectionHelper.getFieldAccessor(JavaFormattingOptions.class, value.getName());
                        f.set(aDefault, value.getValue());
                    } catch (Throwable e) {
                        Logger.error(e);
                    }
                }
        );
        return aDefault;
    });

    public HashMap<String, ClassNode> activeNodes;
    @Override
    public String decompile(ClassNode classNode) {
        DecompilerSettings decompilerSettings = new DecompilerSettings();
        this.activeNodes = JarHelper.getAllNodes(classNode);
        MetadataSystem metadataSystem = new MetadataSystem(new ITypeLoader() {
            private final InputTypeLoader backLoader = new InputTypeLoader();

            @Override
            public boolean tryLoadType(String s, Buffer buffer) {
                if (ProcyonDecompiler.this.activeNodes.containsKey(s)) {
                    byte[] b = ProcyonDecompiler.this.activeNodes.get(s).getClassBytes();
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
            settings.setJavaFormattingOptions(cachedFormattingOptions.getValue());
            setSettings(settings);
        }});
        return stringwriter.toString();
    }
}
