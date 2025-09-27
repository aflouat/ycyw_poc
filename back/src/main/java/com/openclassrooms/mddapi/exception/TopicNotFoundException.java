package com.openclassrooms.mddapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(String topicNotFound) {
        super(topicNotFound);
    }
}

