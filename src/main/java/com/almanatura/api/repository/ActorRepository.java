package com.almanatura.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.Actor;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    boolean existsByFullNameIgnoreCase(String fullName);
}
