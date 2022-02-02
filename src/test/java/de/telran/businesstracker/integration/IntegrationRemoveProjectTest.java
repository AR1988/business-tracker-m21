package de.telran.businesstracker.integration;

import de.telran.businesstracker.model.*;
import de.telran.businesstracker.repositories.*;
import de.telran.businesstracker.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class IntegrationRemoveProjectTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MilestoneRepository milestoneRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    RoadmapRepository roadmapRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    ProjectService projectService;

    @Test
    public void integrationTest() {
        User user3 = new User("3Ivan", "3Petrov", "3Boss", "3img-url");
        userRepository.save(user3);

        User user = new User("Ivan", "Petrov", "Boss", "img-url");
        Project project = new Project("Great project", user3);
        Roadmap roadmap = new Roadmap("Roadmap", LocalDate.now(), project);
        Milestone milestone = new Milestone("Milestone", roadmap, new ArrayList<>());
        milestone.addKpi("kpi_0_1");
        milestone.addKpi("kpi_0_2");
        milestone.addKpi("kpi_0_3");

        Member member = new Member(project, user);

        Task task = new Task("Task", false, false, milestone, member);
        Resource resource = new Resource("Resource", 100, 1290.00, task);

        saveComplete(user, project, roadmap, milestone, member, task, resource);

        User user2 = new User("2 Ivan", "2 Petrov", "2 Boss", "2 img-url");
        Project project2 = new Project("2 Great project", user2);
        Roadmap roadmap2 = new Roadmap("2 Roadmap", project2);
        Milestone milestone2 = new Milestone("2 Milestone", roadmap2, new ArrayList<>());
        milestone2.addKpi("kpi_2_1");
        milestone2.addKpi("kpi_2_2");
        milestone2.addKpi("kpi_2_3");

        Member member2 = new Member(project2, user2);

        Task task2 = new Task("2 Task", false, false, milestone2, member2);
        Resource resource2 = new Resource("2 Resource", 100, 1290.00, task2);

        saveComplete(user2, project2, roadmap2, milestone2, member2, task2, resource2);

        Assertions.assertEquals(projectRepository.count(), 2);
        List<Project> list = projectRepository.findAll();

        final Long PROJECT_ID_1 = list.get(0).getId();
        final Long PROJECT_ID_2 = list.get(1).getId();

        projectService.removeById(PROJECT_ID_1);

        Project projectsTwoFromService = projectService.getById(project2.getId());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> projectService.getById(PROJECT_ID_1));
        Assertions.assertEquals(exception.getMessage(), "Error! This project doesn't exist in our DB");

        Assertions.assertEquals(projectsTwoFromService, project2);

        Assertions.assertTrue(userRepository.existsByNameIgnoreCaseAndLastNameIgnoreCaseAndPositionIgnoreCase(user.getName(), user.getLastName(), user.getPosition()));
        Assertions.assertTrue(userRepository.existsByNameIgnoreCaseAndLastNameIgnoreCaseAndPositionIgnoreCase(user2.getName(), user2.getLastName(), user2.getPosition()));
        Assertions.assertTrue(userRepository.existsByNameIgnoreCaseAndLastNameIgnoreCaseAndPositionIgnoreCase(user3.getName(), user3.getLastName(), user3.getPosition()));

        Assertions.assertEquals(projectRepository.count(), 1);
        Assertions.assertEquals(roadmapRepository.count(), 1);
        Assertions.assertEquals(memberRepository.count(), 1);
        Assertions.assertEquals(taskRepository.count(), 1);
        Assertions.assertEquals(resourceRepository.count(), 1);

        projectService.removeById(PROJECT_ID_2);

        Assertions.assertTrue(userRepository.existsByNameIgnoreCaseAndLastNameIgnoreCaseAndPositionIgnoreCase(user.getName(), user.getLastName(), user.getPosition()));
        Assertions.assertTrue(userRepository.existsByNameIgnoreCaseAndLastNameIgnoreCaseAndPositionIgnoreCase(user2.getName(), user2.getLastName(), user2.getPosition()));
        Assertions.assertTrue(userRepository.existsByNameIgnoreCaseAndLastNameIgnoreCaseAndPositionIgnoreCase(user3.getName(), user3.getLastName(), user3.getPosition()));

        Assertions.assertEquals(projectRepository.count(), 0);
        Assertions.assertEquals(roadmapRepository.count(), 0);
        Assertions.assertEquals(memberRepository.count(), 0);
        Assertions.assertEquals(taskRepository.count(), 0);
        Assertions.assertEquals(resourceRepository.count(), 0);
    }

    private void saveComplete(User user, Project project, Roadmap roadmap, Milestone milestone, Member member, Task task, Resource resource) {
        userRepository.save(user);
        projectRepository.save(project);
        roadmapRepository.save(roadmap);
        milestoneRepository.save(milestone);
        memberRepository.save(member);
        taskRepository.save(task);
        resourceRepository.save(resource);
    }
}
