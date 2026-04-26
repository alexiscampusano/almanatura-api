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
 *   <li>One mapper per aggregate (e.g. {@code UserMapper}, {@code EventMapper}).
 *   <li>Mappers must not depend on services or repositories — only DTO ↔ Entity translation.
 *   <li>Sensitive fields (e.g. password hashes, encrypted DNI) must be {@code @Mapping(ignore =
 *       true)} in the entity → DTO direction.
 * </ul>
 */
package com.almanatura.api.mapper;
