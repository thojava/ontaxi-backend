package vn.ontaxi.common.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Behavior implements Serializable, Comparable<Behavior> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Behavior{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Behavior that = (Behavior) obj;
        if (that.getId() == null || id == null)
            return false;
        return id.equals(that.getId());
    }

    @Override
    public int compareTo(Behavior o) {
        return this.name.compareTo(o.name);
    }
}
