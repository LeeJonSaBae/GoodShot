package com.d201.goodshot.swing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Similarity {

    private double address;
    private double finish;
    private double impact;
    private double midBackSwing;
    private double midDownSwing;
    private double midFollowThrough;
    private double toeUp;
    private double top;

}

