package cn.enaium.joe.util.transformer;


import org.tinylog.Logger;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class TransformerList<T> {
    private final List<ITransformer<T>> transformers;

    public TransformerList(List<ITransformer<T>> list){
        this.transformers = list;
    }

    public List<ITransformer<T>> getTransformers() {
        return transformers;
    }

    public void register(ITransformer<T> transformer){
        Logger.debug("Registering transformer instance: {}", transformer.getClass().getName());
        transformers.add(transformer);
    }

    public void unregister(ITransformer<T> transformer) {
        Logger.debug("Unregistering transformer: {}", transformer.getClass());
        try {
            transformers.remove(transformer);
        } catch (Exception ignored) {
        }
    }

    public void unregister(Predicate<ITransformer<T>> predicate) {
        try {
            transformers.removeIf(predicate);
        } catch (Exception e) {
            Logger.error("Error removing transformers. {}", e);
        }
    }

    public T run(String name, T value) {
        for(ITransformer<T> transformer : Collections.unmodifiableList(transformers)) {
            value = transformer.transform(name,  value);
        }
        return value;
    }
}
