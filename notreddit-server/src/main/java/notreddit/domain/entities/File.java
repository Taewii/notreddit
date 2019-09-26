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
    @Column(unique = true, nullable = false)
    private Long fileId;

    @Column
    private String thumbnailUrl;

    @NotNull
    @Column(nullable = false)
    private String url;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;
}
