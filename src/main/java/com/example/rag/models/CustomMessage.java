package com.example.rag.models;


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
public class CustomMessage implements Serializable {
    private String from_or_id;
    private String message_id;
    private String msg_body;
    private String message_type;
}
