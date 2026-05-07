package com.almanatura.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.ActivityParticipation;

public interface ActivityParticipationRepository
        extends JpaRepository<ActivityParticipation, Long> {

    boolean existsByActivity_IdAndActor_Id(long activityId, long actorId);

    Optional<ActivityParticipation> findByIdAndActivity_Id(long id, long activityId);

    List<ActivityParticipation> findByActivity_IdOrderByIdAsc(long activityId);
}
