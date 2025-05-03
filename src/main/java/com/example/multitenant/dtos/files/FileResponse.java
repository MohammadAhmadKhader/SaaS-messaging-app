package com.example.multitenant.dtos.files;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class FileResponse {
    private String fileName;
    private String contentType;
    private long size;
    private String url;
    private LocalDateTime uploadTime;

    public FileResponse() {
        this.uploadTime = LocalDateTime.now();
    }

    public FileResponse(String fileName, String contentType, long size, String url) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.url = url;
        this.uploadTime = LocalDateTime.now();
    }
}
