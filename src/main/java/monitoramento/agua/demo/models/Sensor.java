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
@Table(name = "tb_sensor")
public class Sensor extends BaseEntity {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "sensor", length = 200, nullable = false)
    private SensorType sensorType = SensorType.Temperature;

    @ManyToOne
    private Device device;
}
