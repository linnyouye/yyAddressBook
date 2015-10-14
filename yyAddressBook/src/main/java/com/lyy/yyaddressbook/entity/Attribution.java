package com.lyy.yyaddressbook.entity;

public class Attribution {

    public static final String YIDONG = "移动";
    public static final String LIANTONG = "联通";
    public static final String DIANXIN = "电信";

    private String phone;
    private String city;

    private String type;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
