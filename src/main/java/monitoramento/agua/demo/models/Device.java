package monitoramento.agua.demo.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column(name = "phProbe", length = 200, nullable = false)
    private String phProbe;

    @Column(name = "turbidity", length = 200, nullable = false)
    private String turbidity;

    @ManyToOne
    private Property property;

    @JsonIgnore
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors;
}
