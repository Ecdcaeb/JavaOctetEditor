package cn.enaium.joe.util.reflection;

import java.io.Serial;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class ReflectionHelper {
    public static class UnableToFindMethodException extends RuntimeException
    {
        @Serial
        private static final long serialVersionUID = 1L;

        public UnableToFindMethodException(String[] methodNames, Exception failed)
        {
            super(Arrays.toString(methodNames), failed);
        }

        public UnableToFindMethodException(Throwable failed)
        {
            super(failed);
        }

    }

    public static class UnableToFindClassException extends RuntimeException
    {
        @Serial
        private static final long serialVersionUID = 1L;

        public UnableToFindClassException(String[] classNames, Exception err)
        {
            super(Arrays.toString(classNames), err);
        }

    }

    public static class UnableToAccessFieldException extends RuntimeException
    {
        @Serial
        private static final long serialVersionUID = 1L;

        public UnableToAccessFieldException(String[] fieldNames, Exception e)
        {
            super(Arrays.toString(fieldNames), e);
        }
    }

    public static class UnableToFindFieldException extends RuntimeException
    {
        @Serial
        private static final long serialVersionUID = 1L;

        public UnableToFindFieldException(String[] fieldNameList, Exception e)
        {
            super(Arrays.toString(fieldNameList), e);
        }
    }

    public static class UnknownConstructorException extends RuntimeException
    {
        public UnknownConstructorException(final String message)
        {
            super(message);
        }
    }

    public static Field findField(Class<?> clazz, String... fieldNames)
    {
        Exception failed = null;
        for (String fieldName : fieldNames)
        {
            try
            {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            }
            catch (Exception e)
            {
                failed = e;
            }
        }
        throw new UnableToFindFieldException(fieldNames, failed);
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class <? super E > classToAccess, E instance, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T) f.get(instance);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(new String[0], e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class <? super E > classToAccess, E instance, String... fieldNames)
    {
        try
        {
            return (T) findField(classToAccess, fieldNames).get(instance);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, E value, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(new String[0] , e);
        }
    }

    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, E value, String... fieldNames)
    {
        try
        {
            findField(classToAccess, fieldNames).set(instance, value);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? super Object> getClass(ClassLoader loader, String... classNames)
    {
        Exception err = null;
        for (String className : classNames)
        {
            try
            {
                return (Class<? super Object>) Class.forName(className, false, loader);
            }
            catch (Exception e)
            {
                err = e;
            }
        }

        throw new UnableToFindClassException(classNames, err);
    }

    /**
     * Finds a method with the specified name and parameters in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the method is not found.
     *
     * @param clazz          The class to find the method on.
     * @param methodName     The name of the method to find (used in developer environments, i.e. "getWorldTime").
     * @param parameterTypes The parameter types of the method to find.
     * @return The method with the specified name and parameters in the given class.
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
    {
        try
        {
            Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
            m.setAccessible(true);
            return m;
        }
        catch (Exception e)
        {
            throw new UnableToFindMethodException(e);
        }
    }

    /**
     * Finds a constructor in the specified class that has matching parameter types.
     *
     * @param klass The class to find the constructor in
     * @param parameterTypes The parameter types of the constructor.
     * @param <T> The type
     * @return The constructor
     * @throws NullPointerException if {@code klass} is null
     * @throws NullPointerException if {@code parameterTypes} is null
     * @throws UnknownConstructorException if the constructor could not be found
     */
    public static <T> Constructor<T> findConstructor(final Class<T> klass, final Class<?>... parameterTypes)
    {
        Objects.requireNonNull(klass, "class");
        Objects.requireNonNull(parameterTypes, "parameter types");

        final Constructor<T> constructor;
        try
        {
            constructor = klass.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        }
        catch (final NoSuchMethodException e)
        {
            final StringBuilder desc = new StringBuilder();
            desc.append(klass.getSimpleName()).append('(');
            for (Class<?> parameterType : parameterTypes) {
                desc.append(parameterType.getName());
            }
            desc.append(')');
            throw new UnknownConstructorException("Could not find constructor '" + desc + "' in " + klass);
        }
        return constructor;
    }

    public static<R, I> MethodAccessor<R, I> getMethodAccessor(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method = findMethod(clazz, methodName, parameterTypes);
        return new MethodAccessor<>(method);
    }

    public static<T,E> FieldAccessor<T,E> getFieldAccessor(Class<?> clazz, String... fieldNames){
        Field field = findField(clazz, fieldNames);
        return new FieldAccessor<>(field);
    }
}
