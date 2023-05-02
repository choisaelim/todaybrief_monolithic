package com.example.user.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.modelmapper.ModelMapper;

import com.example.user.dto.CarInfoDto;
import com.example.user.dto.MapDto;
import com.example.user.dto.ResponseMap;
import com.example.user.dto.WeatherInfoDto;
import com.example.user.jpa.MapEntity;
import com.example.user.jpa.MapRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {
    private final MapRepository repository;
    private final Environment env;

    @Override
    public Iterable<MapEntity> getMapbyUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<WeatherInfoDto> getWeather(String userId) {
        Iterable<MapEntity> mapList = getMapbyUserId(userId);
        List<WeatherInfoDto> list = new ArrayList<>();

        mapList.forEach(t -> {
            MapDto map = new ModelMapper().map(t, MapDto.class);
            if ("S".equals(map.getMapType())) {
                // 날씨 조회
                try {
                    list.add(getWeatherAPI(map));
                } catch (ParseException e) {
                    e.toString();
                }
            } else if ("E".equals(map.getMapType())) {
                try {
                    list.add(getWeatherAPI(map));
                } catch (ParseException e) {
                    e.toString();
                }
            }

        });

        return list;
    }

    public WeatherInfoDto getWeatherAPI(MapDto map) throws ParseException {
        WeatherInfoDto dto = new WeatherInfoDto();
        // 오늘 날짜
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 단기예보 공공 API URL
        String url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
                + "?serviceKey=" + env.getProperty("api.weatherkey")
                + "&pageNo=1&numOfRows=500&dataType=JSON&base_time=0500"
                + "&base_date=" + formatDate
                + "&nx=" + map.getXlocation()
                + "&ny=" + map.getYlocation();

        RestTemplate restTemplate = new RestTemplate();
        String jsonString = restTemplate.getForObject(url, String.class);
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
        JSONArray jsonItemList = new JSONArray();

        if ((JSONObject) jsonObject.get("response") != null) {
            JSONObject jsonItems = (JSONObject) ((JSONObject) ((JSONObject) jsonObject.get("response")).get("body"))
                    .get("items");
            jsonItemList = (JSONArray) jsonItems.get("item");
        }

        for (Object o : jsonItemList) {
            JSONObject item = (JSONObject) o;
            // WeatherDto dto = new WeatherDto((JSONObject) o);
            // List로 8시, 18시 추가
            // 0번째는 출근길 온도는 10도, 날씨는 맑고
            // 1번째는 퇴근길 온도는 7도, 날씨는 비가 오며 강수확률 POP 강수량은 10mm(PCP)
            // REH 습도(여름) SNO 적설량(겨울)
            // 퇴근시 우산을 챙기세요
            if (formatDate.equals(item.get("fcstDate").toString())) {
                if ("S".equals(map.getMapType()) && "0800".equals(item.get("fcstTime").toString())) {
                    dto.mapper(item.get("fcstTime").toString(), item.get("category").toString(),
                            item.get("fcstValue").toString());
                } else if ("E".equals(map.getMapType()) && "1800".equals(item.get("fcstTime").toString())) {
                    dto.mapper(item.get("fcstTime").toString(), item.get("category").toString(),
                            item.get("fcstValue").toString());
                }
            }
        }

        dto.weatherMessage();

        return dto;
    }

    @Override
    public List<CarInfoDto> getCarTime(String userId) throws ParseException {
        Iterable<MapEntity> mapList = getMapbyUserId(userId);
        List<CarInfoDto> list = new ArrayList<>();
        List<MapDto> res = new ArrayList<>();

        mapList.forEach(t -> {
            MapDto map = new ModelMapper().map(t, MapDto.class);
            res.add(map);
        });

        try {
            list.add(getCarTimeAPI(res));
        } catch (ParseException e) {
            e.toString();
        }

        return list;
    }

    @Override
    public CarInfoDto getCarTimeAPI(List<MapDto> mapList) throws ParseException {
        CarInfoDto dto = new CarInfoDto();
        MapDto start = mapList.stream().filter(x -> "S".equals(x.getMapType())).findFirst().orElse(null);
        MapDto end = mapList.stream().filter(x -> "E".equals(x.getMapType())).findFirst().orElse(null);

        int duration = 0;
        double distance = 0;
        String message = "";
        // 카카오 내비 API
        // https://apis-navi.kakaomobility.com/v1/directions?
        // origin=126.885209391838,37.5156016724837&destination=127.028234154874,37.4945246017971
        // &waypoints=&priority=RECOMMEND&car_fuel=GASOLINE&car_hipass=false&alternatives=false&road_details=false
        if (start != null && end != null) {
            String url = "https://apis-navi.kakaomobility.com/v1/directions?"
                    // + "?serviceKey=" + env.getProperty("api.kakaorestkey")
                    + "priority=RECOMMEND&car_fuel=GASOLINE&car_hipass=false&alternatives=false&road_details=false"
                    + "&origin=" + start.getLon() + "," + start.getLat()
                    + "&destination=" + end.getLon() + "," + end.getLat();

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", env.getProperty("api.kakaorestkey"));

            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> jsonString = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            // String jsonString = restTemplate.getForObject(url, String.class);
            // 헤더 넣어야 함
            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString.getBody());
            JSONArray jsonItemList = (JSONArray) jsonObject.get("routes");

            if (jsonItemList != null && jsonItemList.size() > 0) {
                JSONObject jsonItem = (JSONObject) ((JSONObject) jsonItemList.get(0)).get("summary");
                duration = Math.round(Integer.parseInt(jsonItem.get("duration").toString()) / 60); // 초단위로 되어 있으므로 60초로
                                                                                                   // 나눈다
                distance = (double) (Math.round((Double.parseDouble(jsonItem.get("distance").toString()) / 1000) * 100))
                        / 100;
            }
        }

        dto.setDuration(duration);
        dto.setDistance(distance);
        message = start.getAddr() + "에서 " + end.getAddr() + "까지 자동차로 이동시 거리는 " + distance + "km, 소요시간은 약" + duration
                + "분 입니다.";
        dto.setMessage(message);

        return dto;
    }

}
