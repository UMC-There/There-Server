package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetChatContentRes {

    private int userIdx;
    private String content;
    private String created_At;
}
