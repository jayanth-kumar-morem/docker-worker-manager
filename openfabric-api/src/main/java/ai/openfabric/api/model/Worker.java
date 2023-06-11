package ai.openfabric.api.model;


import ai.openfabric.api.enums.ContainerState;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity()
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Worker extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    public String id;

    public String workerName;

    private String dockerImageName;

    private String dockerImageId;

    private ContainerState status;

    private Long port;
}