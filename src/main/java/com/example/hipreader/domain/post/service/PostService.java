package com.example.hipreader.domain.post.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.post.dto.request.PostSaveRequestDto;
import com.example.hipreader.domain.post.dto.request.PostUpdateRequestDto;
import com.example.hipreader.domain.post.dto.response.PostSaveResponseDto;
import com.example.hipreader.domain.post.dto.response.PostUpdateResponseDto;
import com.example.hipreader.domain.post.dto.response.PostGetResponseDto;
import com.example.hipreader.domain.post.entity.Post;
import com.example.hipreader.domain.post.repository.PostRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;

	// 게시물 생성
	@Transactional
	public PostSaveResponseDto savePost(PostSaveRequestDto requestDto) {
		Long userId = 1L;

		User user = userRepository.findUserById(userId)
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

		Post post = Post.builder()
			.user(user)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.build();

		Post savedPost = postRepository.save(post);

		return PostSaveResponseDto.toDto(savedPost);
	}

	// 게시물 다건 조회
	@Transactional(readOnly = true)
	public PageResponseDto<PostGetResponseDto> getPosts(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
		Page<Post> postPage = postRepository.findAllPosts(pageRequest);

		List<PostGetResponseDto> content = postPage.getContent().stream()
			.map(PostGetResponseDto::toDto).toList();

		return PageResponseDto.<PostGetResponseDto>builder()
			.pageNumber(postPage.getNumber())
			.pageSize(postPage.getSize())
			.totalPages(postPage.getTotalPages())
			.totalElements(postPage.getTotalElements())
			.content(content)
			.build();
	}

	// 게시물 단건 조회
	public PostGetResponseDto getPost(Long postId) {
		Post findPost = findPostByIdOrElseThrow(postId);

		return PostGetResponseDto.toDto(findPost);
	}

	// 게시물 수정
	@Transactional
	public PostUpdateResponseDto updatePosts(Long postId, PostUpdateRequestDto requestDto) {
		// 작성자가 맞는지 확인

		// 게시물 수정
		Post findPost = findPostByIdOrElseThrow(postId);

		findPost.updateTitleIfNotNull(requestDto.getTitle());
		findPost.updateContentIfNotNull(requestDto.getContent());

		return PostUpdateResponseDto.toDto(findPost);
	}

	// 게시물 삭제
	@Transactional
	public void deletePost(Long postId) {
		// 작성자가 맞는지 확인

		Post findPost = findPostByIdOrElseThrow(postId);
		findPost.setDeletedAt();
	}

	public Post findPostByIdOrElseThrow(Long postId) {
		return postRepository.findById(postId).orElseThrow(
			() -> new ResponseStatusException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage())
		);
	}
}
