package com.gaocy.sample.vo;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.SpiderEnum;

import java.util.Map;

/**
 * Created by godwin on 2017-03-16.
 */
public class CarVo {
    private SpiderEnum src;
    private String id;
    private String srcId;
    private String city;
    private String name;
    private String regDate;
    private String mileage;
    private String price;
    private String address;
    private String shopId;
    private Map<String, String> params;

    public SpiderEnum getSrc() {
        return src;
    }

    public void setSrc(SpiderEnum src) {
        this.src = src;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}