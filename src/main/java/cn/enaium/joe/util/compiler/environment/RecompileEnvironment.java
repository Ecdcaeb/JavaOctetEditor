package cn.enaium.joe.util.compiler.environment;

import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.reflection.ReflectionHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

public class RecompileEnvironment {
    public static int getJavaVersion(){
        return 21;
    }

    public static Map<String, byte[]> environment = null;

    public static Map<String, byte[]> getEnvironment(){
        return environment;
    }

    public static Map<String, byte[]> build(Jar jar, IntConsumer progress){
        Map<String, ClassCatch> classes = new HashMap<>();
        int total = jar.classes.size();
        int done = 0;
        for(ClassNode classNode : jar.classes.values()){
            applyClassNode(classes, classNode);
            done ++;
            progress.accept((int) ((double) done / total * 70f));
        }

        classes.remove("B");
        classes.remove("C");
        classes.remove("I");
        classes.remove("S");
        classes.remove("Z");
        classes.remove("J");
        classes.remove("F");
        classes.remove("D");

        progress.accept(70);
        Set<String> innerClasses = new HashSet<>();

        classes = classes.entrySet().stream()
                .filter(entry -> entry!= null &&entry.getKey() != null && entry.getValue() != null)
                .filter(entry -> !entry.getKey().startsWith("java/"))
                .filter(entry -> !entry.getKey().startsWith("jdk/"))
                .filter(entry -> !entry.getKey().startsWith("sun/"))
                .filter(entry -> !entry.getKey().startsWith("javax/"))
                .filter(entry -> !entry.getKey().startsWith("com/sun/"))
                .filter(entry -> !ReflectionHelper.isClassExist(entry.getKey().replace('/', '.')))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1));
        progress.accept(80);
        Map<String, ClassNode> classNodes = new HashMap<>();

        for(Map.Entry<String, ClassCatch> entry : classes.entrySet()){
            if (entry.getKey().contains("$")) innerClasses.add(entry.getKey());
            ClassCatch classCatch = entry.getValue();
            ClassNode classNode = new ClassNode();
            classNode.visit(getJavaVersion(), classCatch.access, classCatch.internalName, null, "java/lang/Object", new String[0]);

            for(AccessibleObject field : classCatch.fields){
                classNode.fields.add(new FieldNode(field.access, field.name, field.desc, null, null));
            }

            for(AccessibleObject method : classCatch.fields){
                MethodNode methodNode = new MethodNode(method.access, method.name, method.desc, null, new String[0]);
                if (method.desc.endsWith("V")){
                    methodNode.visitInsn(Opcodes.RETURN);
                } else if (method.desc.endsWith(";")){
                    methodNode.visitInsn(Opcodes.ACONST_NULL);
                    methodNode.visitInsn(Opcodes.ARETURN);
                } else if (method.desc.endsWith("B") || method.desc.endsWith("C") || method.desc.endsWith("I") || method.desc.endsWith("S") || method.desc.endsWith("Z")){
                    methodNode.visitInsn(Opcodes.ICONST_0);
                    methodNode.visitInsn(Opcodes.IRETURN);
                } else if (method.desc.endsWith("J")) {
                    methodNode.visitInsn(Opcodes.ICONST_0);
                    methodNode.visitInsn(Opcodes.LRETURN);
                } else if (method.desc.endsWith("F")){
                    methodNode.visitInsn(Opcodes.FCONST_0);
                    methodNode.visitInsn(Opcodes.FRETURN);
                } else if (method.desc.endsWith("D")){
                    methodNode.visitInsn(Opcodes.DCONST_0);
                    methodNode.visitInsn(Opcodes.DRETURN);
                }
                classNode.methods.add(methodNode);
            }

            classNodes.put(classCatch.internalName, classNode);
        }
        progress.accept(90);

        for(String name : innerClasses){
            String cls = name.substring(0, name.lastIndexOf('$'));
            if (classNodes.containsKey(cls)){
                classNodes.get(cls).innerClasses.add(new InnerClassNode(name, cls, name.replace('/', '.'), classNodes.get(name).access));
                classNodes.get(name).outerClass = cls;
            }
        }
        progress.accept(95);

        Collection<ClassNode> classNodeSet = classNodes.values();
        Map<String, byte[]> environment_ = HashMap.newHashMap(classNodeSet.size());
        for(ClassNode classNode : classNodeSet){
            environment_.put(classNode.name.replace('/', '.'), cn.enaium.joe.util.classes.ClassNode.of(classNode).getClassBytes());
        }
        progress.accept(99);

        return environment_;
    }

    public static void applyClassNode(Map<String, ClassCatch> internalName2ClassCatchers, ClassNode classNode){
        internalName2ClassCatchers.computeIfAbsent(classNode.superName, (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC));
        for(String interfaces : classNode.interfaces){
            internalName2ClassCatchers.computeIfAbsent(interfaces, (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC | Opcodes.ACC_INTERFACE));
        }

        if (classNode.visibleAnnotations != null) for(AnnotationNode annotationNode : classNode.visibleAnnotations){
            String clazz = Type.getType(annotationNode.desc).getInternalName();
            internalName2ClassCatchers.computeIfAbsent(clazz, (key) ->  new ClassCatch(key, Opcodes.ACC_PUBLIC | Opcodes.ACC_ANNOTATION));
        }

        if (classNode.invisibleAnnotations != null) for(AnnotationNode annotationNode : classNode.invisibleAnnotations){
            String clazz = Type.getType(annotationNode.desc).getInternalName();
            internalName2ClassCatchers.computeIfAbsent(clazz, (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC | Opcodes.ACC_ANNOTATION));
        }

        for(FieldNode fieldNode : classNode.fields){
            internalName2ClassCatchers.computeIfAbsent(Type.getType(fieldNode.desc).getInternalName(), (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC));
            if (fieldNode.visibleAnnotations != null) for(AnnotationNode annotationNode : fieldNode.visibleAnnotations){
                String clazz = Type.getType(annotationNode.desc).getInternalName();
                internalName2ClassCatchers.computeIfAbsent(clazz, (key) ->  new ClassCatch(key, Opcodes.ACC_PUBLIC | Opcodes.ACC_ANNOTATION));
            }

            if (fieldNode.invisibleAnnotations != null) for(AnnotationNode annotationNode : fieldNode.invisibleAnnotations){
                String clazz = Type.getType(annotationNode.desc).getInternalName();
                internalName2ClassCatchers.computeIfAbsent(clazz, (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC | Opcodes.ACC_ANNOTATION));
            }
        }

        for(MethodNode methodNode : classNode.methods){
            for(Type type : Type.getArgumentTypes(methodNode.desc)){
                internalName2ClassCatchers.computeIfAbsent(type.getInternalName(), (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC));
            }
            if (methodNode.visibleAnnotations != null) for(AnnotationNode annotationNode : methodNode.visibleAnnotations){
                String clazz = Type.getType(annotationNode.desc).getInternalName();
                internalName2ClassCatchers.computeIfAbsent(clazz, (key) ->  new ClassCatch(key, Opcodes.ACC_PUBLIC | Opcodes.ACC_ANNOTATION));
            }

            if (methodNode.invisibleAnnotations != null) for(AnnotationNode annotationNode : methodNode.invisibleAnnotations){
                String clazz = Type.getType(annotationNode.desc).getInternalName();
                internalName2ClassCatchers.computeIfAbsent(clazz, (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC | Opcodes.ACC_ANNOTATION));
            }

            Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
            AbstractInsnNode node;
            while (iterator.hasNext()){
                node = iterator.next();
                if (node instanceof FieldInsnNode fieldInsnNode) {
                    int opcode = fieldInsnNode.getOpcode();
                    internalName2ClassCatchers
                            .computeIfAbsent(fieldInsnNode.owner, (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC))
                            .addField(fieldInsnNode.name, fieldInsnNode.desc, opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC);
                    internalName2ClassCatchers.computeIfAbsent(Type.getType(fieldInsnNode.desc).getInternalName(), (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC));
                } else if (node instanceof MethodInsnNode methodInsnNode) {
                    int opcode = methodInsnNode.getOpcode();
                    internalName2ClassCatchers
                            .computeIfAbsent(methodInsnNode.owner, (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC))
                            .addMethod(methodInsnNode.name, methodInsnNode.desc, opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.PUTSTATIC, methodInsnNode.itf);
                    for(Type type : Type.getArgumentTypes(methodInsnNode.desc)){
                        internalName2ClassCatchers.computeIfAbsent(type.getInternalName(), (key) -> new ClassCatch(key, Opcodes.ACC_PUBLIC));
                    }
                }
            }
        }
    }
    public static class ClassCatch{
        public final String internalName;
        public int access;
        public final Set<AccessibleObject> methods = new HashSet<>();
        public final Set<AccessibleObject> fields = new HashSet<>();
        public ClassCatch(String internalName, int access){
            this.internalName = internalName;
            this.access = access;
        }
        public void addStaticMethod(String name, String desc){
            this.methods.add(new AccessibleObject(name, desc, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC));
        }
        public void addInstanceMethod(String name, String desc){
            this.methods.add(new AccessibleObject(name, desc, Opcodes.ACC_PUBLIC));
        }
        public void addMethod(String name, String desc, boolean isStatic, boolean itf){
            if (itf) this.access = this.access | Opcodes.ACC_INTERFACE;
            if (isStatic) addStaticMethod(name, desc);
            else addInstanceMethod(name, desc);
        }

        public void addStaticField(String name, String desc){
            this.fields.add(new AccessibleObject(name, desc, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC));
        }
        public void addInstanceField(String name, String desc){
            this.fields.add(new AccessibleObject(name, desc, Opcodes.ACC_PUBLIC));
        }
        public void addField(String name, String desc, boolean isStatic){
            if (isStatic) addStaticField(name, desc);
            else addInstanceField(name, desc);
        }
    }
    public record AccessibleObject(String name, String desc, int access){}
}
