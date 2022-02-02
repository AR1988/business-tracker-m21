package de.telran.businesstracker.mapper;

import de.telran.businesstracker.controller.dto.MemberDto;
import de.telran.businesstracker.model.Member;
import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MemberMapperTest {
    @InjectMocks
    MemberMapper memberMapper;

    private Member member;

    @BeforeEach
    public void beforeEachTest() {
        User user = new User("Ivan", "Petrov", "Boss", "img-url");
        Project project = new Project("Some project", user);
        member = new Member(1L, project, user);
    }

    @Test
    public void mapMemberToTaskDto() {
        MemberDto memberDto = memberMapper.toDto(member);

        assertEquals(member.getId(), memberDto.id);
        assertEquals(member.getUser().getLastName(), memberDto.lastName);
        assertEquals(member.getUser().getName(), memberDto.name);
        assertEquals(member.getUser().getPosition(), memberDto.position);
        assertEquals(member.getUser().getImg(), memberDto.img);
        assertEquals(member.getProject().getId(), memberDto.projectId);
        assertEquals(member.getUser().getId(), memberDto.userId);
        assertEquals(7, MemberDto.class.getFields().length);
    }
}
