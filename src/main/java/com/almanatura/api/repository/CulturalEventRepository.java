package com.almanatura.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.CulturalEvent;

public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {

    List<CulturalEvent> findAllByOrderByStartsAtAsc();
}
