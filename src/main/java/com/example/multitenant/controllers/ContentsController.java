package com.example.multitenant.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.annotations.contract.AuthorizeOrg;
import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.common.validators.contract.ValidateSize;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.contents.ContentCreateDTO;
import com.example.multitenant.dtos.contents.ContentUpdateDTO;
import com.example.multitenant.services.contents.ContentsService;
import com.example.multitenant.services.users.UsersService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RestController
@RequestMapping("/api/contents")
public class ContentsController {

    private final ContentsService contentsService;
    private final UsersService usersService;

    public ContentsController(ContentsService contentsService, UsersService usersService) {
        this.contentsService = contentsService;
        this.usersService = usersService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllContents(@HandlePage Integer page, @HandleSize Integer size,
        @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir, 
        @RequestParam(defaultValue = "") List<String> filters, @RequestHeader("X-Tenant-ID") String tenantId) {

        var contents = this.contentsService.findAllPopulatedWithFilters(page, size, sortBy, sortBy, filters, Integer.parseInt(tenantId));
        var count = contents.getTotalElements();
        var contentsViews = contents.map((con) -> {
            return con.toViewDTO();
        }).toList();

        var bodyResponse = ApiResponses.GetAllResponse("contents", contentsViews, count, page, size);

        return ResponseEntity.ok(bodyResponse);
    }

    @GetMapping("/{id}")
    @AuthorizeOrg({""})
    public ResponseEntity<Object> getContentById(@PathVariable @ValidateNumberId Integer id, @RequestHeader("X-Tenant-ID") String tenantId) {
        var content = this.contentsService.findById(id, Integer.parseInt(tenantId));
        if(content == null) {
            var respBody = ApiResponses.GetErrResponse(String.format("content with id: %s was not found", id));
            return ResponseEntity.badRequest().body(respBody);
        }

        var bodyResponse = ApiResponses.OneKey("content",content.toViewDTO());

        return ResponseEntity.ok(bodyResponse);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> getContentsByUserId(
        @PathVariable @ValidateNumberId Long userId, @RequestParam(defaultValue = "1") @Min(value = 1 ,message = "page must be at least {value}") Integer page, 
        @RequestParam(defaultValue = "9") Integer size, @RequestHeader("X-Tenant-ID") String tenantId) {
        
        var contents = this.contentsService.findContentsByUserId(page, size,userId, Integer.parseInt(tenantId));
        var count = contents.getTotalElements();
        var contentsViews = contents.stream().map((con) -> {
            return con.toViewDTO();
        }).toList();

        var bodyResponse = ApiResponses.GetAllResponse("contents", contentsViews, count, page, size);
        
        return ResponseEntity.ok().body(bodyResponse);
    }

    @PostMapping("")
    public ResponseEntity<Object> createContent(@Valid @RequestBody ContentCreateDTO dto, @RequestHeader("X-Tenant-ID") String tenantId) {
        var content = this.contentsService.createByUser(dto.toModel(), Integer.parseInt(tenantId));
        var contentView = content.toViewDTO();
        var responseBody = ApiResponses.OneKey("content", contentView);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateContent(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody ContentUpdateDTO dto, @RequestHeader("X-Tenant-ID") String tenantId) {
        var updatedContent = this.contentsService.updateByUser(id, dto.toModel(), Integer.parseInt(tenantId));
        if(updatedContent == null) {
            var respBody = ApiResponses.GetErrResponse(String.format("content with id: '%s' does not exist", id));
            return ResponseEntity.badRequest().body(respBody);
        } 

        var responseBody = ApiResponses.OneKey("content", updatedContent.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id, @RequestHeader("X-Tenant-ID") String tenantId) {
        this.contentsService.deleteByUser(id, Integer.parseInt(tenantId));
        return ResponseEntity.noContent().build();
    }
    
}
