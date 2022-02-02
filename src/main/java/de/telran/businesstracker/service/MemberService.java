package de.telran.businesstracker.service;

import de.telran.businesstracker.model.Member;
import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.User;
import de.telran.businesstracker.repositories.MemberRepository;
import de.telran.businesstracker.repositories.ProjectRepository;
import de.telran.businesstracker.repositories.UserRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class MemberService {

    static final String PROJECT_DOES_NOT_EXIST = "Error! This project doesn't exist in our DB";
    static final String USER_DOES_NOT_EXIST = "Error! This user doesn't exist in our DB";
    static final String MEMBER_DOES_NOT_EXIST = "Error! This member doesn't exist in our DB";
    static final String MEMBER_ALREADY_EXIST = "Error! This member already exist by this project";

    final MemberRepository memberRepository;
    final ProjectRepository projectRepository;
    final UserRepository userRepository;

    public MemberService(MemberRepository memberRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Member add(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_DOES_NOT_EXIST));

        for (Member projectMember : project.getMembers())
            if (projectMember.getUser().getId().equals(userId)) throw new EntityExistsException(MEMBER_ALREADY_EXIST);

        Member member = new Member(project, user);
        return memberRepository.save(member);

    }

    public List<Member> getAllByProjectId(long projectId) {
        Project project = getProjectById(projectId);
        return memberRepository.findAllByProject(project);
    }

    public Member getById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MEMBER_DOES_NOT_EXIST));
    }

    public void removeById(Long id) {
        memberRepository.deleteById(id);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException(PROJECT_DOES_NOT_EXIST));
    }
}



