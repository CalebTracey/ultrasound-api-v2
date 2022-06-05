package com.ultrasound.app.payload.response;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
