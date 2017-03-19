package com.gaocy.sample.spider;

import com.gaocy.sample.vo.CityEnum;
import com.gaocy.sample.vo.CarVo;

import java.util.List;

/**
 * Created by Godwin on 11/16/16.
 */
public interface Spider {

    public CityEnum[] getCityArr();

    List<CarVo> listByCity(CityEnum city);

}