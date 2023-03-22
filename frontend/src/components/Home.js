import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import AuthService from "../services/auth.service";
import UserService from "../services/user.service";

const Home = () => {
  const [content, setContent] = useState("");
  let navigate = useNavigate();

  useEffect(() => {
    const currentUser = AuthService.getCurrentUser();
    if(currentUser !== undefined && currentUser !== null){
      navigate("/map");
    }else{
      //login
      navigate("/login");
    }
  }, []);

  return (
    <div className="container">
      <header className="jumbotron">
        <h3>{content}</h3>
      </header>
    </div>
  );
};

export default Home;
