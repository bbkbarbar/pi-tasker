package hu.barbar.tasker.todo.items;

import hu.barbar.tasker.todo.items.util.IoTDataReader;

public class IoTDataLogger extends IoTDataReader {

    @Override
    public String getClassTitle() {
        return null;
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean needToRun() {
        return false;
    }
}
