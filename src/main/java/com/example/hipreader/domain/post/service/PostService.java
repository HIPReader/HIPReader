package com.example.hipreader.domain.post.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.domain.post.dto.request.SavePostRequestDto;
import com.example.hipreader.domain.post.dto.request.UpdatePostRequestDto;
import com.example.hipreader.domain.post.dto.response.SavePostResponseDto;
import com.example.hipreader.domain.post.dto.response.UpdatePostResponseDto;
import com.example.hipreader.domain.post.dto.response.GetPostResponseDto;
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
	public SavePostResponseDto savePost(SavePostRequestDto requestDto, AuthUser authUser) {
		User user = userRepository.findUserById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		Post post = Post.builder()
			.user(user)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.build();

		Post savedPost = postRepository.save(post);

		return SavePostResponseDto.toDto(savedPost);
	}

	// 게시물 다건 조회
	@Transactional(readOnly = true)
	public PageResponseDto<GetPostResponseDto> getPosts(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
		Page<Post> postPage = postRepository.findAllPosts(pageRequest);

		List<GetPostResponseDto> content = postPage.getContent().stream()
			.map(GetPostResponseDto::toDto).toList();

		return PageResponseDto.<GetPostResponseDto>builder()
			.pageNumber(postPage.getNumber())
			.pageSize(postPage.getSize())
			.totalPages(postPage.getTotalPages())
			.totalElements(postPage.getTotalElements())
			.content(content)
			.build();
	}

	// 게시물 단건 조회
	public GetPostResponseDto getPost(Long postId) {
		Post findPost = findPostByIdOrElseThrow(postId);

		return GetPostResponseDto.toDto(findPost);
	}

	// 게시물 수정
	@Transactional
	public UpdatePostResponseDto patchPost(Long postId, UpdatePostRequestDto requestDto, AuthUser authUser) {
		Post findPost = findPostByIdOrElseThrow(postId);

		// 작성자가 맞는지 확인
		if (!Objects.equals(findPost.getUser().getId(), authUser.getId())) {
			throw new UnauthorizedException(POST_UNAUTHORIZED);
		}

		// 게시물 수정
		findPost.updateTitleIfNotNull(requestDto.getTitle());
		findPost.updateContentIfNotNull(requestDto.getContent());

		return UpdatePostResponseDto.toDto(findPost);
	}

	// 게시물 삭제
	@Transactional
	public void deletePost(Long postId, AuthUser authUser) {
		Post findPost = findPostByIdOrElseThrow(postId);

		// 작성자가 맞는지 확인
		if (!Objects.equals(findPost.getUser().getId(), authUser.getId())) {
			throw new UnauthorizedException(POST_UNAUTHORIZED);
		}

		findPost.setDeletedAt();
	}

	public Post findPostByIdOrElseThrow(Long postId) {
		return postRepository.findById(postId).orElseThrow(
			() -> new NotFoundException(POST_NOT_FOUND)
		);
	}
}
