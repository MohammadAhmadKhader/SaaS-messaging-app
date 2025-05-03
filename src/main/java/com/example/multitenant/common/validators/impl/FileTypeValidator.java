package com.example.multitenant.common.validators.impl;

import jakarta.validation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.common.validators.contract.AllowedFileTypes;

import java.util.Arrays;
import java.util.List;

public class FileTypeValidator implements ConstraintValidator<AllowedFileTypes, MultipartFile> {

    private List<String> allowedTypes;

    @Override
    public void initialize(AllowedFileTypes constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.types());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file == null || allowedTypes.contains(file.getContentType());
    }
}