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
@Table(name = "tb_device")
public class Device extends BaseEntity {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "status", length = 200, nullable = false)
    private Status status = Status.Active;

    @Column(name = "temperature", length = 200, nullable = false)
    private String temperature;

    @Column(name = "PhProbe", length = 200, nullable = false)
    private String PhProbe;

    @Column(name = "Turbidity", length = 200, nullable = false)
    private String Turbidity;

    @ManyToOne
    private Property property;
}
