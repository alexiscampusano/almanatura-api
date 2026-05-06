package com.almanatura.api.config;

import java.util.List;
import java.util.Map;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final String PROBLEM_SCHEMA_REF = "#/components/schemas/ProblemDetail";

    @Bean
    public OpenAPI almanaturaOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("AlmaNatura API")
                                .description("REST API for the AlmaNatura rural actors and projects platform")
                                .version("v1")
                                .license(
                                        new License()
                                                .name("Private")
                                                .url("https://almanatura.org")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEME_NAME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                                .addSchemas("ProblemDetail", problemDetailSchema()));
    }

    /**
     * Adds default RFC 7807 error responses to every operation in the OpenAPI document. Each
     * operation only needs to declare its happy-path responses; the cross-cutting error envelope is
     * injected here so Swagger UI consistently advertises {@code application/problem+json} for the
     * standard failure modes.
     */
    @Bean
    public OperationCustomizer problemDetailResponsesCustomizer() {
        Map<String, String> defaults =
                Map.of(
                        "400", "Validation failed or malformed request",
                        "401", "Authentication is required or credentials are invalid",
                        "403", "Authenticated user lacks the required permissions",
                        "404", "Requested resource could not be found",
                        "429", "Rate limit exceeded",
                        "500", "Unexpected server error");

        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }
            for (Map.Entry<String, String> entry : defaults.entrySet()) {
                responses.computeIfAbsent(
                        entry.getKey(),
                        status ->
                                new ApiResponse()
                                        .description(entry.getValue())
                                        .content(problemContent()));
            }
            return operation;
        };
    }

    private static Content problemContent() {
        Schema<?> ref = new Schema<>().$ref(PROBLEM_SCHEMA_REF);
        return new Content()
                .addMediaType(
                        MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().schema(ref));
    }

    private static Schema<ProblemDetail> problemDetailSchema() {
        Schema<ProblemDetail> schema = new Schema<>();
        schema.type("object");
        schema.description("RFC 7807 problem details with AlmaNatura extensions");
        schema.required(List.of("status", "title", "code"));
        schema.addProperty("type", new StringSchema().format("uri"));
        schema.addProperty("title", new StringSchema().example("Validation failed"));
        schema.addProperty("status", new IntegerSchema().format("int32").example(400));
        schema.addProperty("detail", new StringSchema().example("One or more fields are invalid"));
        schema.addProperty(
                "instance", new StringSchema().format("uri").example("/api/v1/admin/projects"));
        schema.addProperty("code", new StringSchema().example("VALIDATION_FAILED"));
        schema.addProperty(
                "traceId", new StringSchema().example("65c2f4a1b8d3e7f9a0b1c2d3e4f5a6b7"));
        schema.addProperty("timestamp", new DateTimeSchema());
        schema.addProperty("violations", violationsSchema());
        return schema;
    }

    private static ArraySchema violationsSchema() {
        ObjectSchema item = new ObjectSchema();
        item.addProperty("field", new StringSchema());
        item.addProperty("message", new StringSchema());
        ArraySchema array = new ArraySchema();
        array.setItems(item);
        return array;
    }
}
