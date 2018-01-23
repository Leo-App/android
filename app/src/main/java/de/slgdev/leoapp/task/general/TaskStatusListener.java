package de.slgdev.leoapp.task.general;

public interface TaskStatusListener {
    void taskStarts();
    void taskFinished(Object... params);
}
