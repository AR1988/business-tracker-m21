package de.telran.businesstracker.integration;

import de.telran.businesstracker.model.*;
import de.telran.businesstracker.repositories.*;
import de.telran.businesstracker.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IntegrationTest {

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
    TaskService taskService;

    @Test
    public void integrationTestTask() {
        User user = new User();
        userRepository.save(user);

        Project project = new Project();
        projectRepository.save(project);

        Roadmap roadmap = new Roadmap();
        roadmapRepository.save(roadmap);

        Member member = new Member();
        memberRepository.save(member);

        Milestone milestone = new Milestone();
        milestoneRepository.save(milestone);

        Task task = taskService.add("Task1", false, false, milestone.getId(), member.getId());
        Assertions.assertEquals("Task1", task.getName());
        Assertions.assertFalse(task.isFinished());
        Assertions.assertEquals(milestone.getId(), task.getMilestone().getId());
        Assertions.assertEquals(member.getId(), task.getResponsibleMember().getId());

        taskService.edit(task.getId(), "Task2", true, false);
        Task editedTask = taskService.getById(task.getId());
        Assertions.assertEquals("Task2", editedTask.getName());
        Assertions.assertTrue(editedTask.isFinished());

        Task task1 = taskService.add("Task1", false, false, milestone.getId(), member.getId());
        Assertions.assertEquals("Task2", taskService.getById(task.getId()).getName());

        taskService.removeById(task1.getId());
        taskService.removeById(task.getId());

        milestoneRepository.delete(milestone);
        memberRepository.delete(member);
        roadmapRepository.delete(roadmap);
        projectRepository.delete(project);
        userRepository.delete(user);
    }
}
