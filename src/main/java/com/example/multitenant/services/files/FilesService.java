package com.example.multitenant.services.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.config.SupabaseConfig;
import com.example.multitenant.dtos.files.FileResponse;
import com.example.multitenant.exceptions.AppFilesException;
import com.example.multitenant.models.enums.FilesPath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilesService {
    private final SupabaseConfig storageConfig;
    
    public FileResponse uploadFile(MultipartFile file, FilesPath path) {
        try {
            var fileName = this.generateUniqueFileName(file.getOriginalFilename());
            log.info("file original name {}", file.getOriginalFilename());
            var fullPath = path.getValue() + "/" + fileName;
            log.info("fullPath {}", fullPath);
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                var uploadUrl = this.storageConfig.getSupabaseUrl() + "/storage/v1/object/" + this.storageConfig.getBucketName() + fullPath;
                log.info("upload Url {}", uploadUrl);
                var uploadRequest = new HttpPost(uploadUrl);
                uploadRequest.setHeader("Authorization", "Bearer " +this.storageConfig.getJwtSecret());
                uploadRequest.setHeader("Content-Type", file.getContentType());
                
                var builder = MultipartEntityBuilder.create();
                builder.addBinaryBody(
                    "file", 
                    file.getInputStream(), 
                    ContentType.parse(file.getContentType()), 
                    fileName
                );

                var fileEntity = new InputStreamEntity(file.getInputStream(), file.getSize());
                uploadRequest.setEntity(fileEntity);
                
                try (var response = httpClient.execute(uploadRequest)) {
                    var statusCode = response.getStatusLine().getStatusCode();
                    
                    if (statusCode >= 200 && statusCode < 300) {
                        var publicUrl = this.storageConfig.getSupabaseUrl() + "/storage/v1/object/public/" + this.storageConfig.getBucketName() + fullPath;
                        
                        return new FileResponse(
                            fileName,
                            file.getContentType(),
                            file.getSize(),
                            publicUrl
                        );
                    } else {
                        var responseBody = EntityUtils.toString(response.getEntity());
                        throw new AppFilesException("failed to upload file", statusCode, responseBody);
                    }
                }
            }
        } catch (IOException e) {
            throw new AppFilesException("failed to upload file " + file.getOriginalFilename(), e);
        }
    }

    public List<FileResponse> uploadMultipleFiles(List<MultipartFile> files, FilesPath path) {
        var responses = new ArrayList<FileResponse>();
        
        for (MultipartFile file : files) {
            responses.add(this.uploadFile(file, path));
        }
        
        return responses;
    }
    
    public boolean deleteFile(FilesPath path, String url) {
        var fileName = this.getFileNameFromUrl(url);
        var fullPath = path.getValue() + "/" + fileName;
        var deleteUrl = this.storageConfig.getSupabaseUrl() + "/storage/v1/object/" + this.storageConfig.getBucketName() + fullPath;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {  
            var deleteRequest = new HttpDelete(deleteUrl);
            deleteRequest.setHeader("Authorization", "Bearer " + this.storageConfig.getJwtSecret());
            
            try (CloseableHttpResponse response = httpClient.execute(deleteRequest)) {
                var statusCode = response.getStatusLine().getStatusCode();
                
                if (statusCode >= 200 && statusCode < 300) {
                    return true;
                } else {
                    var responseBody = EntityUtils.toString(response.getEntity());
                    throw new AppFilesException("failed to delete file", statusCode, responseBody);
                }
            }
        } catch (Exception e) {
            throw new AppFilesException("failed to delete file " + fullPath, e);
        }
    }

    public FileResponse updateFile(MultipartFile newFile, FilesPath path, String fileUrl) {
        try {
            if(!fileUrl.contains(storageConfig.getBucketName())) {
                throw new AppFilesException(String.format("invalid file url received '%s'", fileUrl));
            }

            var fileName = this.getFileNameFromUrl(fileUrl);
            var fullPath = path.getValue() + "/" + fileName;

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                var updateUrl = storageConfig.getSupabaseUrl()+ "/storage/v1/object/" + storageConfig.getBucketName() + fullPath;
                var put = new HttpPut(updateUrl);

                put.setHeader("Authorization", "Bearer " + storageConfig.getJwtSecret());
                put.setHeader("Content-Type", newFile.getContentType());

                var entity = new InputStreamEntity(newFile.getInputStream(), newFile.getSize());
                put.setEntity(entity);

                try (CloseableHttpResponse resp = httpClient.execute(put)) {
                    var statusCode = resp.getStatusLine().getStatusCode();
                    var responseBody = EntityUtils.toString(resp.getEntity());

                    if (statusCode >= 200 && statusCode < 300) {
                        var publicUrl = storageConfig.getSupabaseUrl() + "/storage/v1/object/public/" + storageConfig.getBucketName() + fullPath;
                        
                        log.info("[Files Service] response: {}, status: {}", responseBody, statusCode);
                        return new FileResponse(
                            fileUrl,
                            newFile.getContentType(),
                            newFile.getSize(),
                            publicUrl
                        );
                    } else {
                        throw new AppFilesException("failed to update file", statusCode, responseBody);
                    }
                }
            }
        } catch (IOException e) {
            throw new AppFilesException("failed to update file " + fileUrl, e);
        }
    }

    public String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        var lastSlashIndex = url.lastIndexOf('/');
        return (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) ? url.substring(lastSlashIndex + 1) : null;
    }
    
    private String generateUniqueFileName(String originalFilename) {
        var uuid = UUID.randomUUID().toString();
        var lastDotIndex = originalFilename.lastIndexOf(".");
        
        if (lastDotIndex > 0) {
            var extension = originalFilename.substring(lastDotIndex);
            return uuid + extension;
        }
        
        return uuid;
    }
}