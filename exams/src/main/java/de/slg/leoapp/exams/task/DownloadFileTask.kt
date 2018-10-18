package de.slg.leoapp.exams.task

import de.slg.leoapp.core.task.ObjectCallbackTask
import java.io.*
import java.net.URL
import java.net.URLConnection

class DownloadFileTask : ObjectCallbackTask<Void?>() {
    override fun doInBackground(vararg params: Any): Void? {
        try {
            val connection: URLConnection = URL("https://ucloud4schools.de/ext/slg/leoapp_php/klausurplan/aktuell.xml")
                    .openConnection()
            connection.connectTimeout = 3000

            val download = BufferedReader(
                    InputStreamReader(
                            connection.getInputStream()
                    )
            )

            val writer = BufferedWriter(
                    OutputStreamWriter(
                            params[0] as OutputStream
                    )
            )

            for (s: String in download.readLines()) {
                writer.write(s)
                writer.newLine()
            }

            download.close()
            writer.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}