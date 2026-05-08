package com.almanatura.api.bootstrap;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.entity.Actor;
import com.almanatura.api.repository.ActorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class ActorBootstrapRunner implements ApplicationRunner {

    private static final List<ActorSeed> SEEDS =
            List.of(
                    new ActorSeed("María García López", "Sierra de Aracena (Huelva)"),
                    new ActorSeed(
                            "Antonio Fernández Ruiz", "Villanueva de los Castillejos (Huelva)"),
                    new ActorSeed("Carmen Martínez Delgado", "Almendralejo (Badajoz)"),
                    new ActorSeed("José Luis Moreno Vega", "Cazalla de la Sierra (Sevilla)"),
                    new ActorSeed("Ana Belén Rodríguez Pinto", "Aracena (Huelva)"),
                    new ActorSeed("Francisco Javier Torres Ramos", "Cortegana (Huelva)"),
                    new ActorSeed("Isabel Navarro Campos", "Jabugo (Huelva)"),
                    new ActorSeed("Pedro Sánchez Molina", "Zafra (Badajoz)"),
                    new ActorSeed("Lucía Romero Gil", "Alájar (Huelva)"),
                    new ActorSeed("Manuel Díaz Herrera", "Fregenal de la Sierra (Badajoz)"));

    private final ActorRepository actorRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int created = 0;
        int skipped = 0;

        for (ActorSeed seed : SEEDS) {
            if (actorRepository.existsByFullNameIgnoreCase(seed.fullName())) {
                skipped++;
                continue;
            }

            actorRepository.save(seed.toEntity());
            created++;
        }

        log.info(
                "Actor bootstrap completed: {} created, {} skipped, {} total definitions",
                created,
                skipped,
                SEEDS.size());
    }

    private record ActorSeed(String fullName, String region) {

        Actor toEntity() {
            return Actor.builder().fullName(fullName).region(region).build();
        }
    }
}
