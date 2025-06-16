package cn.enaium.joe.util.transformer;

@FunctionalInterface
public interface ITransformer<T> {
    /**
     * @param name class name. Not sure why it exists. Do not use.
     * @param basicClass Class.
     * @return Transformed class.
     */
    T transform(String name, T basicClass);
}