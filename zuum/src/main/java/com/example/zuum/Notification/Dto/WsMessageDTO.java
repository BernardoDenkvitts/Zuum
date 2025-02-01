package com.example.zuum.Notification.Dto;

public class WsMessageDTO {
    WsMessageType type; 
    Object data;
    
    public WsMessageDTO(WsMessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public WsMessageType getType() {
        return type;
    }

    public void setType(WsMessageType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    

}
