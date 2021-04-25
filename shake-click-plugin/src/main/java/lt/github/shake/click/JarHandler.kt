package lt.github.shake.click

import org.apache.commons.compress.utils.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class JarHandler(private val inputJar: File, private val outputJar: File){

    private var mFilters:MutableList<((fileName:String) -> Boolean)> = mutableListOf()

    fun filter(predicate: (fileName:String) -> Boolean) : JarHandler {
        mFilters.add(predicate)
        return this
    }

    fun map(transform:(jarEntry:JarEntry,inputStream:InputStream) -> ByteArray) {
        JarFile(inputJar).use { jarFile ->
            JarOutputStream(FileOutputStream(outputJar))?.use { outputStream ->
                jarFile.stream().forEach { jarEntry: JarEntry? ->
                    val entryName = jarEntry?.name
                    val inputStream: InputStream = jarFile.getInputStream(jarEntry)
                    outputStream.putNextEntry(ZipEntry(entryName))
                    var result = false
                    mFilters.forEach { predicate ->
                        if(entryName != null) {
                            result = result xor predicate(entryName)
                        }
                    }
                    if(result && jarEntry != null){
                        outputStream.write(transform(jarEntry,inputStream))
                    } else {
                        outputStream.write(IOUtils.toByteArray(inputStream))
                    }
                    outputStream.closeEntry()
                }
            }
        }
    }

    companion object {

        fun create(inputJar: File,outputJar: File) = JarHandler(inputJar,outputJar)
    }
}