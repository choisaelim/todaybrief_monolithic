import React, {useEffect, useState} from "react";
import { useNavigate } from 'react-router-dom';
import AuthService from "../services/auth.service";
import UserService from "../services/user.service";

const Map = () => {
  let navigate = useNavigate();
  const currentUser = AuthService.getCurrentUser();

    if(currentUser === undefined || currentUser === null){
      navigate("/login");
    }
  const [sMessage, setSMessage] = useState("");
  const [eMessage, setEMessage] = useState("");
  const [carMsg, setCarMsg] = useState("");
  const { kakao } = window;
  useEffect(() => {
      var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
	    mapOption = {
	        center: new kakao.maps.LatLng(37.51190873398981, 126.9815408419237), // 지도의 중심좌표
	        level: 8, // 지도의 확대 레벨
	        mapTypeId : kakao.maps.MapTypeId.ROADMAP // 지도종류
	    }; 
      // 지도를 생성한다 
      var map = new kakao.maps.Map(mapContainer, mapOption); 

      //출발지 마커 이미지
      var startImagesrc = 'https://t1.daumcdn.net/localimg/localimages/07/2018/pc/flagImg/blue_b.png', // 마커이미지의 주소입니다    
      startImageSize = new kakao.maps.Size(37, 37), // 마커이미지의 크기입니다
      startImageOption = {offset: new kakao.maps.Point(27, 37)}; // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.
      //도착지 마커 이미지
      var endImagesrc =  'https://t1.daumcdn.net/localimg/localimages/07/2018/pc/flagImg/red_b.png', 
      endImageSize = new kakao.maps.Size(37, 37), 
      endImageOption = {offset: new kakao.maps.Point(27, 37)}; 

      // 마커의 이미지정보를 가지고 있는 마커이미지를 생성합니다
      var startMarkerImage = new kakao.maps.MarkerImage(startImagesrc, startImageSize, startImageOption);
      var endMarkerImage = new kakao.maps.MarkerImage(endImagesrc, endImageSize, endImageOption);

      // 지도에 마커를 생성하고 표시한다
      var startmarker = new kakao.maps.Marker({
          position: new kakao.maps.LatLng(37.5156016724837, 126.885209391838), // 마커의 좌표
          draggable : true, // 마커를 드래그 가능하도록 설정한다
          image: startMarkerImage,
          map: map // 마커를 표시할 지도 객체
      });
        
      var endmarker = new kakao.maps.Marker({
          position: new kakao.maps.LatLng(37.4945246017971, 127.028234154874), // 마커의 좌표
          draggable : true, // 마커를 드래그 가능하도록 설정한다
          image: endMarkerImage,
          map: map // 마커를 표시할 지도 객체
      });


      UserService.getWeather().then(
        (response) => {
          // setContent(response.data);
          setSMessage(response.data[0].message);
          setEMessage(response.data[1].message);
          // console.log(response);
        },
        (error) => {
          const _content =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();
        }
      );

      UserService.getCar().then(
        (response) => {
          setCarMsg(response.data[0].message);
        },
        (error) => {
          const _content =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();
        }
      );
  }, []);
  return ( <>
      <div id='map' style={{
          width: '70%',
          float: 'left',
          height: '500px'
      }}/>
      <div id='weather' style={{
        width: '30%',
        float: 'right',
        padding: '10px',
        height: '500px'
    }}>
      <p>{sMessage}</p>
      <div/>
      <p>{eMessage}</p>
      <div/>
      <p>{carMsg}</p>
    </div>
    </>
  ); 
  
};

export default Map;
