package de.telran.businesstracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private LocalDate startDate;
    @Setter
    private LocalDate finishDate;

    @Setter
    @ManyToOne
    private Roadmap roadmap;

    @Setter
    @ElementCollection
    private List<String> kpis = new ArrayList<>();

    @OneToMany(mappedBy = "milestone", cascade = CascadeType.REMOVE)
    private Set<Task> tasks = new LinkedHashSet<>();

    public Milestone(String name, LocalDate startDate, LocalDate finishDate, Roadmap roadmap, List<String> kpis) {
        this.name = name;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.roadmap = roadmap;
        this.kpis = kpis;
    }

    public Milestone(String name, Roadmap roadmap, List<String> kpis) {
        this.name = name;
        this.roadmap = roadmap;
        this.kpis = kpis;
    }

    public void addKpi(String kpi) {
        kpis.add(kpi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Milestone milestone = (Milestone) o;
        return id.equals(milestone.id) && name.equals(milestone.name) && Objects.equals(startDate, milestone.startDate) && Objects.equals(finishDate, milestone.finishDate) && roadmap.equals(milestone.roadmap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, roadmap);
    }
}
