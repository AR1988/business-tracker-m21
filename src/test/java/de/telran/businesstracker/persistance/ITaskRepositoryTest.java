package de.telran.businesstracker.persistance;

import de.telran.businesstracker.model.*;
import de.telran.businesstracker.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ITaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User user;
    private Roadmap roadmap;
    private Project project;
    private Milestone milestone;

    Member member;

    @BeforeEach
    public void beforeEachTest() {
        user = new User("Ivan", "Petrov", "Boss", "img-url");
        project = new Project("Great project", user);
        roadmap = new Roadmap("Roadmap", LocalDate.now(), project);
        milestone = new Milestone("Milestone", roadmap, new ArrayList<>());
        member = new Member(project, user);
    }

    @Test
    public void testFindAllByMileStone_oneMileStone_viveRecordsFound() {
        entityManager.persist(user);
        entityManager.persist(project);

        entityManager.persist(roadmap);
        entityManager.persist(member);
        entityManager.persist(milestone);

        List<Task> tasks = Arrays.asList(
                new Task("Task_01", false, true, milestone, member),
                new Task("Task_02", false, false, milestone, member),
                new Task("Task_03", false, true, milestone, member),
                new Task("Task_04", false, false, milestone, member),
                new Task("Task_05", false, false, milestone, member)
        );

        tasks.forEach(task -> entityManager.persist(task));

        entityManager.flush();
        entityManager.clear();

        List<Task> foundTasks = taskRepository.findAllByMilestone(milestone);
        assertEquals(5, foundTasks.size());

        assertEquals(tasks.get(0).getName(), foundTasks.get(0).getName());
        assertEquals(tasks.get(1).getName(), foundTasks.get(1).getName());
        assertEquals(tasks.get(2).getName(), foundTasks.get(2).getName());
        assertEquals(tasks.get(3).getName(), foundTasks.get(3).getName());
        assertEquals(tasks.get(4).getName(), foundTasks.get(4).getName());

        assertEquals(tasks.get(0).getMilestone().getName(), foundTasks.get(0).getMilestone().getName());
        assertEquals(tasks.get(1).getMilestone().getName(), foundTasks.get(1).getMilestone().getName());
        assertEquals(tasks.get(2).getMilestone().getName(), foundTasks.get(2).getMilestone().getName());
        assertEquals(tasks.get(3).getMilestone().getName(), foundTasks.get(3).getMilestone().getName());
        assertEquals(tasks.get(4).getMilestone().getName(), foundTasks.get(4).getMilestone().getName());

        assertEquals(tasks.get(0).isActive(), foundTasks.get(0).isActive());
        assertEquals(tasks.get(2).isActive(), foundTasks.get(2).isActive());
    }

    @Test
    public void testFindAllByMileStone_threeMileStone_viveRecordsFound() {
        entityManager.persist(user);
        entityManager.persist(project);

        Milestone milestone = new Milestone("Milestone", roadmap, new ArrayList<>());
        Milestone milestone2 = new Milestone("Milestone_02", roadmap, new ArrayList<>());
        Milestone milestone3 = new Milestone("Milestone_03", roadmap, new ArrayList<>());

        entityManager.persist(roadmap);
        entityManager.persist(member);
        entityManager.persist(milestone);
        entityManager.persist(milestone2);
        entityManager.persist(milestone3);

        List<Task> tasks = Arrays.asList(
                new Task("Task_01", false, true, milestone, member),
                new Task("Task_02", false, false, milestone, member),
                new Task("Task_03", false, true, milestone, member),
                new Task("Task_04", false, false, milestone, member),
                new Task("Task_05", false, false, milestone, member),

                new Task("Task_23", false, true, milestone2, member),
                new Task("Task_24", false, false, milestone2, member),
                new Task("Task_25", false, false, milestone2, member),
                new Task("Task_35", false, false, milestone3, member)
        );

        tasks.forEach(task -> entityManager.persist(task));

        entityManager.flush();
        entityManager.clear();

        List<Task> foundTasks = taskRepository.findAllByMilestone(milestone);
        assertEquals(5, foundTasks.size());

        assertEquals(tasks.get(0).getName(), foundTasks.get(0).getName());
        assertEquals(tasks.get(1).getName(), foundTasks.get(1).getName());
        assertEquals(tasks.get(2).getName(), foundTasks.get(2).getName());
        assertEquals(tasks.get(3).getName(), foundTasks.get(3).getName());
        assertEquals(tasks.get(4).getName(), foundTasks.get(4).getName());

        assertEquals(tasks.get(0).getMilestone().getName(), foundTasks.get(0).getMilestone().getName());
        assertEquals(tasks.get(1).getMilestone().getName(), foundTasks.get(1).getMilestone().getName());
        assertEquals(tasks.get(2).getMilestone().getName(), foundTasks.get(2).getMilestone().getName());
        assertEquals(tasks.get(3).getMilestone().getName(), foundTasks.get(3).getMilestone().getName());
        assertEquals(tasks.get(4).getMilestone().getName(), foundTasks.get(4).getMilestone().getName());

        assertEquals(tasks.get(0).isActive(), foundTasks.get(0).isActive());
        assertEquals(tasks.get(2).isActive(), foundTasks.get(2).isActive());
    }

    @Test
    public void testFindByMileStone_twoMileStone_noElementsFound() {
        Milestone milestone = new Milestone("Milestone", roadmap, new ArrayList<>());
        Milestone milestone2 = new Milestone("Milestone_02", roadmap, new ArrayList<>());

        entityManager.persist(user);
        entityManager.persist(project);
        entityManager.persist(roadmap);

        entityManager.persist(member);

        entityManager.persist(milestone);
        entityManager.persist(milestone2);

        Task task = new Task("Task_01", false, true, milestone2, member);

        entityManager.persist(task);

        entityManager.flush();
        entityManager.clear();

        List<Task> foundTasks = taskRepository.findAllByMilestone(milestone);
        assertEquals(0, foundTasks.size());
    }

    @Test
    public void testFindByProjectAndActive_oneProject_threeMileStone_fourRecordsFound() {

        Milestone milestone = new Milestone("Milestone", roadmap, new ArrayList<>());
        Milestone milestone2 = new Milestone("Milestone_02", roadmap, new ArrayList<>());
        Milestone milestone3 = new Milestone("Milestone_03", roadmap, new ArrayList<>());

        entityManager.persist(user);
        entityManager.persist(project);

        entityManager.persist(roadmap);
        entityManager.persist(member);

        entityManager.persist(milestone);
        entityManager.persist(milestone2);
        entityManager.persist(milestone3);

        List<Task> tasks = Arrays.asList(
                new Task("Task_01", false, true, milestone, member),
                new Task("Task_02", true, false, milestone, member),
                new Task("Task_03", false, true, milestone, member),
                new Task("Task_04", true, false, milestone, member),
                new Task("Task_05", false, false, milestone, member),

                new Task("Task_23", false, true, milestone2, member),
                new Task("Task_24", false, false, milestone2, member),
                new Task("Task_25", true, false, milestone2, member),

                new Task("Task_35", false, true, milestone3, member)
        );

        tasks.forEach(task -> entityManager.persist(task));

        entityManager.flush();
        entityManager.clear();

        List<Task> foundTasks = taskRepository.findAllByMilestone_Roadmap_ProjectAndActiveTrue(project);
        assertEquals(4, foundTasks.size());

        assertEquals("Task_01", foundTasks.get(0).getName());
        assertEquals("Task_03", foundTasks.get(1).getName());
        assertEquals("Task_23", foundTasks.get(2).getName());
        assertEquals("Task_35", foundTasks.get(3).getName());

        assertEquals("Milestone", foundTasks.get(0).getMilestone().getName());
        assertEquals("Milestone", foundTasks.get(1).getMilestone().getName());
        assertEquals("Milestone_02", foundTasks.get(2).getMilestone().getName());
        assertEquals("Milestone_03", foundTasks.get(3).getMilestone().getName());
    }

    @Test
    public void testFindByProjectAndActive_twoProject_fourMileStone_fourRecordsFound() {
        Milestone milestone = new Milestone("Milestone", roadmap, new ArrayList<>());
        Milestone milestone2 = new Milestone("Milestone_02", roadmap, new ArrayList<>());
        Milestone milestone3 = new Milestone("Milestone_03", roadmap, new ArrayList<>());

        User user2 = new User("2 Ivan", "2 Petrov", "2 Boss", "2 img-url");
        Project project2 = new Project("2 Great project", user2);
        Roadmap roadmap2 = new Roadmap("2 Roadmap", project2);

        Milestone milestone4 = new Milestone("Milestone_03", roadmap2, new ArrayList<>());

        entityManager.persist(user);
        entityManager.persist(user2);

        entityManager.persist(project);
        entityManager.persist(project2);

        entityManager.persist(roadmap);
        entityManager.persist(roadmap2);

        entityManager.persist(member);

        entityManager.persist(milestone);
        entityManager.persist(milestone2);
        entityManager.persist(milestone3);
        entityManager.persist(milestone4);

        List<Task> tasks = Arrays.asList(
                new Task("Task_01", false, true, milestone, member),
                new Task("Task_02", true, false, milestone, member),
                new Task("Task_03", false, true, milestone, member),
                new Task("Task_04", true, false, milestone, member),
                new Task("Task_05", false, false, milestone, member),

                new Task("Task_23", false, true, milestone2, member),
                new Task("Task_24", false, false, milestone2, member),
                new Task("Task_25", true, false, milestone2, member),

                new Task("Task_35", false, true, milestone3, member),

                new Task("Task_223", false, true, milestone4, member),
                new Task("Task_224", false, true, milestone4, member),
                new Task("Task_225", true, false, milestone4, member),
                new Task("Task_235", false, true, milestone4, member)
        );

        tasks.forEach(task -> entityManager.persist(task));

        entityManager.flush();
        entityManager.clear();

        List<Task> foundTasks = taskRepository.findAllByMilestone_Roadmap_ProjectAndActiveTrue(project);
        assertEquals(4, foundTasks.size());

        assertEquals("Task_01", foundTasks.get(0).getName());
        assertEquals("Task_03", foundTasks.get(1).getName());
        assertEquals("Task_23", foundTasks.get(2).getName());
        assertEquals("Task_35", foundTasks.get(3).getName());

        assertEquals("Milestone", foundTasks.get(0).getMilestone().getName());
        assertEquals("Milestone", foundTasks.get(1).getMilestone().getName());
        assertEquals("Milestone_02", foundTasks.get(2).getMilestone().getName());
        assertEquals("Milestone_03", foundTasks.get(3).getMilestone().getName());
    }


    @Test
    public void testFindByProjectAndActive_oneProject_twoMileStone_noRecordsFound() {

        entityManager.persist(user);

        User user2 = new User("2 Ivan", "2 Petrov", "2 Boss", "2 img-url");
        entityManager.persist(user2);
        Project project2 = new Project("2 Great project", user2);
        entityManager.persist(project);
        entityManager.persist(project2);

        entityManager.persist(roadmap);
        entityManager.persist(member);

        entityManager.persist(milestone);
        entityManager.persist(new Task("Task", false, false, milestone, member));

        entityManager.flush();
        entityManager.clear();

        List<Task> foundTasks = taskRepository.findAllByMilestone_Roadmap_ProjectAndActiveTrue(project);
        assertEquals(0, foundTasks.size());
    }
}
