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

package cn.enaium.joe.gui.panel.file.tabbed.tab.classes;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.config.extend.ApplicationConfig;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.compiler.Compiler;
import cn.enaium.joe.event.events.EditSaveSuccessEvent;
import cn.enaium.joe.gui.panel.CodeAreaPanel;
import cn.enaium.joe.util.*;
import cn.enaium.joe.util.classes.ASMClassLoader;
import cn.enaium.joe.util.reflection.ReflectionHelper;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Enaium
 */
public class ASMifierTablePanel extends ClassNodeTabPanel {
    protected CodeAreaPanel codeAreaPanel;
    public ASMifierTablePanel(ClassNode classNode) {
        super(classNode);
        setLayout(new BorderLayout());
        CodeAreaPanel codeAreaPanel = this.codeAreaPanel = new CodeAreaPanel() {{
            KeyStrokeUtil.register(getTextArea(), JavaOctetEditor.getInstance().config.getByClass(ApplicationConfig.class).keymap.getValue().save.getValue(), () -> {
                try {
                    String className = "ASMifier" + Integer.toHexString(classNode.getInternalName().hashCode()) + Integer.toHexString(getTextArea().getText().hashCode());
                    String stringBuilder =
                            "import org.objectweb.asm.AnnotationVisitor;" +
                            "import org.objectweb.asm.Attribute;" +
                            "import org.objectweb.asm.ClassReader;" +
                            "import org.objectweb.asm.ClassWriter;" +
                            "import org.objectweb.asm.ConstantDynamic;" +
                            "import org.objectweb.asm.FieldVisitor;" +
                            "import org.objectweb.asm.Handle;" +
                            "import org.objectweb.asm.Label;" +
                            "import org.objectweb.asm.MethodVisitor;" +
                            "import org.objectweb.asm.Opcodes;" +
                            "import org.objectweb.asm.RecordComponentVisitor;" +
                            "import org.objectweb.asm.ModuleVisitor;" +
                            "import org.objectweb.asm.Type;" +
                            "import org.objectweb.asm.TypePath;" +
                            "public class " + className + " implements Opcodes" +
                            "{" +
                            "public static byte[] dump() throws Exception {" +
                            getTextArea().getText() +
                            "return classWriter.toByteArray();" +
                            "}" +
                            "}";

                    StringWriter errorTracer = new StringWriter();
                    byte[] dumpClazz = Compiler.compileSingle(className, stringBuilder, errorTracer);
                    if (dumpClazz == null) {
                        MessageUtil.error(errorTracer.toString());
                    }
                    byte[] dumps = (byte[])new ASMClassLoader().defineClass(className, dumpClazz).getMethod("dump").invoke(null);
                    classNode.accept(ClassNode.of(dumps));
                    MessageUtil.info(LangUtil.i18n("success"));
                    EditSaveSuccessEvent.trigger(classNode.getInternalName());
                } catch (Throwable e) {
                    MessageUtil.error(e);
                }
            });
        }};
        codeAreaPanel.getTextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeAreaPanel.getTextArea().setEditable(true);
        update();
        add(codeAreaPanel);
    }

    public static String getSimpleName(String name){
        int idx = name.lastIndexOf('/');
        if (idx != -1){
            return name.substring(idx + 1, name.length() - 1);
        } else return name.substring(name.lastIndexOf('.') + 1, name.length() - 1);
    }

    public void update(){
        StringWriter stringWriter = new StringWriter();

        ASyncUtil.execute(() -> {
            this.getClassNode().trace(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(stringWriter)));
        }, () -> {
            String trim = getMiddle(getMiddle(stringWriter.toString())).trim();
            codeAreaPanel.getTextArea().setText(trim.substring(0, trim.lastIndexOf("\n")));
            codeAreaPanel.getTextArea().setCaretPosition(0);
        });
    }

    public String getMiddle(String s) {
        return s.substring(s.indexOf("{") + 1, s.lastIndexOf("}"));
    }
}
