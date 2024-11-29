package cn.enaium.joe.util.compiler.virtual;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class VirtualFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final Map<String, VirtualJavaFileObject> sources;
    private final MemoryClassLoader classLoader;

    public VirtualFileManager(JavaFileManager javaFileManager, Map<String, VirtualJavaFileObject> sources, Map<String, byte[]> context){
        super(javaFileManager);
        this.sources = sources;
        this.classLoader = new MemoryClassLoader(context);
    }

    public Map<String, byte[]> getClasses() {
        return sources.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getBytecode()));
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return this.classLoader;
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
}
