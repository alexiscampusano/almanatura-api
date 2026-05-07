/**
 * MapStruct mappers between JPA entities and DTOs.
 *
 * <p>All mappers in this package are generated at compile-time by the MapStruct annotation
 * processor (configured in {@code pom.xml}) using {@code componentModel = "spring"}. They therefore
 * become Spring beans available for dependency injection.
 *
 * <p>Conventions:
 *
 * <ul>
 *   <li>One mapper interface per aggregate where practical: {@link
 *       com.almanatura.api.mapper.ProjectMapper} for {@code Project}; {@link
 *       com.almanatura.api.mapper.ProjectActivityMapper} also covers planning/follow-up DTOs
 *       (activities, activity participation responses, outbound notification rows, impact entries)
 *       to avoid a large set of tiny interfaces.
 *   <li>Mappers must not depend on services or repositories — only DTO ↔ Entity translation.
 *   <li>Sensitive fields (e.g. password hashes, encrypted DNI) must be {@code @Mapping(ignore =
 *       true)} in the entity → DTO direction.
 * </ul>
 */
package com.almanatura.api.mapper;
