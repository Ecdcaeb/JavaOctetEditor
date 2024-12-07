package cn.enaium.joe.task;

import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.compiler.environment.RecompileEnvironment;

import java.util.Map;

public class BuildEnvironmentTask extends AbstractTask<Map<String, byte[]>> {
    Jar jar;
    public BuildEnvironmentTask(Jar jar) {
        super(" BuildEnvironment");
        this.jar = jar;
    }

    @Override
    public Map<String, byte[]> get() {
        RecompileEnvironment.environment.setValue(RecompileEnvironment.build(jar, this::setProgress));
        this.setProgress(100);
        return RecompileEnvironment.getEnvironment();
    }
}
