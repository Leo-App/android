package de.leoapp_slg.core.task

interface TaskStatusListener {
    fun taskStarts()

    fun taskFinished(vararg params: Any)
}
