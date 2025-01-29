package com.example.rag.chat_controller;


import lombok.*;

@Data
@NoArgsConstructor
public class Message {
    private String from;
    private String msg_id;
    private String body;
    private String msg_type;

    public Message(String from,String msg_id,String body,String msg_type)
    {
        this.msg_type=msg_type;
        this.body=body;
        this.from=from;
        this.msg_id=msg_id;
    }

}
