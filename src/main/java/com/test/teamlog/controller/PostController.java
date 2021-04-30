package com.test.teamlog.controller;

import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PostDTO;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.service.PostService;
import com.test.teamlog.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @ApiOperation(value = "단일 포스트 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.PostResponse> getProjectById(@PathVariable("id") long id) {
        PostDTO.PostResponse response = postService.getPost(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "포스트 생성")
    @PostMapping
    public ResponseEntity<ApiResponse> createProject(@RequestPart(value="key", required=true) PostDTO.PostRequest request,
                                                     @RequestPart(value="files", required=false) MultipartFile[] files) {
        ApiResponse apiResponse = postService.createPost(request,files);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "포스트 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProject(@PathVariable("id") long id,
                                                              @RequestBody PostDTO.PostRequest request) {
        ApiResponse apiResponse = postService.updatePost(id, request);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "포스트 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("id") Long id) {
        ApiResponse apiResponse = postService.deletePost(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}