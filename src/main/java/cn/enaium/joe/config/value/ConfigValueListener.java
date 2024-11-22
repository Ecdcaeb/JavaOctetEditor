package cn.enaium.joe.config.value;

@FunctionalInterface
public interface ConfigValueListener<T> {
    void update(Value<T> instance, T oldValue, T newValue);
}
