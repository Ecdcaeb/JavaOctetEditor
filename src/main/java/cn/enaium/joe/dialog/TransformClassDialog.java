package cn.enaium.joe.dialog;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.api.transformer.IClassTransformer;
import cn.enaium.joe.gui.panel.CodeAreaPanel;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.classes.ASMClassLoader;
import cn.enaium.joe.util.compiler.Compiler;
import cn.enaium.joe.util.event.events.EditSaveSuccessEvent;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

public class TransformClassDialog extends Dialog {
    protected CodeAreaPanel codeAreaPanel = new CodeAreaPanel();
    protected Button button;
    public TransformClassDialog() {
        super(LangUtil.i18n("menu.attach.transform"));
        setLayout(new BorderLayout());
        setSize(700, 400);
        add(codeAreaPanel, BorderLayout.CENTER);
        codeAreaPanel.getTextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeAreaPanel.getTextArea().setEditable(true);
        String hex = Long.toHexString(System.nanoTime());
        String className = "Transformer" + hex.substring(Math.max(0, hex.length() - 12));
        String stringBuilder =
                        "import org.objectweb.asm.*;\n" +
                        "import org.objectweb.asm.tree.*;\n" +
                        "public class " + className + " implements Opcodes, cn.enaium.joe.api.transformer.IClassTransformer{\n" +
                        "    public boolean transform(ClassNode classNode) {\n" +
                        "        return false;\n" +
                        "    }\n" +
                        "}";
        codeAreaPanel.getTextArea().setText(stringBuilder);
        add(button = new Button(LangUtil.i18n("button.edit")), BorderLayout.SOUTH);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StringWriter errorTracer = new StringWriter();
                byte[] dumpClazz = Compiler.compileSingle(className, codeAreaPanel.getTextArea().getText(), errorTracer);
                if (dumpClazz == null) {
                    MessageUtil.error(errorTracer.toString());
                }
                try {
                    final IClassTransformer iClassTransformer = (IClassTransformer) new ASMClassLoader().defineClass(className, dumpClazz).getConstructor().newInstance();
                    JavaOctetEditor.getInstance().getJar().getClasses()
                            .forEach(classNode -> {
                                if (iClassTransformer.transform(classNode.getNodeInternal())) {
                                    classNode.mkdir();
                                    EditSaveSuccessEvent.trigger(classNode.getInternalName());
                                }
                            });
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException ex) {
                    MessageUtil.error("Error at transform", ex);
                }
                MessageUtil.info(LangUtil.i18n("success"));
            }
        });
    }
}
