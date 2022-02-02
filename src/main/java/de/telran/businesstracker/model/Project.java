package de.telran.businesstracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Set<Roadmap> roadmaps = new LinkedHashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Set<Member> members = new LinkedHashSet<>();

    public Project(String name, User user) {
        this.name = name;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id.equals(project.id) && name.equals(project.name) && user.equals(project.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, user);
    }
}
