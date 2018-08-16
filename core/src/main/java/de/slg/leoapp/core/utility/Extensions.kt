package de.leoappslg.core.utility

import java.io.BufferedReader
import java.io.File

//File
private fun File.contains(s: String): Boolean {
    val reader = inputStream().bufferedReader()
    return reader.use(BufferedReader::readText).contains(s)
}