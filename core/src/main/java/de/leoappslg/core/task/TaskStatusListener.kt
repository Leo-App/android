package de.leoappslg.core.task

interface TaskStatusListener {
    fun taskStarts()

    fun taskFinished(vararg params: Any)
}
