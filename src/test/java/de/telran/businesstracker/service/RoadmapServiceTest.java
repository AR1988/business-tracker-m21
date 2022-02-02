package de.telran.businesstracker.service;

import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.Roadmap;
import de.telran.businesstracker.model.User;
import de.telran.businesstracker.repositories.ProjectRepository;
import de.telran.businesstracker.repositories.RoadmapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoadmapServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    RoadmapRepository roadmapRepository;

    @InjectMocks
    RoadmapService roadmapService;


    private Project project;
    private Roadmap roadmap;

    @BeforeEach
    public void beforeEachTest() {
        User user = new User(5L, "Ivan", "Petrov", "Boss", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        project = new Project(4L, "Great project", user, new LinkedHashSet<>(), new LinkedHashSet<>());
        roadmap = new Roadmap(3L, "Roadmap", LocalDate.now(), project, new LinkedHashSet<>());
    }

    @Test
    public void testAdd_success() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        roadmapService.add(roadmap.getName(), roadmap.getStartDate(), roadmap.getProject().getId());

        verify(roadmapRepository, times(1)).save(any());
        verify(roadmapRepository, times(1))
                .save(argThat(savedRoadmap -> savedRoadmap.getName().equals(roadmap.getName()) &&
                        savedRoadmap.getStartDate().equals(roadmap.getStartDate()) &&
                        savedRoadmap.getProject().getId().equals(project.getId()))
                );
    }

    @Test
    public void testAdd_success_StartDeteNull() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        roadmapService.add(roadmap.getName(), null, roadmap.getProject().getId());

        verify(roadmapRepository, times(1)).save(any());
        verify(roadmapRepository, times(1))
                .save(argThat(savedRoadmap -> savedRoadmap.getName().equals(roadmap.getName()) &&
                        savedRoadmap.getStartDate().equals(LocalDate.now()) &&
                        savedRoadmap.getProject().getId().equals(project.getId()))
                );
    }

    @Test
    public void testAdd_projectDoesNotExist_EntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                roadmapService.add(roadmap.getName(), roadmap.getStartDate(), roadmap.getProject().getId() + 1));

        verify(projectRepository, times(1)).findById(any());
        assertEquals("Error! This project doesn't exist in our DB", exception.getMessage());
    }

    @Test
    public void roadmapEdit_roadmapExist_fieldsChanged() {
        String newName = "New roadmap";
        LocalDate newStartDay = LocalDate.now().plusDays(1);

        when(roadmapRepository.findById(roadmap.getId())).thenReturn(Optional.of(roadmap));
        roadmapService.edit(roadmap.getId(), newName, newStartDay);

        verify(roadmapRepository, times(1)).save(any());
        verify(roadmapRepository, times(1))
                .save(argThat(savedRoadmap -> savedRoadmap.getName().equals(newName) &&
                        savedRoadmap.getStartDate().equals(newStartDay) &&
                        savedRoadmap.getProject().getId().equals(project.getId()))
                );
    }

    @Test
    void testGetById_objectExist() {
        when(roadmapRepository.findById(roadmap.getId())).thenReturn(Optional.of(roadmap));
        Roadmap expectedRoadmap = roadmapService.getById(roadmap.getId());

        assertEquals(expectedRoadmap.getName(), roadmap.getName());
        assertEquals(expectedRoadmap.getStartDate(), roadmap.getStartDate());
        assertEquals(expectedRoadmap.getProject(), roadmap.getProject());

        verify(roadmapRepository, times(1)).findById(argThat(
                id -> id.equals(roadmap.getId())));
    }

    @Test
    void testGetById_objectNotExist() {
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> roadmapService.getById(roadmap.getId() + 1));

        verify(roadmapRepository, times(1)).findById(any());
        assertEquals("Error! This roadmap doesn't exist in our DB", exception.getMessage());

    }

    @Captor
    ArgumentCaptor<Roadmap> taskArgumentCaptor;

    @Test
    void removeById_oneObjectDeleted() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        roadmapService.add(roadmap.getName(), roadmap.getStartDate(), roadmap.getProject().getId());
        roadmapService.removeById(roadmap.getId());

        List<Roadmap> capturedRoadmaps = taskArgumentCaptor.getAllValues();
        verify(roadmapRepository, times(1)).deleteById(roadmap.getId());
        assertEquals(0, capturedRoadmaps.size());
    }
}
