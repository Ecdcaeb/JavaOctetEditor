/*
 * Copyright 2022 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.joe.service.decompiler;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.config.util.CachedGlobalValue;
import cn.enaium.joe.util.MessageUtil;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.classes.JarHelper;
import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.apiunreleased.ClassFileSource2;
import org.benf.cfr.reader.apiunreleased.JarContent;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.state.TypeUsageInformation;
import org.benf.cfr.reader.util.AnalysisType;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Enaium
 * @since 0.7.0
 */
public class CFRDecompiler implements IDecompiler {

    public static final CachedGlobalValue<Options> options = new CachedGlobalValue<>(config -> OptionsImpl.getFactory().create(JavaOctetEditor.getInstance().CONFIG.getConfigMapStrings(config)));

    public HashMap<String, ClassNode> activeNodes;
    @Override
    public String decompile(final ClassNode classNode) {
        this.activeNodes = JarHelper.getAllNodes(classNode);
        DCCommonState state = new DCCommonState(options.getValue(), new ClassFileSource2() {
            @Override
            public Pair<byte[], String> getClassFileContent(String path) {
                String name = path.substring(0, path.length() - 6);
                if (activeNodes.containsKey(name)) {
                    return Pair.make(activeNodes.get(name).getClassBytes(), name);
                } else {
                    if (JavaOctetEditor.getInstance().getJar().hasClass(name)) {
                        return Pair.make(JavaOctetEditor.getInstance().getJar().getClassNode(name).getClassBytes(), name);
                    }
                }
                return null;
            }
            @Override public void informAnalysisRelativePathDetail(String a, String b) {}
            @Override public JarContent addJarContent(String s, AnalysisType analysisType) {return null;}
            @Override public String getPossiblyRenamedPath(String path) {return path;}
            @Override public Collection<String> addJar(String arg0) {return Collections.emptySet();}
        });
        return decompile(state, classNode.getInternalName());
    }

    public static String decompile(DCCommonState state, String internalName){
        try {
            Main.doClass(state, internalName, false, PluginDumperFactory.INSTANCE);
            return PluginDumperFactory.INSTANCE.getResult();
        } catch (Exception e) {
            MessageUtil.error(e);
            return e.toString();
        }
    }

    private static class PluginDumperFactory implements DumperFactory {
        public static final PluginDumperFactory INSTANCE = new PluginDumperFactory();

        private final IllegalIdentifierDump illegalIdentifierDump = new IllegalIdentifierDump.Nop();
        private StringBuilder outBuffer;

        public PluginDumperFactory() {
            this.outBuffer = new StringBuilder();
        }

        public Dumper getNewTopLevelDumper(JavaTypeInstance classType, SummaryDumper summaryDumper, TypeUsageInformation typeUsageInformation, IllegalIdentifierDump illegalIdentifierDump) {
            return new StringStreamDumper(new MethodErrorCollector.SummaryDumperMethodErrorCollector(classType, summaryDumper), this.outBuffer, typeUsageInformation, CFRDecompiler.options.getValue(), this.illegalIdentifierDump);
        }
        public Dumper wrapLineNoDumper(Dumper dumper) {
            return dumper;
        }
        public SummaryDumper getSummaryDumper() {
            return !CFRDecompiler.options.getValue().optionIsSet(OptionsImpl.OUTPUT_DIR) ? new NopSummaryDumper() : new FileSummaryDumper(CFRDecompiler.options.getValue().getOption(OptionsImpl.OUTPUT_DIR), CFRDecompiler.options.getValue(), null);
        }
        public ProgressDumper getProgressDumper() {
            return ProgressDumperNop.INSTANCE;
        }
        public ExceptionDumper getExceptionDumper() {
            return new StdErrExceptionDumper();
        }
        public DumperFactory getFactoryWithPrefix(String prefix, int version) {
            return this;
        }
        public String getResult(){
            String s = outBuffer.toString();
            outBuffer = new StringBuilder();
            return s;
        }
    }

}
