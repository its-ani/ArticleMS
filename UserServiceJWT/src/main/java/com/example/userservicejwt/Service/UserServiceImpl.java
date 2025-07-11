package com.example.userservicejwt.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.userservicejwt.DTO.*;
import com.example.userservicejwt.Exceptions.InvalidOrExpiredOTPException;
import com.example.userservicejwt.Exceptions.UserAlreadyExistsException;
import com.example.userservicejwt.Exceptions.UserNotFoundException;
import com.example.userservicejwt.Projections.UserProjection;
import com.example.userservicejwt.models.Role;
import com.example.userservicejwt.models.Token;
import com.example.userservicejwt.models.TokenType;
import com.example.userservicejwt.models.User;
import com.example.userservicejwt.repositories.RoleRepository;
import com.example.userservicejwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JWTService jwtService;
    private OtpService otpService;
    private KafkaService kafkaService;
    private RedisService redisService;

    AuthenticationManager authenticationManager;
    @Autowired
    public UserServiceImpl(UserRepository userRepository , RoleRepository roleRepository , BCryptPasswordEncoder bCryptPasswordEncoder , JWTService jwtService , OtpService otpService , RedisService redisService , AuthenticationManager authenticationManager , KafkaService kafkaService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.redisService = redisService;
        this.authenticationManager = authenticationManager;
        this.kafkaService = kafkaService;
    }

    @Override
    public ResponseEntity<?> registerUser(UserRegisterDTO userRegisterDTO){
        String email = userRegisterDTO.getEmail().toLowerCase();

        if(userRepository.findByEmail(email).isPresent()){
            throw new UserAlreadyExistsException();
        }

        String otp = otpService.generateAndStoreOtp(userRegisterDTO);
    String subject = "OTP Verification";
        String body = "This is your OTP for verifying your email:\n" + otp + "\nThis OTP is valid for 5 minutes. \n\n Do not share this OTP with anyone. Kindly ignore this email if you have not requested this OTP.";
        String message = email + " /BREAK/ " + subject + " /BREAK/ " + body;

        kafkaService.sendEmail(message);

        return ResponseEntity.ok("OTP sent to email");
    }

    @Override
    public ResponseEntity<?> registerUserComp(UserRegisterVerifyDTO user) {
        String email = user.getEmail().toLowerCase();
        String OTP = user.getOtp();

        RegisterOtpCacheDTO userDetails = otpService.verifyOtp(email, OTP);

        if(userDetails == null){
            throw new InvalidOrExpiredOTPException();
        }

        String password = userDetails.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        List<Role> roles = userDetails.getRoles().stream()
                .map(roleName -> roleRepository.findByValue(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toList());

        User newUser = new User(userDetails.getName() , email, encodedPassword, roles);
        userRepository.save(newUser);

        String subject = "Registration Successful.";
        String body = "Welcome " + userDetails.getName()  + ", to Career Connect. \n\n" + "We're happy to inform you that your registration was successful.\n\n" + "You can now log in and start using your account.\n\n" + "If this wasn't you, please ignore this email or get in touch with our support team.\n\n" + "Warm regards,\n" + "The Career Connect Team";
        String msg = email + " /BREAK/ " + subject + " /BREAK/ " + body;
        kafkaService.sendEmail(msg);

        return new ResponseEntity<>(UserProjection.makeProjection(newUser), HttpStatus.OK);
    }


    public ResponseEntity<?> login(LoginDTO user){
        String email = user.getEmail();
        email.toLowerCase();
        String password = user.getPassword();
        Optional<User> res = userRepository.findByEmail(email);
        if(res == null) throw new UserNotFoundException();

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        if(auth.isAuthenticated()){
            String jwtToken = jwtService.generateToken(email);

            Token token = Token.builder()
//                    .user(res.get())
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();

            List<?> rawList = redisService.get("TOKEN_" + email, List.class);
            List<Token> tokens = new ArrayList<>();

            if (rawList != null) {
                ObjectMapper mapper = new ObjectMapper();
                tokens = rawList.stream()
                        .map(item -> mapper.convertValue(item, Token.class))
                        .collect(Collectors.toList());
            }

            tokens.add(token);

            redisService.remove("TOKEN_" + email);
            redisService.set("TOKEN_" + user.getEmail() , tokens , 60L , TimeUnit.MINUTES);

            revokeUserTokens(res.get());

            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("BAD CREDENTIALS", HttpStatus.UNAUTHORIZED);
        }
    }


    private void revokeUserTokens(User user){

        List<?> rawList = redisService.get("TOKEN_" + user.getEmail(), List.class);
        List<Token> validUserTokens = new ArrayList<>();

        if (rawList != null) {
            ObjectMapper mapper = new ObjectMapper();
            validUserTokens = rawList.stream()
                    .map(item -> mapper.convertValue(item, Token.class))
                    .collect(Collectors.toList());
        }

        if(validUserTokens.isEmpty()){
            return;
        }

        for (int i = 0; i < validUserTokens.size() - 3; i++) {
            Token token = validUserTokens.get(i);
            token.setExpired(true);
            token.setRevoked(true);
        }

        if(validUserTokens.size() > 3) validUserTokens.subList(0, validUserTokens.size() - 3).clear();

        redisService.remove("TOKEN_" + user.getEmail());
        redisService.set("TOKEN_" + user.getEmail() , validUserTokens , 60L , TimeUnit.MINUTES);
    }


    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<User>> getAllActiveUsers(){
        List<User> userList = userRepository.findAllActive();
        if (userList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> addRole(String role) {
        if(role.isEmpty()){
            return new ResponseEntity<>("Role name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        role = role.toUpperCase();
        if(roleRepository.findByValue(role).isPresent()){
            return new ResponseEntity<>("Role already exists" , HttpStatus.CONFLICT);
        }

        Role newRole = new Role();
        newRole.setValue(role);

        Role check = roleRepository.save(newRole);
        if(check == null){
            return new ResponseEntity<>("Cannot add role", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Role added" , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserProjection> getUser(String email) {
        if(email == "") return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        UserProjection user = redisService.get("user:" + email , UserProjection.class);

        if(user != null){
            System.out.println("val : " + user);
            return new ResponseEntity<>(user , HttpStatus.OK);
        }

        Optional<User> res = userRepository.findByEmail(email);
        if (!res.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            UserProjection result = new UserProjection();
            result = result.makeProjection(res.get());
            redisService.set("user:" + email , result , 100L , TimeUnit.MINUTES);
            return new ResponseEntity<>(result , HttpStatus.OK);
        }
    }
}