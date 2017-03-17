package com.gaocy.model.yiche;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Godwin on 11/15/16.
 */
public class YicheModelVo {
    private int brandId;                // 品牌ID
    private String brandName;           // 品牌名称
    private int seriesId;               // 车系ID
    private String seriesName;          // 车系名称
    private int modelId;                // 车型ID
    private String modelName;           // 车型名称
    private String seriesSpell;         // 车系spell

    private String modelDisplacement;   // 排量
    private String modelGear;           // 变速箱

    private String modelGuidePrice;     // 厂商指导价
    private String modelRefPrice;       // 全国参考价

    private String modelUrl;

    public YicheModelVo(int brandId, String brandName, int seriesId, String seriesName, String seriesSpell, int modelId, String modelName, String modelDisplacement, String modelGear, String modelGuidePrice, String modelRefPrice) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.seriesId = seriesId;
        this.seriesName = seriesName;
        this.seriesSpell = seriesSpell;
        this.modelId = modelId;
        this.modelName = modelName;
        this.modelDisplacement = modelDisplacement;
        this.modelGear = modelGear;
        this.modelGuidePrice = modelGuidePrice;
        this.modelRefPrice = modelRefPrice;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesSpell() {
        return seriesSpell;
    }

    public void setSeriesSpell(String seriesSpell) {
        this.seriesSpell = seriesSpell;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelDisplacement() {
        return modelDisplacement;
    }

    public void setModelDisplacement(String modelDisplacement) {
        this.modelDisplacement = modelDisplacement;
    }

    public String getModelGear() {
        return modelGear;
    }

    public void setModelGear(String modelGear) {
        this.modelGear = modelGear;
    }

    public String getModelGuidePrice() {
        return modelGuidePrice;
    }

    public void setModelGuidePrice(String modelGuidePrice) {
        this.modelGuidePrice = modelGuidePrice;
    }

    public String getModelRefPrice() {
        return modelRefPrice;
    }

    public void setModelRefPrice(String modelRefPrice) {
        this.modelRefPrice = modelRefPrice;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}