package me.ele.lancet.weaver.internal.asm.classvisitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.stream.Collectors;

import me.ele.lancet.weaver.internal.asm.classvisitor.methodvisitor.TryCatchMethodVisitor;
import me.ele.lancet.weaver.internal.entity.TotalInfo;
import me.ele.lancet.weaver.internal.entity.TryCatchInfo;
import me.ele.lancet.weaver.internal.log.Log;


/**
 * Created by gengwanpeng on 17/3/27.
 */
public class TryCatchInfoClassVisitor extends ClassVisitor {

    private String className;
    private List<TryCatchInfo> infos;
    private List<TryCatchInfo> matches = null;

    public TryCatchInfoClassVisitor(int api, ClassVisitor mv, TotalInfo totalInfo) {
        super(api, mv);
        infos = totalInfo.tryCatchInfos;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        matches = infos.stream().filter(t -> t.match(name)).collect(Collectors.toList());
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (matches.size() > 0) {
            Log.tag("transform").i("visit TryCatch method: "+className+"."+name+" "+desc);
            mv = new TryCatchMethodVisitor(Opcodes.ASM5, mv, matches);
        }
        return mv;
    }
}
