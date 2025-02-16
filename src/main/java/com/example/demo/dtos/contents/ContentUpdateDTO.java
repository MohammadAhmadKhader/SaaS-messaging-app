package com.example.demo.dtos.contents;

import com.example.demo.models.Content;
import com.example.demo.models.enums.ContentType;
import com.example.demo.models.enums.Status;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ContentUpdateDTO(
    @Size(max = 128, message = "title must be at most {max}")
    @Size(min = 5, message = "title must be at least {min}")
    String title,

    @Size(max = 45, message = "description must be at most {max}")
    @Size(min = 5, message = "description must be at least {min}")
    String description,

    @Min(value = 1, message = "user id must be at least {value}")
    Long userId,
    
    Status status,
    ContentType contentType,
    String url
){

    public Content toModel(){
        return new Content(this.title, this.description, this.status, this.contentType, this.url);
    }
}
