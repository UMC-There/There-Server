package com.there.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class GetRoomInfoRes {

    private int roomIdx;
    private int count;

}
