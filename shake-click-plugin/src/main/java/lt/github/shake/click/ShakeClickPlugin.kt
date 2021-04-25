package lt.github.shake.click

import com.android.build.gradle.AppExtension
import org.apache.commons.compress.utils.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File

class ShakeClickPlugin : BaseTransform(), Plugin<Project> {

    override val transformName: String
        get() = "ShakeClick"

    private var project: Project? = null
    private val shakeClickExtension: ShakeClickExtension? by lazy {
        project?.extensions?.findByType(ShakeClickExtension::class.java)
    }
    private val duration: Long
        get() = shakeClickExtension?.duration ?: 300

    override fun apply(project: Project) {
        this.project = project
        project.extensions.findByType(AppExtension::class.java)?.registerTransform(this)
        project.extensions.create("shakeClick", ShakeClickExtension::class.java)
    }

    override fun transformFile(output: File) {
        if (shakeClickExtension?.enabled == false) {
            return
        }
        val classReader = ClassReader(output.readBytes())
        val classWriter =
            ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val cv: ClassVisitor = ViewClickClassVisitor(classWriter, duration)
        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
        val code = classWriter.toByteArray()
        output.outputStream().use {
            it.write(code)
        }
    }

    override fun transformJar(inputJar: File, outputJar: File) {
        val jarHandler = JarHandler.create(inputJar, outputJar)
        jarHandler.filter { shakeClickExtension?.enabled == true }
            .map { _, inputStream ->
                val classReader = ClassReader(
                    IOUtils.toByteArray(inputStream)
                )
                val classWriter = ClassWriter(
                    classReader,
                    ClassWriter.COMPUTE_MAXS
                )
                val cv: ClassVisitor = ViewClickClassVisitor(classWriter, duration)
                classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                classWriter.toByteArray()
            }
    }
}