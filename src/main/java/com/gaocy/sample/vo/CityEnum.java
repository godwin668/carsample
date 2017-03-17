package com.gaocy.sample.vo;

/**
 * Created by godwin on 2017/3/17.
 */
public enum CityEnum {

    beijing("北京", "beijing", "bj"),
    shanghai("上海", "shanghai", "sh"),
    hangzhou("杭州", "hangzhou", "hz"),
    suzhou("苏州", "suzhou", "sz"),
    ningbo("宁波", "ningbo", "nb"),
    ;

    private String name;
    private String pinyin;
    private String py;

    CityEnum(String name, String pinyin, String py) {
        this.name = name;
        this.pinyin = pinyin;
        this.py = py;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPy() {
        return py;
    }

    public void setPy(String py) {
        this.py = py;
    }

    @Override
    public String toString() {
        return this.pinyin;
    }

    public static void main(String[] args) {
        System.out.println(CityEnum.beijing);
        System.out.println(CityEnum.beijing == CityEnum.valueOf("beijing"));
    }
}