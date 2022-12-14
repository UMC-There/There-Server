package com.there.src.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class GetSearchByAccountRes {

    private int userIdx;
    private String name;
    private String nickName;
    private String profileImgUrl;

}
