package com.example.hipreader.domain.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.post.dto.request.SavePostRequestDto;
import com.example.hipreader.domain.post.dto.request.UpdatePostRequestDto;
import com.example.hipreader.domain.post.dto.response.SavePostResponseDto;
import com.example.hipreader.domain.post.dto.response.UpdatePostResponseDto;
import com.example.hipreader.domain.post.dto.response.GetPostResponseDto;
import com.example.hipreader.domain.post.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	// 게시물 생성
	@PostMapping("/v1/posts")
	public ResponseEntity<SavePostResponseDto> savePost(@Valid @RequestBody SavePostRequestDto requestDto) {
		SavePostResponseDto savePostResponseDto = postService.savePost(requestDto);

		return new ResponseEntity<>(savePostResponseDto, HttpStatus.CREATED);
	}

	// 게시물 다건 조회
	@GetMapping("/v1/posts")
	public ResponseEntity<PageResponseDto<GetPostResponseDto>> findPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		PageResponseDto<GetPostResponseDto> posts = postService.getPosts(page, size);

		return new ResponseEntity<>(posts, HttpStatus.OK);
	}

	// 게시물 단건 조회
	@GetMapping("/v1/posts/{postId}")
	public ResponseEntity<GetPostResponseDto> findPost(@PathVariable Long postId) {
		GetPostResponseDto getPostResponseDto = postService.getPost(postId);

		return new ResponseEntity<>(getPostResponseDto, HttpStatus.OK);
	}

	// 게시물 수정
	@PatchMapping("/v1/posts/{postId}")
	public ResponseEntity<UpdatePostResponseDto> updatePost(
		@PathVariable Long postId,
		@RequestBody UpdatePostRequestDto requestDto
	) {
		UpdatePostResponseDto updatePostResponseDto = postService.updatePosts(postId, requestDto);

		return new ResponseEntity<>(updatePostResponseDto, HttpStatus.OK);
	}

	// 게시물 삭제
	@DeleteMapping("/v1/posts/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
