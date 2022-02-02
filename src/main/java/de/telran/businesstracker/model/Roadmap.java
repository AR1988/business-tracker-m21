package de.telran.businesstracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private LocalDate startDate;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.REMOVE)
    private Set<Milestone> roadmaps = new LinkedHashSet<>();

    public Roadmap(String name, LocalDate startDate, Project project) {
        this.name = name;
        this.startDate = startDate;
        this.project = project;
    }

    public Roadmap(String name, Project project) {
        this.name = name;
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roadmap roadmap = (Roadmap) o;
        return id.equals(roadmap.id) && name.equals(roadmap.name) && Objects.equals(startDate, roadmap.startDate) && project.equals(roadmap.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, project);
    }
}
