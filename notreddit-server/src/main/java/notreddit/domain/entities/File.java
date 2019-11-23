package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File {

    @Id
    private UUID id;

    @Column
    private String thumbnailUrl;

    @NotNull
    @Column(nullable = false)
    private String url;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;
}
