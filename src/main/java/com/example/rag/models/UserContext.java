package com.example.rag.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonDeserialize
@Getter
@Setter
public class UserContext implements Serializable {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("_rev")
    private String rev;
    @JsonProperty("age")
    private String age;
}
