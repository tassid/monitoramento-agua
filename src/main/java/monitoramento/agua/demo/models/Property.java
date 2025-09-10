package monitoramento.agua.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_property")
public class Property extends BaseEntity {

    @Column(name = "areaHa", length = 255, nullable = false)
    private String areaHa;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @ManyToOne
    private Person person;
}
