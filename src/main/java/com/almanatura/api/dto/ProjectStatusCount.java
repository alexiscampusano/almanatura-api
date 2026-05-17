package com.almanatura.api.dto;

import com.almanatura.api.enums.ProjectStatus;

public record ProjectStatusCount(ProjectStatus status, long count) {}
