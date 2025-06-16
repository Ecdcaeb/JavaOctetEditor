package cn.enaium.joe.util.config.util;

import cn.enaium.joe.util.config.Config;

import java.util.function.Consumer;
import java.util.function.Function;

public class CachedGlobalValue<T> implements Consumer<Config> {
    protected T value;
    protected Function<Config, T> processor;

    public CachedGlobalValue(Function<Config, T> processor){
        this.processor = processor;
        this.value = null;
    }

    @Override
    public void accept(Config config) {
        this.value = this.processor.apply(config);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value){
        this.value = value;
    }
}
