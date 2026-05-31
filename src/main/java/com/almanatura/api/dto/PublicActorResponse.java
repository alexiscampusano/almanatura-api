package com.almanatura.api.dto;

import java.util.List;

public record PublicActorResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String nationalId,
        List<ActorProjectInfo> projects) {

    public PublicActorResponse(Long id, String fullName) {
        this(id, fullName, null, null, null, List.of());
    }

    public record ActorProjectInfo(
            Long projectId, String projectTitle, String pillar, String applicationStatus) {}
}
