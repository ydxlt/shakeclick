package lt.github.shake.click;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class ViewClickAdapter extends AdviceAdapter {

    private boolean mShakeClick = true;

    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param name          the method's name.
     * @param descriptor    the method's descriptor (see {@link Type Type}).
     */
    protected ViewClickAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
        // android.view.View.OnClickListener.onClick(android.view.View)
        if((access & ACC_PUBLIC) != 0
                && (access & ACC_STATIC) == 0
                && name.equals("onClick") && descriptor.equals("(Landroid/view/View;)V")){
            mShakeClick = false;
        }
    }

    @Override
    protected void onMethodEnter() {
        if(mShakeClick){
            return;
        }
        visitVarInsn(ALOAD,1);
        visitMethodInsn(INVOKESTATIC, "io/geek/shake/click/ShakeClickPredictor", "intercept", "(Landroid/view/View;)Z", false);
        Label label = new Label();
        visitJumpInsn(IFEQ,label);
        visitInsn(RETURN);
        visitLabel(label);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if("Lio/geek/shake/click/ShakeClick;".equals(descriptor) && !visible){
            mShakeClick = true;
            return null; // delete this annotation
        }
        return super.visitAnnotation(descriptor, visible);
    }
}
