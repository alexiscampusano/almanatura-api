package com.almanatura.api.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import jakarta.persistence.Entity;

import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

/**
 * Layer guardrails. These tests fail the build if anyone bypasses the architecture (e.g. a
 * controller injecting a repository directly, or JPA leaking out of {@code entity}).
 */
@AnalyzeClasses(
        packages = "com.almanatura.api",
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

    @ArchTest
    static final ArchRule layered_architecture_is_respected =
            Architectures.layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Controllers")
                    .definedBy("com.almanatura.api.controller..")
                    .layer("Services")
                    .definedBy("com.almanatura.api.service..")
                    .layer("Repositories")
                    .definedBy("com.almanatura.api.repository..")
                    .layer("Security")
                    .definedBy("com.almanatura.api.security..")
                    .layer("Bootstrap")
                    .definedBy("com.almanatura.api.bootstrap..")
                    .whereLayer("Controllers")
                    .mayNotBeAccessedByAnyLayer()
                    .whereLayer("Services")
                    .mayOnlyBeAccessedByLayers("Controllers", "Services", "Bootstrap")
                    .whereLayer("Repositories")
                    .mayOnlyBeAccessedByLayers("Services", "Repositories", "Security", "Bootstrap");

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_repositories =
            noClasses()
                    .that()
                    .resideInAPackage("..controller..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..repository..");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_controllers_or_dtos =
            noClasses()
                    .that()
                    .resideInAPackage("..entity..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..controller..", "..dto..");

    @ArchTest
    static final ArchRule jpa_entities_only_in_entity_package =
            classes().that().areAnnotatedWith(Entity.class).should().resideInAPackage("..entity..");

    @ArchTest
    static final ArchRule rest_controllers_must_live_in_controller_package =
            classes()
                    .that()
                    .areAnnotatedWith(RestController.class)
                    .should()
                    .resideInAPackage("..controller..")
                    .andShould()
                    .haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule services_should_be_suffixed_correctly =
            classes()
                    .that()
                    .resideInAPackage("..service..")
                    .and()
                    .areAnnotatedWith(org.springframework.stereotype.Service.class)
                    .should()
                    .haveSimpleNameEndingWith("Service");

    @ArchTest
    static final ArchRule repositories_should_be_interfaces =
            classes().that().resideInAPackage("..repository..").should().beInterfaces();
}
