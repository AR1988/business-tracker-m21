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
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private Integer hours;
    @Setter
    private Double cost;

    @ManyToOne
    private Task task;

    public Resource(String name, Integer hours, Double cost, Task task) {
        this.name = name;
        this.hours = hours;
        this.cost = cost;
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return id.equals(resource.id) && name.equals(resource.name) && hours.equals(resource.hours) && cost.equals(resource.cost) && task.equals(resource.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, hours, cost, task);
    }
}
