package de.telran.businesstracker.service;

import de.telran.businesstracker.model.*;
import de.telran.businesstracker.repositories.MemberRepository;
import de.telran.businesstracker.repositories.MilestoneRepository;
import de.telran.businesstracker.repositories.ProjectRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    MilestoneRepository milestoneRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    TaskRepository taskRepository;
    @InjectMocks
    TaskService taskService;

    private Project project;
    private Task task;
    private Milestone milestone;
    private Member member;

    @BeforeEach
    public void beforeEachTest() {
        User user = new User(5L, "Ivan", "Petrov", "Boss", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        project = new Project(4L, "Great project", user, new LinkedHashSet<>(), new LinkedHashSet<>());
        Roadmap roadmap = new Roadmap(3L, "Roadmap", LocalDate.now(), project, new LinkedHashSet<>());
        member = new Member(13L, new Project(), new User());
        milestone = new Milestone(1L, "Milestone", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>(), new LinkedHashSet<>());
        task = new Task(9L, "Task", false, false, milestone, member, new ArrayList<>());
    }

    @Test
    public void testAdd_success() {
        when(memberRepository.findById(member.getId()))
                .thenReturn(Optional.of(member));
        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));

        taskService.add(task.getName(), task.isFinished(), task.isActive(), task.getMilestone().getId(), task.getResponsibleMember().getId());

        verify(taskRepository, times(1)).save(argThat(savedTask -> savedTask.getName().equals(task.getName()) &&
                !savedTask.isActive() && savedTask.getMilestone().getId().equals(milestone.getId()) &&
                savedTask.getResponsibleMember().getId().equals(member.getId()))
        );
    }

    @Test
    public void testAdd_memberDoesNotExist_EntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                taskService.add(task.getName(), task.isFinished(), task.isActive(), task.getMilestone().getId(), task.getResponsibleMember().getId())
        );

        verify(memberRepository, times(1)).findById(any());
        assertEquals("Error! This member doesn't exist in our DB", exception.getMessage());
    }

    @Test
    public void testEdit_taskExist_fieldsNameChanged() {
        String name = "newTask";

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        taskService.edit(task.getId(), name, task.isFinished(), task.isActive());

        verify(taskRepository, times(1)).save(argThat(
                savedTask -> savedTask.getName().equals(name) &&
                        savedTask.getId().equals(task.getId()) &&
                        savedTask.isFinished() == task.isFinished() &&
                        savedTask.isActive() == task.isActive() &&
                        savedTask.getMilestone().getId().equals(milestone.getId()) &&
                        savedTask.getResponsibleMember().getId().equals(member.getId()))
        );
    }

    @Test
    public void testEdit_taskExist_fieldsActiveChanged() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        taskService.edit(task.getId(), task.getName(), task.isFinished(), true);

        verify(taskRepository, times(1)).save(argThat(
                savedTask -> savedTask.getName().equals(task.getName()) &&
                        savedTask.getId().equals(task.getId()) &&
                        savedTask.isFinished() == task.isFinished() &&
                        savedTask.isActive() &&
                        savedTask.getMilestone().getId().equals(milestone.getId()) &&
                        savedTask.getResponsibleMember().getId().equals(member.getId()))
        );
    }

    @Test
    public void testEdit_taskExist_fieldsFinishedChanged() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        taskService.edit(task.getId(), task.getName(), true, task.isActive());

        verify(taskRepository, times(1)).save(argThat(
                savedTask -> savedTask.getName().equals(task.getName()) &&
                        savedTask.getId().equals(task.getId()) &&
                        savedTask.isFinished() &&
                        savedTask.isActive() == task.isActive() &&
                        savedTask.getMilestone().getId().equals(milestone.getId()) &&
                        savedTask.getResponsibleMember().getId().equals(member.getId()))
        );
    }

    @Test
    void testGetById_objectExist() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        Task task1 = taskService.getById(task.getId());

        assertEquals(task1.getName(), task.getName());
        assertEquals(task1.isActive(), task.isActive());
        assertEquals(task1.isFinished(), task.isFinished());
        assertEquals(task1.getMilestone(), task.getMilestone());
        assertEquals(task1.getResponsibleMember(), task.getResponsibleMember());

        verify(taskRepository, times(1)).findById(argThat(
                id -> id.equals(this.task.getId())));
    }

    @Test
    void testGetTasksByProjectAndActive_fourElementsFound() {
        List<Task> tasks = Arrays.asList(
                task,
                new Task(3L, "Task_02", false, true, milestone, member, Collections.emptyList()),
                new Task(4L, "Task_03", false, true, milestone, member, Collections.emptyList()),
                new Task(5L, "Task_04", false, true, milestone, member, Collections.emptyList())
        );

        when(taskRepository.findAllByMilestone_Roadmap_ProjectAndActiveTrue(project)).thenReturn(tasks);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        List<Task> tasksResult = taskService.getAllActiveTasksByProjectId(project.getId());

        verify(projectRepository, times(1)).findById(project.getId());
        verify(taskRepository, times(1)).findAllByMilestone_Roadmap_ProjectAndActiveTrue(project);

        assertEquals(tasks.size(), tasksResult.size());

        assertEquals(tasks.get(0).getName(), tasksResult.get(0).getName());
        assertEquals(tasks.get(0).getId(), tasksResult.get(0).getId());

        assertEquals(tasks.get(1).getName(), tasksResult.get(1).getName());
        assertEquals(tasks.get(1).getId(), tasksResult.get(1).getId());

        assertEquals(tasks.get(2).getName(), tasksResult.get(2).getName());
        assertEquals(tasks.get(2).getId(), tasksResult.get(2).getId());

        assertEquals(tasks.get(3).getName(), tasksResult.get(3).getName());
        assertEquals(tasks.get(3).getId(), tasksResult.get(3).getId());
    }

    @Test
    void testGetTasksByMileStone_fourElementsFound() {
        List<Task> tasks = Arrays.asList(
                task,
                new Task(3L, "Task_02", false, true, milestone, member, Collections.emptyList()
                ),
                new Task(4L, "Task_03", false, true, milestone, member, Collections.emptyList()
                ),
                new Task(5L, "Task_04", false, true, milestone, member, Collections.emptyList()
                )
        );

        when(taskRepository.findAllByMilestone(milestone)).thenReturn(tasks);
        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));

        List<Task> tasksResult = taskService.getAllByMileStoneId(milestone.getId());

        verify(projectRepository, times(0)).findById(any());
        verify(taskRepository, times(0)).findAllByMilestone_Roadmap_ProjectAndActiveTrue(any());

        verify(milestoneRepository, times(1)).findById(milestone.getId());
        verify(taskRepository, times(1)).findAllByMilestone(milestone);

        assertEquals(tasks.size(), tasksResult.size());

        assertEquals(tasks.get(0).getName(), tasksResult.get(0).getName());
        assertEquals(tasks.get(0).getId(), tasksResult.get(0).getId());

        assertEquals(tasks.get(1).getName(), tasksResult.get(1).getName());
        assertEquals(tasks.get(1).getId(), tasksResult.get(1).getId());

        assertEquals(tasks.get(2).getName(), tasksResult.get(2).getName());
        assertEquals(tasks.get(2).getId(), tasksResult.get(2).getId());

        assertEquals(tasks.get(3).getName(), tasksResult.get(3).getName());
        assertEquals(tasks.get(3).getId(), tasksResult.get(3).getId());
    }

    @Test
    void testGetById_objectNotExist() {
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.getById(task.getId() + 1));

        verify(taskRepository, times(1)).findById(any());
        assertEquals("Error! This task doesn't exist in our DB", exception.getMessage());
    }

    @Captor
    ArgumentCaptor<Task> taskArgumentCaptor;

    @Test
    void removeById_oneObjectDeleted() {

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));

        taskService.add(task.getName(), task.isFinished(), task.isActive(), task.getMilestone().getId(), task.getResponsibleMember().getId());
        taskService.removeById(task.getId());

        List<Task> capturedTasks = taskArgumentCaptor.getAllValues();
        verify(taskRepository, times(1)).deleteById(task.getId());
        assertEquals(0, capturedTasks.size());
    }
}
