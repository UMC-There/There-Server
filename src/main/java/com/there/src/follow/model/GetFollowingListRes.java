package com.there.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class GetFollowingListRes {
    private String nickName;
    private String profileImgUrl;
}
