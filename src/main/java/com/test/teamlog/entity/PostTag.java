package com.test.teamlog.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "post_tag")
public class PostTag {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    public void setPost(Post post) {
        if(this.post != null) {
            this.post.getHashtags().remove(this);
        }
        this.post = post;
        post.getHashtags().add(this);
    }
}
