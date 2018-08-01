package de.leoapp_slg.core.task;

@SuppressWarnings("WeakerAccess")
public interface TaskStatusListener {
    //default void taskStarts() {
    // stub
    //}
    void taskStarts();

    void taskFinished(Object... params);
}
