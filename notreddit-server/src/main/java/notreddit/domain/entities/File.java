package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File extends BaseUUIDEntity {

    @NotNull
    @Column(nullable = false, unique = true)
    private long fileId;

    @Column(nullable = false, unique = true)
    private String url;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Post post;
}
