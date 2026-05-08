package com.almanatura.api.bootstrap;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.entity.Project;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class ProjectBootstrapRunner implements ApplicationRunner {

    private static final List<ProjectSeed> SEEDS =
            List.of(
                    new ProjectSeed(
                            "Proyecto MIES",
                            "Proyecto desarrollado con Ashoka España y Google.org para impulsar"
                                    + " oportunidades en el mundo rural.",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2024, 5),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=800"),
                    new ProjectSeed(
                            "The Break",
                            "Proyecto desarrollado con la Escuela de Organización Industrial y The"
                                    + " Break Alliance.",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2023, 7),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1552664730-d307ca884978?w=800"),
                    new ProjectSeed(
                            "Activa tu pueblo",
                            "Proyecto desarrollado con la Mancomunidad Beturia y ADRAO Desarrollo"
                                    + " Rural.",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2023, 6),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800"),
                    new ProjectSeed(
                            "RURAL 2030. La Universidad en el pueblo",
                            "Proyecto desarrollado con la UNIA y la Diputación de Huelva.",
                            ProjectPillar.EDUCATION,
                            startOfMonth(2023, 6),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=800"),
                    new ProjectSeed(
                            "Colabora Almendralejo",
                            "Proyecto desarrollado con ACCIONA Energía y el Ayuntamiento de"
                                    + " Almendralejo (Badajoz).",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2023, 5),
                            null,
                            "Almendralejo (Badajoz)",
                            "https://images.unsplash.com/photo-1473341304170-971dccb5ac1e?w=800"),
                    new ProjectSeed(
                            "GIRA Jóvenes",
                            "Proyecto desarrollado con Coca-Cola España e institutos de formación"
                                    + " profesional.",
                            ProjectPillar.EDUCATION,
                            startOfMonth(2023, 5),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1529390079861-591de354faf5?w=800"),
                    new ProjectSeed(
                            "Holapueblo",
                            "Proyecto desarrollado con Redeia, IKEA y ayuntamientos.",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2023, 5),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1518780664697-55e3ad937233?w=800"),
                    new ProjectSeed(
                            "GIRA Mujeres",
                            "Proyecto desarrollado con Coca-Cola España y entidades públicas.",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2023, 4),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=800"),
                    new ProjectSeed(
                            "AlmaNatura LAB",
                            "Proyecto desarrollado con Fundación AlmaNatura.",
                            ProjectPillar.TECHNOLOGY,
                            startOfMonth(2023, 4),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800"),
                    new ProjectSeed(
                            "Relevo Generacional",
                            "Proyecto desarrollado con Danone España y granjas lecheras del"
                                    + " territorio español.",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2021, 7),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1500076656116-558758c991c1?w=800"),
                    new ProjectSeed(
                            "Comisionado Reto Demográfico",
                            "Proyecto desarrollado con la Diputación de Huelva y ayuntamientos de"
                                    + " la provincia.",
                            ProjectPillar.EDUCATION,
                            startOfMonth(2021, 6),
                            null,
                            "Provincia de Huelva",
                            "https://images.unsplash.com/photo-1491438590914-bc09fcaaf77a?w=800"),
                    new ProjectSeed(
                            "Rural Emprende",
                            "Proyecto desarrollado con Fundación Andalucía Emprende y la Diputación"
                                    + " de Jaén.",
                            ProjectPillar.ENTREPRENEURSHIP,
                            startOfMonth(2019, 9),
                            null,
                            "Jaén",
                            "https://images.unsplash.com/photo-1556761175-4b46a572b786?w=800"),
                    new ProjectSeed(
                            "Lab de Innovación Rural",
                            "Proyecto desarrollado con Impact Hub Madrid y la Red Española de"
                                    + " Desarrollo Rural.",
                            ProjectPillar.TECHNOLOGY,
                            startOfMonth(2019, 9),
                            null,
                            "Madrid",
                            "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800"),
                    new ProjectSeed(
                            "Tu Caja Online",
                            "Proyecto desarrollado con Fundación Caja Rural del Sur y"
                                    + " ayuntamientos.",
                            ProjectPillar.TECHNOLOGY,
                            startOfMonth(2018, 9),
                            null,
                            null,
                            "https://images.unsplash.com/photo-1563986768609-322da13575f2?w=800"));

    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int created = 0;
        int skipped = 0;

        for (ProjectSeed seed : SEEDS) {
            if (projectRepository.existsByTitleIgnoreCase(seed.title())) {
                skipped++;
                continue;
            }

            projectRepository.save(seed.toEntity());
            created++;
        }

        log.info(
                "Project bootstrap completed: {} created, {} skipped, {} total definitions",
                created,
                skipped,
                SEEDS.size());
    }

    private static Instant startOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).atTime(LocalTime.of(10, 0)).toInstant(ZoneOffset.UTC);
    }

    private record ProjectSeed(
            String title,
            String description,
            ProjectPillar pillar,
            Instant startsAt,
            Instant endsAt,
            String location,
            String imageUrl) {

        Project toEntity() {
            return Project.builder()
                    .title(title)
                    .description(description)
                    .pillar(pillar)
                    .status(ProjectStatus.PUBLISHED)
                    .startsAt(startsAt)
                    .endsAt(endsAt)
                    .location(location)
                    .imageUrl(imageUrl)
                    .build();
        }
    }
}
