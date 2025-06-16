package cn.enaium.joe.util.transformer;

/**
 * The new transformer type. It can bypass transformer exclusion but can't do wildcards matching.
 */
@FunctionalInterface
public interface IExplicitTransformer<T>{

    /**
     * @param basicClass Class. Only classes matching transformed name will be fed.
     * @return Modified Class.
     */
    T transform(T basicClass);
}