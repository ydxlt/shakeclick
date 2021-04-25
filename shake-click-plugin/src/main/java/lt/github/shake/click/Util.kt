package lt.github.shake.click

import com.android.SdkConstants
import java.io.File
import java.security.MessageDigest

inline fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }

inline fun String.md5() = MessageDigest.getInstance("MD5").digest(toByteArray()).toHex()

inline fun File.isClass() = name.endsWith(SdkConstants.DOT_CLASS)

inline fun String.isClass() = this.endsWith(SdkConstants.DOT_CLASS)