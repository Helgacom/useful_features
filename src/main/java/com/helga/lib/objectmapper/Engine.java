package com.helga.lib.objectmapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Engine {
    private String name;
    private String[] voices;
    private String[] sampleRates;
    private int maxLength;
}
