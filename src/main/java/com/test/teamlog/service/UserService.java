package com.test.teamlog.service;

import com.test.teamlog.entity.User;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserDTO.UserResponse getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));
        return new UserDTO.UserResponse(user);
    }

    // 로그인
    public User signIn(UserDTO.SignInRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("USER", "ID", request.getId()));
        if (request.getPassword().equals(user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    // 회원 가입
    @Transactional
    public void signUp(UserDTO.UserRequest userRequest) {
        validateDuplicateuId(userRequest.getId());
        User user = User.builder()
                .id(userRequest.getId())
                .password(userRequest.getPassword())
                .name(userRequest.getName())
                .introduction(userRequest.getIntroduction())
                .profileImgPath(userRequest.getProfileImgPath())
                .build();
        userRepository.save(user);
    }

    // id 중복 체크
    private void validateDuplicateuId(String id) {
        if (userRepository.findById(id).isPresent()) {
            // TODO : 다른 exception 만들어서 처리
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    public ApiResponse updateUser(UserDTO.UserRequest userRequest, User currentUser) {
        currentUser.setName(userRequest.getName());
        currentUser.setIntroduction(userRequest.getIntroduction());
        userRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "프로필 수정 성공");
    }

    @Transactional
    public ApiResponse updateUserProfileImage(MultipartFile image, User currentUser) {
        if (currentUser.getProfileImgPath() != null) {
            fileStorageService.deleteFile(currentUser.getProfileImgPath());
            currentUser.setProfileImgPath(null);
        }
        String profileImgPath = fileStorageService.storeFile(image, null, null);
        currentUser.setProfileImgPath(profileImgPath);
        userRepository.save(currentUser);
        return new ApiResponse(Boolean.TRUE, "프로필 이미지 수정 성공");
    }

    //회원 탈퇴
    @Transactional
    public ApiResponse deleteUser(User currentUser) {
        // TODO : 허가된 사용자인지 검증해야함..
        userRepository.delete(currentUser);
        return new ApiResponse(Boolean.TRUE, "회원 탈퇴 성공");
    }
}
