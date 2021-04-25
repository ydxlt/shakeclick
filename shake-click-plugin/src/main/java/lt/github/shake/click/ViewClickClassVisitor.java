package lt.github.shake.click;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ViewClickClassVisitor extends ClassVisitor implements Opcodes {

    private long duration;
    private String className;

    public ViewClickClassVisitor(ClassVisitor classVisitor,long duration) {
        super(Opcodes.ASM9, classVisitor);
        this.duration = duration;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access,name,descriptor,signature,exceptions);
        return new ViewClickAdapter(api,mv,access,name,descriptor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if("io/geek/shake/click/ShakeClickPredictor".equals(className)
                && "FROZEN_WINDOW_MILLIS".equals(name) && (access & Opcodes.ACC_PRIVATE) != 0
                && (access & ACC_STATIC) != 0
         && "J".equals(descriptor)){
            return super.visitField(access, name, descriptor, signature, duration);
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
