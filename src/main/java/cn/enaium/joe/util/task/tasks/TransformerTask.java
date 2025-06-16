package cn.enaium.joe.util.task.tasks;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.task.AbstractTask;
import cn.enaium.joe.util.transformer.ITransformer;

public class TransformerTask extends AbstractTask<Boolean> {
    private final ITransformer<ClassNode> transformer;

    public TransformerTask(ITransformer<ClassNode> classNodeITransformer) {
        super("Transforming");
        this.transformer = classNodeITransformer;
    }

    @Override
    public Boolean get() {
        JavaOctetEditor.getInstance().getJar().getClasses().forEach((classNode -> {
            transformer.transform(classNode.getCanonicalName(), classNode);
            classNode.mkdir();
        }));
        JavaOctetEditor.getInstance().refreshJar();
        return true;
    }


}
