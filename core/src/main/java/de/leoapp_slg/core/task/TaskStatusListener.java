package de.leoapp_slg.core.task;

public interface TaskStatusListener {
    //default void taskStarts() {
    // stub
    //}
    void taskStarts();

    void taskFinished(Object... params);
}
