package de.telran.businesstracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    private boolean finished;
    private boolean active;

    @ManyToOne
    @Setter
    private Milestone milestone;

    @ManyToOne
    @Setter
    private Member responsibleMember;

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Resource> resources = new ArrayList<>();

    public Task(String name, boolean finished, boolean active, Milestone milestone, Member responsibleMember) {
        this.name = name;
        this.finished = finished;
        this.active = active;
        this.milestone = milestone;
        this.responsibleMember = responsibleMember;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return finished == task.finished && active == task.active && id.equals(task.id) && name.equals(task.name) && milestone.equals(task.milestone) && responsibleMember.equals(task.responsibleMember);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, finished, active, milestone, responsibleMember);
    }
}
