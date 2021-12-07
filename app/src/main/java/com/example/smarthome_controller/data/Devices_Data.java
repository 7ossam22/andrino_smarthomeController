package com.example.smarthome_controller.data;

public class Devices_Data {
    private int ID;
    private String Name,IP,Status ,Device_img;

    public String getDevice_img() {
        return Device_img;
    }

    public void setDevice_img(String device_img) {
        Device_img = device_img;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }


}
