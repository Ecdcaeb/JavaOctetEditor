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
import cn.enaium.joe.util.config.extend.ApplicationConfig;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.compiler.Compiler;
import cn.enaium.joe.util.event.events.EditSaveSuccessEvent;
import cn.enaium.joe.gui.panel.CodeAreaPanel;
import cn.enaium.joe.util.*;
import cn.enaium.joe.util.classes.ASMClassLoader;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.objectweb.asm.*;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.tinylog.Logger;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

/**
 * @author Enaium
 */
public class ASMifierTablePanel extends ClassNodeTabPanel {
    protected CodeAreaPanel codeAreaPanel;
    protected AutoCompletion autoCompletion;
    public ASMifierTablePanel(ClassNode classNode) {
        super(classNode);
        setLayout(new BorderLayout());
        CodeAreaPanel codeAreaPanel = this.codeAreaPanel = new CodeAreaPanel() {{
            KeyStrokeUtil.register(getTextArea(), JavaOctetEditor.getInstance().CONFIG.getByClass(ApplicationConfig.class).keymap.getValue().save.getValue(), () -> {
                if (ClassTabPanel.classTabIndex == 2) {
                    try {
                        String className = "ASMifier" + Integer.toHexString(classNode.getInternalName().hashCode()) + Integer.toHexString(getTextArea().getText().hashCode());
                        String stringBuilder =
                                        "import org.objectweb.asm.*;" +
                                        "public class " + className + " implements Opcodes {" +
                                        "public static byte[] dump() throws Exception {" +
                                             getTextArea().getText() +
                                        "return classWriter.toByteArray();}} ";

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
                }
            });
        }};
        add(codeAreaPanel);
        codeAreaPanel.getTextArea().setCodeFoldingEnabled(true);
        codeAreaPanel.getTextArea().setEditable(true);
        codeAreaPanel.getTextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        AutoCompletion ac = autoCompletion = new AutoCompletion(new DefaultCompletionProvider());
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(100);
        ac.setAutoCompleteSingleChoices(false);
        ac.setParameterAssistanceEnabled(true);
        ac.install(codeAreaPanel.getTextArea());

        update();
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
            DefaultCompletionProvider defaultCompletionProvider = (DefaultCompletionProvider) autoCompletion.getCompletionProvider();
            defaultCompletionProvider.clear();
            updateCompletionProvider(defaultCompletionProvider, codeAreaPanel.getTextArea().getText());
        });
    }

    public String getMiddle(String s) {
        return s.substring(s.indexOf("{") + 1, s.lastIndexOf("}"));
    }

    public static void updateCompletionProvider(DefaultCompletionProvider defaultCompletionProvider, String baseString) {
        List<Completion> completions = new ArrayList<>(0);
        for (String str : new HashSet<>(extractQuotedStrings(baseString))) {
            completions.add(new DynamicBasicCompletion(defaultCompletionProvider, str));
        }

        for (String str : completionProvider()) {
            completions.add(new BasicCompletion(defaultCompletionProvider, str));
        }
        defaultCompletionProvider.addCompletions(completions);
        defaultCompletionProvider.setAutoActivationRules(true, ".abcdefghijklmnopqrstuvwxyz");
    }

    private static class DynamicBasicCompletion extends BasicCompletion{
        public DynamicBasicCompletion(CompletionProvider provider, String replacementText) {
            super(provider, replacementText);
        }
    }
    public static List<String> extractQuotedStrings(String text) {
        List<String> result = new ArrayList<>();
        int start = -1;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '"') {
                if (start == -1) {
                    start = i;
                } else {
                    String extracted = text.substring(start + 1, i);
                    result.add(extracted);
                    start = -1; // 重置标记
                }
            }
        }
        Logger.error("String Key Words for:" + text);
        Logger.error("String Key Words:" + Arrays.toString(result.toArray()));
        return result;
    }
    private static Set<String> $provider = null;
    private static Set<String> completionProvider() {
        if ($provider != null) return $provider;
        else {
            HashSet<String> keyWords = new HashSet<>();

            for (Field field : Opcodes.class.getFields()) {
                keyWords.add(field.getName());
            }
            for (Method method : MethodVisitor.class.getMethods()) {
                keyWords.add("methodVisitor." + method.getName());
            }
            for (Method method : FieldVisitor.class.getMethods()) {
                keyWords.add("fieldVisitor." + method.getName());
            }
            for (Method method : ClassWriter.class.getMethods()) {
                keyWords.add("classWriter." + method.getName());
            }
            for (Method method : AnnotationVisitor.class.getMethods()) {
                keyWords.add("annotationVisitor0." + method.getName());
            }
            for (Method method : RecordComponentVisitor.class.getMethods()) {
                keyWords.add("recordComponentVisitor." + method.getName());
            }

            keyWords.add("classWriter");
            keyWords.add("fieldVisitor");
            keyWords.add("recordComponentVisitor");
            keyWords.add("methodVisitor");
            keyWords.add("annotationVisitor0");

            return $provider = keyWords;
        }
    }
}
