package com.gaocy.sample.vo;

import com.alibaba.fastjson.JSON;

/**
 * Created by godwin on 2017-04-05.
 */
public class BizVo {

    // ID
    private String id;

    // 城市
    private String city;

    // 名称
    private String name;

    // 地址
    private String address;

    // 电话
    private String phone;

    // 商家URL
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}