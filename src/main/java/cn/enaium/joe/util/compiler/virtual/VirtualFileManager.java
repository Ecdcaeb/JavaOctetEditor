package cn.enaium.joe.util.compiler.virtual;

import javax.tools.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class VirtualFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final Map<String, VirtualJavaFileObject> sources;
    private final MemoryClassLoader classLoader;

    public VirtualFileManager(StandardJavaFileManager javaFileManager, Map<String, VirtualJavaFileObject> sources, MemoryClassLoader classLoader){
        super(javaFileManager);
        this.sources = sources;
        this.classLoader = classLoader;
    }

    public Map<String, byte[]> getClasses() {
        return sources.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getBytecode()));
    }


    @Override
    public ClassLoader getClassLoader(Location location) {
        return this.classLoader == null ? super.getClassLoader(location) : this.classLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (JavaFileObject.Kind.CLASS == kind) {
            VirtualJavaFileObject virtualJavaFileObject = new VirtualJavaFileObject(className, null);
            sources.put(className, virtualJavaFileObject);
            return virtualJavaFileObject;
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    @Override
    public <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service) throws IOException {
        return this.classLoader == null ? super.getServiceLoader(location, service) : ServiceLoader.load(service, classLoader);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        List<JavaFileObject> ret = new ArrayList<>();
        if (this.classLoader != null) {
            if ((StandardLocation.CLASS_OUTPUT.equals(location) || StandardLocation.CLASS_PATH.equals(location))
                    && kinds.contains(JavaFileObject.Kind.CLASS)) {
                for (Map.Entry<String, byte[]> e : classLoader.classes.entrySet()) {
                    if (e.getKey().startsWith(packageName + ".")) {
                        if (recurse || e.getKey().lastIndexOf('.') == packageName.length()) {
                            ret.add(new VirtualJavaClassObject(e.getKey(), e.getValue()));
                        }
                    }
                }
            }
        }
        System.out.println(Arrays.toString(ret.toArray()));
        Iterable<JavaFileObject> superList = super.list(location, packageName, kinds, recurse);
        if (superList != null) for (JavaFileObject f : superList)
            ret.add(f);
        return ret;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof VirtualJavaClassObject) {
            return file.getName();
        } else return super.inferBinaryName(location, file);
    }
}
