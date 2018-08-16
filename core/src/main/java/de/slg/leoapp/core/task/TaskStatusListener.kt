package de.slg.leoapp.core.task

interface TaskStatusListener {
    fun taskStarts()

    fun taskFinished(vararg params: Any)
}
