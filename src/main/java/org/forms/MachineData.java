package org.forms;

public enum MachineData {
    ID("ID"),
    AUTO("Машина"),
    SERIAL_NUMBER("Серийный номер"),
    HOUSE_NUMBER("Хозяйственный номер"),
    FAULT("Неисправность"),
    DATE("Время создания");
    private final String data;

    MachineData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
