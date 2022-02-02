package de.telran.businesstracker.service;

import de.telran.businesstracker.model.Member;
import de.telran.businesstracker.model.Milestone;
import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.Task;
import de.telran.businesstracker.repositories.MemberRepository;
import de.telran.businesstracker.repositories.MilestoneRepository;
import de.telran.businesstracker.repositories.ProjectRepository;
import de.telran.businesstracker.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TaskService {

    private final String PROJECT_DOES_NOT_EXIST = "Error! This project doesn't exist in our DB";
    static final String MEMBER_DOES_NOT_EXIST = "Error! This member doesn't exist in our DB";
    static final String MILESTONE_DOES_NOT_EXIST = "Error! This milestone doesn't exist in our DB";
    static final String TASK_DOES_NOT_EXIST = "Error! This task doesn't exist in our DB";

    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, MemberRepository memberRepository, MilestoneRepository milestoneRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.memberRepository = memberRepository;
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
    }

    public Task add(String name, boolean finished, boolean active, Long milestoneId, Long memberId) {
        Member responsibleMember = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_DOES_NOT_EXIST));
        Milestone milestone = milestoneRepository.findById(milestoneId).orElseThrow(() -> new EntityNotFoundException(MILESTONE_DOES_NOT_EXIST));

        Task task = new Task(name, finished, active, milestone, responsibleMember);

        return taskRepository.save(task);
    }

    public void edit(Long id, String name, boolean finished, boolean active) {
        Task task = getById(id);

        task.setName(name);
        task.setFinished(finished);
        task.setActive(active);

        taskRepository.save(task);
    }

    public Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(TASK_DOES_NOT_EXIST));
    }

    public void removeById(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getAllActiveTasksByProjectId(long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException(PROJECT_DOES_NOT_EXIST));
        return taskRepository.findAllByMilestone_Roadmap_ProjectAndActiveTrue(project);
    }

    public List<Task> getAllByMileStoneId(long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId).orElseThrow(() -> new EntityNotFoundException(MILESTONE_DOES_NOT_EXIST));
        return taskRepository.findAllByMilestone(milestone);
    }
}



