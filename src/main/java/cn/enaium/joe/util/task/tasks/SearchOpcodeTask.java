package cn.enaium.joe.util.task.tasks;

import cn.enaium.joe.gui.panel.search.ResultNode;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.ColorUtil;
import cn.enaium.joe.util.HtmlUtil;
import cn.enaium.joe.util.asm.OpcodeUtil;
import org.objectweb.asm.tree.*;

import java.util.LinkedList;
import java.util.List;

public class SearchOpcodeTask extends SearchInstructionTask<List<ResultNode>> {

    private final String opcode;

    public SearchOpcodeTask(Jar jar, String opcode) {
        super("SearchOpcode", jar);
        this.opcode = opcode;
    }

    @Override
    public List<ResultNode> get() {
        List<ResultNode> resultNodes = new LinkedList<>();
        searchInstruction((classNode, instruction) -> {
            if (String.valueOf(instruction.getOpcode()).equals(opcode) || opcode.equals(OpcodeUtil.OPCODE.get(instruction.getOpcode()))) {
                switch (instruction) {
                    case MethodInsnNode methodInsnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(methodInsnNode.name, ColorUtil.name)
                                    + HtmlUtil.setColor(":", ColorUtil.opcode)
                                    + HtmlUtil.setColor(methodInsnNode.desc, ColorUtil.desc)
                                    + HtmlUtil.setColor(String.valueOf(methodInsnNode.itf), ColorUtil.bool)));
                    case FieldInsnNode fieldInsnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(fieldInsnNode.name, ColorUtil.name) + HtmlUtil.setColor(":", ColorUtil.opcode) + HtmlUtil.setColor(fieldInsnNode.desc, ColorUtil.desc)));
                    case IntInsnNode intInsnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(OpcodeUtil.OPCODE.get(intInsnNode.getOpcode()) + " ", ColorUtil.name) + HtmlUtil.setColor(Integer.toString(intInsnNode.operand), ColorUtil.opcode)));
                    case LineNumberNode lineNumberNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor("Line ", ColorUtil.name) + HtmlUtil.setColor(Integer.toString(lineNumberNode.line), ColorUtil.opcode)));
                    case InsnNode insnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(OpcodeUtil.OPCODE.get(insnNode.getOpcode()), ColorUtil.name)));
                    case LdcInsnNode ldcInsnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(String.valueOf(ldcInsnNode.cst), ColorUtil.string)));
                    case IincInsnNode iincInsnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(OpcodeUtil.OPCODE.get(iincInsnNode.getOpcode()) + " ", ColorUtil.name) + HtmlUtil.setColor("var: " + iincInsnNode.var + "  incr:" + iincInsnNode.incr, ColorUtil.desc)));
                    case TypeInsnNode typeInsnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(OpcodeUtil.OPCODE.get(typeInsnNode.getOpcode()) + " ", ColorUtil.name) + HtmlUtil.setColor(typeInsnNode.desc, ColorUtil.desc)));
                    case VarInsnNode varInsnNode ->
                            resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(OpcodeUtil.OPCODE.get(varInsnNode.getOpcode()) + " ", ColorUtil.name) + HtmlUtil.setColor(Integer.toString(varInsnNode.var), ColorUtil.desc)));
                    default -> {
                    }
                }
            }
        });
        return resultNodes;
    }
}
