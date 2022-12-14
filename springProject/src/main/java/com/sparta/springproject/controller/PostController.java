package com.sparta.springproject.controller;

import com.sparta.springproject.dto.*;
import com.sparta.springproject.service.PostService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @GetMapping("/post/{id}")
    public ResponseEntity<ResponseDto> findOnePost(@PathVariable Long id) {
        try {
            PostingDto postingDto = postService.findOnePost(id);
            return ResponseEntity.status(HttpStatus.OK).body(new PostingResponseDto(postingDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto("게시글이 존재하지 않습니다."));
        }
    }

    @GetMapping("/post")
    public ResponseEntity<ResponseDto> findAllPost() {
        try {
            List<PostingDto> postingListDto = postService.findAllPost();
            return ResponseEntity.status(HttpStatus.OK).body(new PostingListResponseDto(postingListDto));
        }catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorMessageDto("게시글들이 존재하지 않습니다."));
        }
    }


    @PostMapping("/post")
    public ResponseEntity<ResponseDto> resisterPost(@RequestBody PostingRequestDto postingRequestDto, HttpServletRequest request) {
        try {
            PostingDto postingDto = postService.registerPost(postingRequestDto, request);
            return ResponseEntity.status(HttpStatus.OK).body(new PostingResponseDto(postingDto));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessageDto("토큰이 유효하지 않습니다."));
        }
    }


    @PutMapping("/post/{id}")
    public ResponseEntity<ResponseDto> updatePost(@PathVariable Long id, @RequestBody PostingRequestDto postingRequestDto, HttpServletRequest request) {
        try {
            PostingDto postingDto = postService.updatePost(id, postingRequestDto, request);
            return ResponseEntity.status(HttpStatus.OK).body(new PostingResponseDto(postingDto));
        } catch (JwtException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessageDto("토큰이 유효하지 않습니다."));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto("게시글이 존재하지 않습니다."));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto("작성자만 수정 할 수 있습니다."));
        }

    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<ResponseDto> deletePost(@PathVariable Long id, HttpServletRequest request) {
        try {
            String result = postService.deletePost(id, request); // success
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(result));
        } catch (JwtException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessageDto("토큰이 유효하지 않습니다."));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto("게시글이 존재하지 않습니다."));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto("작성자만 삭제할 수 있습니다."));
        }
    }

}
