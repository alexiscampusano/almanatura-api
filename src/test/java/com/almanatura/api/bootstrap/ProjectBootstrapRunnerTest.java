package com.almanatura.api.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import com.almanatura.api.entity.Project;
import com.almanatura.api.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectBootstrapRunnerTest {

    @Mock private ProjectRepository projectRepository;

    @Captor private ArgumentCaptor<Project> projectCaptor;

    @Test
    void run_createsCuratedProjects_once() throws Exception {
        Set<String> seededTitles = new HashSet<>();

        when(projectRepository.existsByTitleIgnoreCase(any())).thenAnswer(invocation -> {
            String title = invocation.getArgument(0, String.class);
            return seededTitles.contains(title.toLowerCase());
        });
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project project = invocation.getArgument(0, Project.class);
            seededTitles.add(project.getTitle().toLowerCase());
            return project;
        });

        ProjectBootstrapRunner projectBootstrapRunner = new ProjectBootstrapRunner(projectRepository);

        projectBootstrapRunner.run(new DefaultApplicationArguments(new String[0]));
        verify(projectRepository, times(14)).save(projectCaptor.capture());

        assertThat(seededTitles).hasSize(14);
        assertThat(projectCaptor.getAllValues())
                .allSatisfy(
                        project ->
                                assertThat(project.getStatus().name()).isEqualTo("PUBLISHED"));

        projectBootstrapRunner.run(new DefaultApplicationArguments(new String[0]));

        verify(projectRepository, times(14)).save(any(Project.class));
        assertThat(seededTitles).hasSize(14);
    }
}