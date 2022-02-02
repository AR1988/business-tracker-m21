package de.telran.businesstracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private String lastName;
    @Setter
    private String position;
    @Setter
    private String img;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<Project> projects = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Member> members = new ArrayList<>();

    public User(String name, String lastName, String position, String img) {
        this.name = name;
        this.lastName = lastName;
        this.position = position;
        this.img = img;
    }

    public User(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && name.equals(user.name) && lastName.equals(user.lastName) && Objects.equals(position, user.position) && Objects.equals(img, user.img);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName);
    }
}
