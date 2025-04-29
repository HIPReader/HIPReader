package com.example.hipreader.domain.discussion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hipreader.domain.discussion.entity.Discussion;

import jakarta.persistence.LockModeType;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from Discussion d where d.id = :id")
	Optional<Discussion> findByIdWithPessimisticLock(@Param("id") Long id);
}
