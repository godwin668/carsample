package com.gaocy.sample.vo;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.SpiderEnum;

import java.util.List;

/**
 * Created by godwin on 2017-04-05.
 */
public class CarDetailVo {

    // 平台来源 src
    // 城市 city
    // 帖子ID id
    // 原帖链接 url
    // 名称 name
    // 品牌ID brandId
    // 品牌名称 brandName
    // 车系ID seriesId
    // 车系名称 seriesName
    // 车型ID modelId
    // 车型名称 modelName
    // 车辆颜色 color
    // 行驶里程 mileage
    // 价格 price
    // 发布时间 postDate
    // 上牌时间 regDate
    // 联系人 contact
    // 联系电话 phone
    // 看车地址 address
    // 图片 images
    // 身份类型 identity
    // 商家名称 bizName
    // 商家id bizId
    // 帖子状态 status
    // 帖子标签 tag

    // 平台来源
    private String src;

    // 城市
    private String city;

    // 帖子ID
    private String id;

    // 原帖链接
    private String url;

    // 名称
    private String name;

    // 品牌ID
    private String brandId;

    // 品牌名称
    private String brandName;

    // 车系ID
    private String seriesId;

    // 车系名称
    private String seriesName;

    // 车型ID
    private String modelId;

    // 车型名称
    private String modelName;

    // 车辆颜色
    private String color;

    // 行驶里程
    private String mileage;

    // 价格
    private String price;

    // 发布时间
    private String postDate;

    // 上牌时间
    private String regDate;

    // 联系人ID
    private String userId;

    // 联系人名称
    private String userName;

    // 联系电话
    private String phone;

    // 看车地址
    private String address;

    // 图片
    private List<String> images;

    // 身份类型
    private String identity;

    // 商家id
    private String bizId;

    // 商家名称
    private String bizName;

    // 帖子状态
    private String status;

    // 帖子标签
    private String tag;


    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}