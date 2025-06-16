package cn.enaium.joe.util.transformer;

import org.pmw.tinylog.Logger;

import java.util.*;


public class ExplicitTransformerList<T>{
    private final Map<String, List<IExplicitTransformer<T>>> transformers;

    public ExplicitTransformerList(Map<String, List<IExplicitTransformer<T>>> map){
        this.transformers = map;
    }

    public void register(IExplicitTransformer<T> transformer, String... targets){
        if (targets.length == 0) return;
        Logger.debug("Registering explicit transformer instance: {}", transformer.getClass().getSimpleName());
        try {
            for (String target : targets) {
                if (transformers.containsKey(target)) {
                    transformers.get(target).add(transformer);
                } else {
                    LinkedList<IExplicitTransformer<T>> transformerSet = new LinkedList<>();
                    transformerSet.add(transformer);
                    transformers.put(target, transformerSet);
                }
            }

        } catch (Exception e) {
            Logger.error("Error registering explicit transformer class {}", transformer.getClass().getSimpleName(), e);
        }
    }

    public List<IExplicitTransformer<T>> getTransformer(String transformedName) {
        return transformers.get(transformedName);
    }

    public Map<String, List<IExplicitTransformer<T>>> getTransformers() {
        return transformers;
    }

    public T run(String transformedName, T value) {
        for (IExplicitTransformer<T> transformer : transformers.get(transformedName)) {
            value = transformer.transform(value);
        }
        return value;
    }
}
