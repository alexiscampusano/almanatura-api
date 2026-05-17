package com.almanatura.api.dto;

import java.util.List;

public record PublicActorResponse(
        Long id, String fullName, String region, List<ActorProjectInfo> projects) {

    public PublicActorResponse(Long id, String fullName, String region) {
        this(id, fullName, region, List.of());
    }

    public record ActorProjectInfo(
            Long projectId, String projectTitle, String pillar, String applicationStatus) {}
}
