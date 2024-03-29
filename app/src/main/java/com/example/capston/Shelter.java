package com.example.capston;

import java.io.Serializable;

public class Shelter implements Serializable {
    String name;//이름
    String address;//주소
    int number;//도로명으로 구분 유일값을 가졋다는 상징성 때문이지만 아니라도 상관없음
    double x;//경도
    double y;//위도

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int findIndex(String name) { //마커 클릭 시 매칭해줌
        if(this.name.equals(name)) {
            return number;
        }
        else return -1;
    }
}
