package com.test.teamlog.controller;

import com.test.teamlog.entity.User;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.security.JwtUtil;
import com.test.teamlog.service.UserService;
import com.test.teamlog.util.CookieUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
//    private final RedisUtil redisUtil;

    // Read
    @PostMapping("sign-in")
    public ResponseEntity<ApiResponse> getUserById(@RequestBody UserDTO.SignInRequest userRequest,
                                           HttpServletRequest req,
                                           HttpServletResponse res) {
        User user = userService.signIn(userRequest);
        if (user != null) {
            String token = jwtUtil.generateToken(user);
            Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
            // TODO : 추후 Redis 세팅 후 refresh token 사용 고려
            res.addCookie(accessToken);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "로그인 성공"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "로그인 실패"), HttpStatus.NOT_FOUND);
        }
    }

    // Read
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.UserResponse> getUserById(@PathVariable("id") String id) {
        UserDTO.UserResponse userResponse = userService.getUser(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    // Create
    @PostMapping("")
    public ResponseEntity<UserDTO.UserResponse> signUp(@Valid @RequestBody UserDTO.UserRequest userRequest) {
        userService.signUp(userRequest);
        UserDTO.UserResponse userResponse = userService.getUser(userRequest.getId());
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable("id") String id, @Valid @RequestBody UserDTO.UserRequest userRequest) {
        ApiResponse apiResponse = userService.updateUser(id, userRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "프로필 이미지 변경")
    @PutMapping("/{id}/profile-image")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable("id") String id,
                                                  @RequestPart(value = "profileImg", required = false) MultipartFile image) {
        ApiResponse apiResponse = userService.updateUserProfileImage(id, image);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") String id) {
        ApiResponse apiResponse = userService.deleteUser(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}