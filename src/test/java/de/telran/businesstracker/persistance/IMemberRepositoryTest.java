package de.telran.businesstracker.persistance;

import de.telran.businesstracker.model.Member;
import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.Roadmap;
import de.telran.businesstracker.model.User;
import de.telran.businesstracker.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class IMemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private User user;
    private Roadmap roadmap;
    private Project project;

    private User user2;
    private Project project2;

    @BeforeEach
    public void beforeEachTest() {
        user = new User("Ivan", "Petrov", "Boss", "img-url");
        project = new Project("Great project", user);
        roadmap = new Roadmap("Roadmap", LocalDate.now(), project);
        Member member = new Member(project, user);

        user2 = new User("2 Ivan", "2 Petrov", "2 Boss", "2 img-url");
        project2 = new Project("2 Great project", user2);
    }

    @Test
    public void testFindByProject_oneProject_fourRecordsFound() {

        entityManager.persist(user);
        entityManager.persist(project);
        entityManager.persist(roadmap);

        List<Member> members = Arrays.asList(
                new Member(project, user),
                new Member(project, user),
                new Member(project, user),
                new Member(project, user)
        );

        members.forEach(member -> entityManager.persist(member));

        entityManager.flush();
        entityManager.clear();

        List<Member> foundMembers = memberRepository.findAllByProject(project);
        assertEquals(4, foundMembers.size());

        assertEquals(members.get(0).getUser().getName(), foundMembers.get(0).getUser().getName());
        assertEquals(members.get(1).getUser().getName(), foundMembers.get(1).getUser().getName());
        assertEquals(members.get(2).getUser().getName(), foundMembers.get(2).getUser().getName());
        assertEquals(members.get(3).getUser().getName(), foundMembers.get(3).getUser().getName());
    }

    @Test
    public void testFindByProjectAndActive_twoProject_twoRecordsFound() {

        entityManager.persist(user);
        entityManager.persist(user2);

        entityManager.persist(project);
        entityManager.persist(project2);

        List<Member> members = Arrays.asList(
                new Member(project, user2),
                new Member(project, user),
                new Member(project2, user),
                new Member(project2, user2)
        );

        members.forEach(member -> entityManager.persist(member));

        entityManager.flush();
        entityManager.clear();

        List<Member> foundMembers = memberRepository.findAllByProject(project);
        assertEquals(2, foundMembers.size());

        assertEquals(members.get(0).getUser().getName(), foundMembers.get(0).getUser().getName());
        assertEquals(members.get(1).getUser().getName(), foundMembers.get(1).getUser().getName());
    }

    @Test
    public void testFindByProjectAndActive_twoProject_noRecordsFound() {

        entityManager.persist(user);
        entityManager.persist(project);
        entityManager.persist(user2);
        entityManager.persist(project2);

        Member member = new Member(project2, user2);

        entityManager.persist(member);

        entityManager.flush();
        entityManager.clear();

        List<Member> foundMembers = memberRepository.findAllByProject(project);
        assertEquals(0, foundMembers.size());
    }
}
