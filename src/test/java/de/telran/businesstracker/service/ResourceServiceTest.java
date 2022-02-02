package de.telran.businesstracker.service;

import de.telran.businesstracker.model.*;
import de.telran.businesstracker.repositories.ResourceRepository;
import de.telran.businesstracker.repositories.TaskRepository;
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
class ResourceServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    ResourceRepository resourceRepository;

    @InjectMocks
    ResourceService resourceService;

    private Task task;
    private Resource resource;

    @BeforeEach
    public void beforeEachTest() {
        User user = new User(5L, "Ivan", "Petrov", "Boss", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        Project project = new Project(4L, "Great project", user, new LinkedHashSet<>(), new LinkedHashSet<>());
        Roadmap roadmap = new Roadmap(3L, "Roadmap", LocalDate.now(), project, new LinkedHashSet<>());
        Milestone milestone = new Milestone(1L, "Milestone", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>(), new LinkedHashSet<>());
        Member member = new Member(13L, new Project(), new User());

        task = new Task(9L, "Task", false, false,  milestone, member, new ArrayList<>());
        resource = new Resource(1L, "Resource", 100, 1290.00, task);
    }

    @Test
    public void testAdd_success() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        resourceService.add(resource.getName(), resource.getHours(), resource.getCost(),
                resource.getTask().getId());

        verify(resourceRepository, times(1)).save(any());
        verify(resourceRepository, times(1))
                .save(argThat(savedResource -> savedResource.getName().equals(resource.getName()) &&
                        savedResource.getHours().equals(resource.getHours()) &&
                        savedResource.getCost().equals(resource.getCost()) &&
                        savedResource.getTask().getId().equals(resource.getTask().getId())
                ));
    }

    @Test
    public void testAdd_taskDoesNotExist_EntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.add(resource.getName(), resource.getHours(), resource.getCost(),
                        resource.getTask().getId()));

        verify(taskRepository, times(1)).findById(any());
        assertEquals("Error! This task doesn't exist in our DB", exception.getMessage());
    }

    @Test
    public void resourceEdit_resourceExist_fieldsChanged() {
        String newName = "New milestone";
        Integer newHours = 80;
        Double newCast = 5000.00;

        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        resourceService.edit(resource.getId(), newName, newHours, newCast);

        verify(resourceRepository, times(1)).save(any());
        verify(resourceRepository, times(1))
                .save(argThat(savedResource -> savedResource.getName().equals(newName) &&
                        savedResource.getHours().equals(newHours) &&
                        savedResource.getCost().equals(newCast) &&
                        savedResource.getTask().getId().equals(task.getId()))
                );
    }

    @Test
    void testGetById_objectExist() {
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        Resource expectedResource = resourceService.getById(resource.getId());

        assertEquals(expectedResource.getName(), resource.getName());
        assertEquals(expectedResource.getHours(), resource.getHours());
        assertEquals(expectedResource.getCost(), resource.getCost());
        assertEquals(expectedResource.getTask(), resource.getTask());

        verify(resourceRepository, times(1)).findById(argThat(
                id -> id.equals(resource.getId())));
    }

    @Test
    void testGetById_objectNotExist() {
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.getById(resource.getId() + 1));

        verify(resourceRepository, times(1)).findById(any());
        assertEquals("Error! This resource doesn't exist in our DB", exception.getMessage());

    }

    @Captor
    ArgumentCaptor<Resource> taskArgumentCaptor;

    @Test
    void removeById_oneObjectDeleted() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        resourceService.add(resource.getName(), resource.getHours(), resource.getCost(),
                resource.getTask().getId());
        resourceService.removeById(resource.getId());

        List<Resource> capturedResources = taskArgumentCaptor.getAllValues();
        verify(resourceRepository, times(1)).deleteById(resource.getId());
        assertEquals(0, capturedResources.size());
    }
}
