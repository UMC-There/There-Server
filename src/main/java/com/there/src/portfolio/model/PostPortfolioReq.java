package com.there.src.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PostPortfolioReq {

    private String title;
    private int[] postIdx = null;

}
