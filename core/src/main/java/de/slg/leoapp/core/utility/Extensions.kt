package de.slg.leoapp.core.utility

import java.io.BufferedReader
import java.io.File


//File
fun File.contains(s: String): Boolean {
    val reader = inputStream().bufferedReader()
    return reader.use(BufferedReader::readText).contains(s)
}

//ByteArray
fun ByteArray.toHexString(): String {
    val hexArray = "0123456789abcdef".toCharArray()
    val hexChars = CharArray(size * 2)
    for (j in 0 until size) {
        val v = 0xFF and get(j).toInt()
        hexChars[j * 2] = hexArray[v ushr 4]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}