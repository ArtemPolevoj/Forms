package org.forms;

import java.util.Map;

public class Report {
    private String id;
    private String date;
    private String client;
    private String auto;
    private String serialNumber;
    private String houseNumber;
    private String workHours;
    private String clear;
    private String userReport;
    private Map<String, String> answer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getWorkHours() {
        return workHours;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    public String getClear() {
        return clear;
    }

    public void setClear(String clear) {
        this.clear = clear;
    }

    public String getUserReport() {
        return userReport;
    }

    public void setUserReport(String userReport) {
        this.userReport = userReport;
    }

    public Map<String, String> getAnswer() {
        return answer;
    }

    public void setAnswer(Map<String, String> answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", client='" + client + '\'' +
                ", auto='" + auto + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", workHours='" + workHours + '\'' +
                ", clear='" + clear + '\'' +
                ", userReport='" + userReport + '\'' +
                '}';
    }
}
