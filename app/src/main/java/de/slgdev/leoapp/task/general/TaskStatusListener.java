package de.slgdev.leoapp.task.general;

public interface TaskStatusListener {
    default void taskStarts() {
        // stub
    }
    void taskFinished(Object... params);
}
