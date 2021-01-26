package com.example.xiaolan.myapplication.Zigbee;

public class ZigBee {
    private Integer id;
    private String temp;
    private String hum;
    private String lux;
    private Integer flame;
    private Integer smoke;
    private Integer fan;
    private Integer lamp;
    private String fwdata;
    private String seatnum;

    public ZigBee() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getLux() {
        return lux;
    }

    public void setLux(String lux) {
        this.lux = lux;
    }

    public Integer getFlame() {
        return flame;
    }

    public void setFlame(Integer flame) {
        this.flame = flame;
    }

    public Integer getSmoke() {
        return smoke;
    }

    public void setSmoke(Integer smoke) {
        this.smoke = smoke;
    }

    public Integer getFan() {
        return fan;
    }

    public void setFan(Integer fan) {
        this.fan = fan;
    }

    public Integer getLamp() {
        return lamp;
    }

    public void setLamp(Integer lamp) {
        this.lamp = lamp;
    }

    public String getFwdata() {
        return fwdata;
    }

    public void setFwdata(String fwdata) {
        this.fwdata = fwdata;
    }

    public String getSeatnum() {
        return seatnum;
    }

    public void setSeatnum(String seatnum) {
        this.seatnum = seatnum;
    }
}
