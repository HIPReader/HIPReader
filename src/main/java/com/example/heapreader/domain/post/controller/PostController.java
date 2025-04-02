package com.example.heapreader.domain.post.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import com.example.heapreader.common.dto.response.PageResponseDto;
import com.example.heapreader.domain.post.dto.request.PostSaveRequestDto;
import com.example.heapreader.domain.post.dto.request.PostUpdateRequestDto;
import com.example.heapreader.domain.post.dto.response.PostSaveResponseDto;
import com.example.heapreader.domain.post.dto.response.PostUpdateResponseDto;
import com.example.heapreader.domain.post.dto.response.PostGetResponseDto;
import com.example.heapreader.domain.post.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	// 게시물 생성
	@PostMapping("/v1/posts")
	public ResponseEntity<PostSaveResponseDto> savePost(@Valid @RequestBody PostSaveRequestDto requestDto) {
		PostSaveResponseDto responseDto = postService.savePost(requestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// 게시물 다건 조회
	@GetMapping("/v1/posts")
	public ResponseEntity<PageResponseDto<PostGetResponseDto>> findPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		PageResponseDto<PostGetResponseDto> posts = postService.getPosts(page, size);
		return new ResponseEntity<>(posts, HttpStatus.OK);
	}

	// 게시물 단건 조회
	@GetMapping("/v1/posts/{postId}")
	public ResponseEntity<PostGetResponseDto> findPost(@PathVariable Long postId) {
		PostGetResponseDto responseDto = postService.getPost(postId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	// 게시물 수정
	@PatchMapping("/v1/posts/{postId}")
	public ResponseEntity<PostUpdateResponseDto> updatePost(
		@PathVariable Long postId,
		@RequestBody PostUpdateRequestDto requestDto
	) {
		PostUpdateResponseDto responseDto = postService.updatePosts(postId, requestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	// 게시물 삭제
	@DeleteMapping("/v1/posts/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
