package ai.openfabric.api.model;


import ai.openfabric.api.enums.ContainerStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity()
@Builder
@Data
public class Worker extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    public String id;

    @ElementCollection
    public List<String> workerNames;

    private String dockerImageName;

    private String dockerImageId;

    private ContainerStatus status;

    private Long created;

    private Long port;
}