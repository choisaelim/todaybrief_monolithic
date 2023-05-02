package com.example.user.service;

import java.util.List;

import org.json.simple.parser.ParseException;

import com.example.user.dto.CarInfoDto;
import com.example.user.dto.MapDto;
import com.example.user.dto.WeatherInfoDto;
import com.example.user.jpa.MapEntity;

public interface MapService {
    Iterable<MapEntity> getMapbyUserId(String userId);

    List<WeatherInfoDto> getWeather(String userId) throws ParseException;

    WeatherInfoDto getWeatherAPI(MapDto map) throws ParseException;

    List<CarInfoDto> getCarTime(String userId) throws ParseException;

    CarInfoDto getCarTimeAPI(List<MapDto> mapList) throws ParseException;
}
