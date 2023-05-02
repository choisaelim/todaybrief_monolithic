package com.example.user.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.user.dto.CarInfoDto;
import com.example.user.dto.ResponseMap;
import com.example.user.dto.WeatherInfoDto;
import com.example.user.jpa.MapEntity;
import com.example.user.service.MapService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;
    private final Environment env;

    static class map {
        static double Re = 6371.00877; // 지도반경
        static double grid = 5.0; // 격자간격 (km)
        static double slat1 = 30.0; // 표준위도 1
        static double slat2 = 60.0; // 표준위도 2
        static double olon = 126.0; // 기준점 경도
        static double olat = 38.0; // 기준점 위도
        static double xo = 210 / grid; // 기준점 X좌표
        static double yo = 675 / grid; // 기준점 Y좌표
        static double first = 0;
    }

    public void mapper(double lon, double lat) {
        double x = 0;
        double y = 0;

        double PI = Math.asin(1.0) * 2.0;
        double DEGRAD = PI / 180.0;
        double RADDEG = 180.0 / PI;

        double re = map.Re / map.grid;
        double slat1 = map.slat1 * DEGRAD;
        double slat2 = map.slat2 * DEGRAD;
        double olon = map.olon * DEGRAD;
        double olat = map.olat * DEGRAD;

        double sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        map.first = 1;

        double ra = Math.tan(PI * 0.25 + (lat) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = (lon) * DEGRAD - olon;
        if (theta > PI)
            theta -= 2.0 * PI;
        if (theta < -PI)
            theta += 2.0 * PI;
        theta *= sn;
        x = (float) (ra * Math.sin(theta)) + map.xo;
        y = (float) (ro - ra * Math.cos(theta)) + map.yo;

        System.out.println("x : " + String.valueOf(Math.floor(x + 1.5)) + " y : "
                + String.valueOf(Math.floor(y + 1.5)));
    }

    @GetMapping("/weather/{userId}")
    public ResponseEntity<List<WeatherInfoDto>> getWeather(@PathVariable("userId") String userId)
            throws ParseException {

        List<WeatherInfoDto> list = mapService.getWeather(userId);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/car/{userId}")
    public ResponseEntity<List<CarInfoDto>> getCar(@PathVariable("userId") String userId) throws ParseException {

        List<CarInfoDto> list = mapService.getCarTime(userId);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/map/{userId}")
    public ResponseEntity<List<ResponseMap>> getMapdata(@PathVariable("userId") String userId) {

        Iterable<MapEntity> mapList = mapService.getMapbyUserId(userId);
        List<ResponseMap> list = new ArrayList<>();
        // 59, 126
        // 61, 125

        // mapper(126.929810, 37.488201);
        mapper(126.8852269678076, 37.51557330796054);

        mapList.forEach(t -> {
            list.add(new ModelMapper().map(t, ResponseMap.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
