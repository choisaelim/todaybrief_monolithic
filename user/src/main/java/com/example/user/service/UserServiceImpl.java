package com.example.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user.dto.ResponseOrder;
import com.example.user.dto.UserDto;
import com.example.user.jpa.RoleRepository;
import com.example.user.jpa.UserEntity;
import com.example.user.jpa.UserRepository;

import lombok.RequiredArgsConstructor;
import com.example.user.dto.ERole;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    final BCryptPasswordEncoder passEncoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Override
    public UserDto createUser(UserDto userDto) throws Exception {
        // userDto.setUserId(UUID.randomUUID().toString());
        // 중복확인(by userId)
        if (userRepository.findByUserId(userDto.getUserId()) != null) {
            throw new Exception("이미 가입한 회원입니다.");
        }

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // client 단에서 받은 userdto를 entity 클래스로 변환
        UserEntity entity = mapper.map(userDto, UserEntity.class);
        entity.setEncryptedPwd(passEncoder.encode(userDto.getPassword()));

        // 기본 Role USER로 지정
        if (entity.getRoles().size() == 0) {
            entity.getRoles().addAll(roleRepository.findByName(ERole.ROLE_USER));
        }
        userRepository.save(entity);

        UserDto userVo = mapper.map(entity, UserDto.class);
        return userVo;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity entity = userRepository.findByUserId(userId);

        if (entity == null)
            throw new UsernameNotFoundException("user id not found");

        UserDto userDto = new ModelMapper().map(entity, UserDto.class);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUserId(userId);

        if (entity == null)
            throw new UsernameNotFoundException(userId);

        // ModelMapper mapper = new ModelMapper();

        // mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // UserDto userDto = mapper.map(entity, UserDto.class);

        // UserDetails userDetails = UserDetails.builder()
        // .username(entity.getEmail())
        // .password(entity.getEncryptedPwd())
        // .roles("USER")
        // .build();
        return UserDto.build(entity);

    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email);

        if (entity == null)
            throw new UsernameNotFoundException("user id not found");

        UserDto userDto = new ModelMapper().map(entity, UserDto.class);
        return userDto;
    }

}
