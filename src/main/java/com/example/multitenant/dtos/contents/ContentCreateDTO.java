package com.example.multitenant.dtos.contents;



import com.example.multitenant.models.Content;
import com.example.multitenant.models.enums.ContentType;
import com.example.multitenant.models.enums.Status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContentCreateDTO(
    @NotBlank(message = "title can not be empty")
    @Size(max = 128, message = "title must be at most {max}")
    @Size(min = 5, message = "title must be at least {min}")
    String title,

    @NotBlank(message = "description can not be empty")
    @Size(max = 45, message = "description must be at most {max}")
    @Size(min = 5, message = "description must be at least {min}")
    String description,
    Status status,

    @Min(value = 1, message = "user id must be at least {value}")
    Long userId,
    
    @NotNull(message = "content type can not be nullable")
    ContentType contentType,

    @Size(max = 256, message = "url length be at most {max}")
    String url
) {
    public ContentCreateDTO {
        if(status == null) {
            status = Status.IDEA;
        }
        if(url == null) {
            url = "";
        }
    }

    public Content toModel(){
        return new Content(this.title, this.description, this.status, this.contentType, this.url);
    }
}
