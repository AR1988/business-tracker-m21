package de.telran.businesstracker.service;

import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.User;
import de.telran.businesstracker.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RoadmapRepository roadmapRepository;
    @Mock
    MilestoneRepository milestoneRepository;

    @InjectMocks
    ProjectService projectService;

    private User user;
    private Project project;

    @BeforeEach
    public void beforeEachTest() {
        user = new User(5L, "Ivan", "Petrov", "Boss", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        project = new Project(4L, "Great project", user, new LinkedHashSet<>(), new LinkedHashSet<>());
    }

    @Test
    public void testAdd_success() {
        User user2 = new User(2L, "Vasja", "Pupkin", "Dev", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        User user3 = new User(3L, "Vasja", "Pupkin", "Dev", "img-url", new LinkedHashSet<>(), new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));

        projectService.add(project.getName(), List.of(user2.getId(), user3.getId()));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).findById(3L);
        verify(userRepository, times(0)).findById(5L);

        verify(memberRepository, times(1)).saveAll(anyList());
        verify(roadmapRepository, times(1)).save(argThat(argument -> argument.getName().equals("Roadmap")));
        verify(milestoneRepository, times(1)).saveAll(anyList());

        verify(projectRepository, times(1))
                .save(argThat(savedProject -> savedProject.getName().equals(project.getName()) &&
                        savedProject.getUser().getId().equals(user.getId()))
                );
    }

    @Test
    public void testAdd_userDoesNotExist_EntityNotFoundException() {

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                projectService.add(project.getName(), List.of(1L, 2L)));

        verify(userRepository, times(1)).findById(any());
        assertEquals("Error! This user doesn't exist in our DB", exception.getMessage());
    }

    @Test
    public void projectEdit_projectExist_fieldsChanged() {

        String newName = "Small project";
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        projectService.edit(project.getId(), newName);

        verify(projectRepository, times(1))
                .save(argThat(savedProject -> savedProject.getName().equals(newName) &&
                        savedProject.getUser().getId().equals(user.getId()))
                );
    }

    @Test
    void getAll_twoObjects() {

        Project project2 = new Project(44L, "Small project", user, new LinkedHashSet<>(), new LinkedHashSet<>());

        List<Project> projects = new ArrayList<>();

        projects.add(project);
        projects.add(project2);

        when(projectRepository.findAll()).thenReturn(projects);

        assertEquals(project.getName(), projectService.getAll().get(0).getName());
        assertEquals(project.getUser(), projectService.getAll().get(0).getUser());

        assertEquals(project2.getName(), projectService.getAll().get(1).getName());
        assertEquals(project2.getUser(), projectService.getAll().get(1).getUser());
    }

    @Test
    void testGetById_objectExist() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        Project expectedProject = projectService.getById(project.getId());

        assertEquals(expectedProject.getName(), project.getName());
        assertEquals(expectedProject.getUser(), project.getUser());

        verify(projectRepository, times(1)).findById(argThat(
                id -> id.equals(project.getId())));
    }

    @Test
    void testGetById_objectNotExist() {

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getById(project.getId() + 1));

        verify(projectRepository, times(1)).findById(any());
        assertEquals("Error! This project doesn't exist in our DB", exception.getMessage());

    }

    @Test
    void removeById_oneObjectDeleted() {

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        projectService.removeById(project.getId());

        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, times(1)).delete(project);
    }
}
