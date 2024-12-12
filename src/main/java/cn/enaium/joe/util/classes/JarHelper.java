package cn.enaium.joe.util.classes;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.jar.Jar;

import java.util.*;

public class JarHelper {
    public static HashMap<String, ClassNode> getAllNodes(ClassNode classNode){
        return getAllNodes(JavaOctetEditor.getInstance().getJar(), classNode);
    }

    public static HashMap<String, ClassNode> getAllNodes(Jar jar, ClassNode classNode){
        HashMap<String, ClassNode> classNodes = new HashMap<>();
        classNodes.put(classNode.getInternalName(), classNode);
        for(String clazz : classNode.getInnerClassesInternalName()){
            ClassNode cn = jar.classes.get(clazz + ".class");
            if (cn != null) {
                classNodes.put(cn.getInternalName(), cn);
            }
        }
        return classNodes;
    }
}
