import axios from "axios";
import authHeader from "./auth-header";
import AuthService from "../services/auth.service";

const API_URL = "";

const getWeather = () => {
  const currentUser = AuthService.getCurrentUser();

  return axios.get(API_URL + "map/weather/" + currentUser.userId, { headers: authHeader() });
};

const getCar = () => {
  const currentUser = AuthService.getCurrentUser();

  return axios.get(API_URL + "map/car/" + currentUser.userId, { headers: authHeader() });
};

const UserService = {
  getWeather,
  getCar
};

export default UserService;
