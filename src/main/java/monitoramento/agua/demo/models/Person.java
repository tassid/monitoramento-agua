package monitoramento.agua.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_person")
@Getter
@Setter
public class Person extends BaseEntity {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "role")
    private String role;

}
