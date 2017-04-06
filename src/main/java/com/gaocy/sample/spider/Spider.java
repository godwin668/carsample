package com.gaocy.sample.spider;

import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.CarVo;

import java.util.List;

/**
 * Created by Godwin on 11/16/16.
 */
public interface Spider {

    public String[] getCityNameArr();

    List<CarVo> listByCityName(String cityName);

    CarDetailVo getByUrl(CarVo carVo);

}