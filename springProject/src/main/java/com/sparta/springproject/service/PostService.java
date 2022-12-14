package com.sparta.springproject.service;

import com.sparta.springproject.dto.*;
import com.sparta.springproject.entity.Comment;
import com.sparta.springproject.entity.Posting;
import com.sparta.springproject.entity.UserRoleEnum;
import com.sparta.springproject.jwt.JwtUtil;
import com.sparta.springproject.repository.CommentRepository;
import com.sparta.springproject.repository.PostingRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostingRepository postingRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    // 하나의 게시글 반환
    @Transactional
    public PostingDto findOnePost(Long id) {
        Posting posting = postingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        List<CommentDto> comments = new ArrayList<>();

        for (int i = 0; i < posting.getComments().size(); i++) {
            Comment comment = posting.getComments().get(i);
            comments.add(new CommentDto(comment));
        }
        return new PostingDto(posting, comments);
    }

    // 모든 게시글 반환
    @Transactional
    public List<PostingDto> findAllPost() {
        List<Posting> postings = postingRepository.findAllByOrderByModifiedAtDesc();

        if (postings.isEmpty()) {
            throw new NullPointerException("게시글들 없음");
        }

        List<PostingDto> result = new ArrayList<>();

        for (Posting p : postings) {
            result.add(new PostingDto(p, null));
        }

        return result;
    }

    // 게시글 등록
    @Transactional
    public PostingDto registerPost(PostingRequestDto postingRequestDto, HttpServletRequest request) throws JwtException{

        UserDto user = jwtUtil.userCheck(request);

        String username = user.getUsername();

        Posting posting = new Posting(postingRequestDto, username);
        postingRepository.save(posting);
        return new PostingDto(posting, null);

    }



    //게시글 수정
    @Transactional
    public PostingDto updatePost(Long id, PostingRequestDto postingDto, HttpServletRequest request) throws JwtException{

        UserDto user = jwtUtil.userCheck(request);
        String username = user.getUsername();
        UserRoleEnum role = user.getRole();

        String title = postingDto.getTitle();
        String content = postingDto.getContent();

        Posting posting = postingRepository.findById(id).orElseThrow(() -> new NullPointerException("게시글 없음"));

        if (posting.getUsername().equals(username) || role.equals(UserRoleEnum.ADMIN)) {

            posting.update(title, content, username);

            List<CommentDto> comments = new ArrayList<>();

            for (int i = 0; i < posting.getComments().size(); i++) {
                Comment comment = posting.getComments().get(i);
                comments.add(new CommentDto(comment));
            }

            return new PostingDto(posting, comments);
        }
        throw new IllegalArgumentException("유저 불일치");
    }

    // 게시글 삭제
    @Transactional
    public String deletePost(Long id, HttpServletRequest request) throws JwtException{
        UserDto user = jwtUtil.userCheck(request);
        String username = user.getUsername();
        UserRoleEnum role = user.getRole();

        Posting posting = postingRepository.findById(id).orElseThrow(() -> new NullPointerException("게시글 없음"));

        if (posting.getUsername().equals(username) || role.equals(UserRoleEnum.ADMIN)) {
            postingRepository.deleteById(id);

            return "success";
        }
        throw new IllegalArgumentException("유저 불일치");
    }
}
