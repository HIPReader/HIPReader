package com.example.hipreader.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.post.dto.request.SavePostRequestDto;
import com.example.hipreader.domain.post.dto.request.UpdatePostRequestDto;
import com.example.hipreader.domain.post.dto.response.GetPostResponseDto;
import com.example.hipreader.domain.post.dto.response.SavePostResponseDto;
import com.example.hipreader.domain.post.dto.response.UpdatePostResponseDto;
import com.example.hipreader.domain.post.entity.Post;
import com.example.hipreader.domain.post.repository.PostRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@Mock
	PostRepository postRepository;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	PostService postService;

	@Test
	void 게시글_생성_성공() {
		// given
		Long userId = 1L;
		Long postId = 1L;

		SavePostRequestDto requestDto = new SavePostRequestDto("타이틀", "콘텐트");

		User mockUser = User.builder()
			.id(userId)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Post savedPost = Post.builder()
			.id(postId)
			.user(mockUser)
			.title("타이틀")
			.content("콘텐트")
			.build();

		AuthUser authUser = new AuthUser(userId, mockUser.getEmail(), mockUser.getRole());

		when(userRepository.findUserById(any())).thenReturn(Optional.of(mockUser));
		when(postRepository.save(any())).thenReturn(savedPost);

		// when
		SavePostResponseDto responseDto = postService.savePost(requestDto, authUser);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getId()).isEqualTo(postId);
		assertThat(responseDto.getTitle()).isEqualTo("타이틀");
		assertThat(responseDto.getContent()).isEqualTo("콘텐트");
	}

	@Test
	void 게시글_다건_조회_성공() {
		// given
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

		User mockUser = User.builder()
			.id(1L)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Post post1 = Post.builder()
			.id(1L)
			.title("타이틀1")
			.content("콘텐트1")
			.user(mockUser)
			.build();

		Post post2 = Post.builder()
			.id(2L)
			.title("타이틀2")
			.content("콘텐트2")
			.user(mockUser)
			.build();

		List<Post> postList = List.of(post1, post2);
		PageImpl<Post> postPage = new PageImpl<>(postList, pageable, 2);

		when(postRepository.findAllPosts(pageable)).thenReturn(postPage);

		// when
		PageResponseDto<GetPostResponseDto> result = postService.getPosts(page, size);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("타이틀1");
		assertThat(result.getContent().get(1).getTitle()).isEqualTo("타이틀2");
		assertThat(result.getPageNumber()).isEqualTo(0);
		assertThat(result.getPageSize()).isEqualTo(10);
		assertThat(result.getTotalPages()).isEqualTo(1);
		assertThat(result.getTotalElements()).isEqualTo(2);
	}

	@Test
	void 게시글_단건_조회_성공() {
		// given
		Long postId = 1L;

		User mockUser = User.builder()
			.id(1L)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Post post = Post.builder()
			.id(postId)
			.title("타이틀")
			.content("콘텐트")
			.user(mockUser)
			.build();

		given(postRepository.findById(postId)).willReturn(Optional.of(post));

		// when
		GetPostResponseDto responseDto = postService.getPost(postId);

		// then
		assertThat(responseDto.getTitle()).isEqualTo("타이틀");
		assertThat(responseDto.getContent()).isEqualTo("콘텐트");
	}

	@Test
	void 게시글_수정_성공() {
		// given
		Long postId = 1L;
		Long userId = 1L;

		UpdatePostRequestDto requestDto = new UpdatePostRequestDto("타이틀", "콘텐트");

		User mockUser = User.builder()
			.id(1L)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Post post = Post.builder()
			.id(postId)
			.title("타이틀")
			.content("콘텐트")
			.user(mockUser)
			.build();

		AuthUser authUser = new AuthUser(userId, mockUser.getEmail(), mockUser.getRole());

		given(postRepository.findById(postId)).willReturn(Optional.of(post));

		// when
		UpdatePostResponseDto responseDto = postService.patchPost(postId, requestDto, authUser);

		// then
		assertThat(responseDto.getTitle()).isEqualTo("타이틀");
		assertThat(responseDto.getContent()).isEqualTo("콘텐트");
	}

	@Test
	void 게시글_삭제_성공() {
		// given
		Long postId = 1L;
		Long userId = 1L;

		User mockUser = User.builder()
			.id(1L)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Post post = Post.builder()
			.id(postId)
			.title("타이틀")
			.content("콘텐트")
			.user(mockUser)
			.build();

		AuthUser authUser = new AuthUser(userId, mockUser.getEmail(), mockUser.getRole());

		given(postRepository.findById(postId)).willReturn(Optional.of(post));

		// when
		postService.deletePost(postId, authUser);

		// then
		assertThat(post.isDeleted()).isTrue();
	}
}