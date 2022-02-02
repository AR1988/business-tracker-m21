package de.telran.businesstracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private Project project;

    @ManyToOne
    @Setter
    private User user;

    public Member(Project project, User user) {
        this.project = project;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id.equals(member.id) && project.equals(member.project) && user.equals(member.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, project, user);
    }
}
