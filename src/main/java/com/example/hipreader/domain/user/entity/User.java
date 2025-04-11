package com.example.hipreader.domain.user.entity;

import com.example.hipreader.common.entity.TimeStamped;
import com.example.hipreader.domain.user.dto.request.UpdateUserRequestDto;
import com.example.hipreader.domain.user.gender.Gender;
import com.example.hipreader.domain.user.role.UserRole;

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

	@Column
	private String nickname;

	@Column
	private String email;

	@Column
	private String password;

	@Column
	private Integer age;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	public void updateProfile(String nickname, Integer age, Gender gender) {
		this.nickname = nickname != null ? nickname : this.nickname;
		this.age = age != null ? age : this.age;
		this.gender = gender != null ? gender : this.gender;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void deleteUser() {
		this.setDeletedAt();
	}
}
