package com.example.hipreader.domain.user.entity;

import com.example.hipreader.common.entity.TimeStamped;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends TimeStamped {
	//닉네임, 이메일, 비밀번호, 권한 ,성별 ,나이

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private Integer age;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	public void patchProfile(String nickname, Integer age, Gender gender) {
		if (nickname != null) this.nickname = nickname;
		if (age != null) this.age = age;
		if (gender != null) this.gender = gender;
	}

	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void deleteUser() {
		this.setDeletedAt();
	}
}
