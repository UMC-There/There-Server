package com.there.src.post.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPostsRes {

    private int postIdx;
    private String profileImgUrl;
    private String nickName;
    private String imgUrl;
    private String content;
    private int likeCount;
}
