package de.telran.businesstracker.service;

import de.telran.businesstracker.model.Member;
import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.User;
import de.telran.businesstracker.repositories.MemberRepository;
import de.telran.businesstracker.repositories.ProjectRepository;
import de.telran.businesstracker.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    MemberService memberService;

    private Project project;
    private User user;
    private User user2;
    private Member member;

    @BeforeEach
    public void beforeEachTest() {
        user = new User(5L, "Ivan", "Petrov", "Boss", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        user2 = new User(6L, "Vasja", "Pupkin", "Dev", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        project = new Project(4L, "Great project", user, new LinkedHashSet<>(), new LinkedHashSet<>());
        member = new Member(10L, project, user);
    }

    @Test
    public void testAdd_success() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        memberService.add(member.getProject().getId(), member.getUser().getId());

        verify(memberRepository, times(1)).save(any());
        verify(memberRepository, times(1)).save(argThat(savedMember ->
                savedMember.getUser().getPosition().equals(member.getUser().getPosition()) &&
                        savedMember.getProject().getId().equals(project.getId()) &&
                        savedMember.getUser().getId().equals(user.getId()))
        );
    }

    @Test
    public void testAdd_projectDoesNotExist_EntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                memberService.add(100L, member.getUser().getId()));

        verify(projectRepository, times(1)).findById(any());
        assertEquals("Error! This project doesn't exist in our DB", exception.getMessage());
    }


    @Test
    void testGetById_objectExist() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        Member expectedMember = memberService.getById(member.getId());

        assertEquals(expectedMember.getUser().getPosition(), member.getUser().getPosition());
        assertEquals(expectedMember.getProject(), member.getProject());
        assertEquals(expectedMember.getUser(), member.getUser());

        verify(memberRepository, times(1)).findById(argThat(
                id -> id.equals(member.getId())));
    }

    @Test
    void testGetById_objectNotExist() {
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> memberService.getById(member.getId() + 1));

        verify(memberRepository, times(1)).findById(any());
        assertEquals("Error! This member doesn't exist in our DB", exception.getMessage());

    }

    @Test
    void testGetMembersByProjectId_3Found() {
        List<Member> members = Arrays.asList(
                member,
                new Member(2L, project, user),
                new Member(3L, project, user2)
        );
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(memberRepository.findAllByProject(project)).thenReturn(members);

        List<Member> membersResult = memberService.getAllByProjectId(project.getId());

        verify(projectRepository, times(1)).findById(project.getId());
        verify(memberRepository, times(1)).findAllByProject(project);

        assertEquals(members.size(), membersResult.size());
        assertTrue(membersResult.contains(member));

        assertEquals(2L, membersResult.get(1).getId());
        assertEquals(user.getName(), membersResult.get(1).getUser().getName());
        assertEquals(user.getLastName(), membersResult.get(1).getUser().getLastName());
        assertEquals(user.getPosition(), membersResult.get(1).getUser().getPosition());

        assertEquals(3L, membersResult.get(2).getId());
        assertEquals(user2.getName(), membersResult.get(2).getUser().getName());
        assertEquals(user2.getLastName(), membersResult.get(2).getUser().getLastName());
        assertEquals(user2.getPosition(), membersResult.get(2).getUser().getPosition());
    }

    @Captor
    ArgumentCaptor<Member> taskArgumentCaptor;

    @Test
    void removeById_oneObjectDeleted() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        memberService.add(member.getProject().getId(), member.getUser().getId());
        memberService.removeById(member.getId());

        List<Member> capturedMembers = taskArgumentCaptor.getAllValues();
        verify(memberRepository, times(1)).deleteById(member.getId());
        assertEquals(0, capturedMembers.size());
    }
}
