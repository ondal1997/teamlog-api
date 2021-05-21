package com.test.teamlog.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 1000, nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "writer_user_id", nullable = false)
    private User writer;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "access_modifier",nullable = false, columnDefinition = "TINYINT(1)")
    private AccessModifier accessModifier;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "comment_modifier",nullable = false, columnDefinition = "TINYINT(1)")
    private AccessModifier commentModifier;

    private Point location;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostMedia> media;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostTag> hashtags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true) // 애는 삭제할까말까..
    @BatchSize(size = 10)
    private List<PostLiker> postLikers;

    public void addHashTags(List<PostTag> tags)
    {
        this.hashtags.addAll(tags);
    }

    public void removeHashTags(List<PostTag> tags)
    {
        this.hashtags.removeAll(tags);
    }

    public void setProject(Project project) {
        if(this.project != null) {
            this.project.getPosts().remove(this);
        }
        this.project = project;
        project.getPosts().add(this);
    }
}
