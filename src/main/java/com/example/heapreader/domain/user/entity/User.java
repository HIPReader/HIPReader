package com.example.heapreader.domain.user.entity;

import com.example.heapreader.common.entity.TimeStamped;
import com.example.heapreader.domain.user.gender.Gender;
import com.example.heapreader.domain.user.role.UserRole;

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

	@Column(unique = true)
	private String nickname;
	private String email;
	private String password;
	private Integer age;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	public void changePassword(String password) {
		this.password = password;
	}

	public void deleteUser() {
		this.setDeletedAt();
	}
}
