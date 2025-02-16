package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.contents.ContentCreateDTO;
import com.example.demo.dtos.contents.ContentUpdateDTO;
import com.example.demo.services.contents.ContentsService;
import com.example.demo.services.users.UsersService;

import jakarta.validation.Valid;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    public ResponseEntity<Object> getAllContents(
        @RequestParam(name ="page",defaultValue = "1") Integer page, @RequestParam(name ="size",defaultValue = "9") Integer size,
        @RequestParam(name ="sortBy", defaultValue = "createdAt") String sortBy, @RequestParam(name= "sortDir", defaultValue = "DESC") String sortDir, 
        @RequestParam(name = "filters", defaultValue = "") List<String> filters) {
        
        var start = Instant.now();
        var result = contentsService.findAllPopulatedWithFilters(page, size, sortBy, sortBy, filters);
        var bodyResponse = ApiResponses.GetAllResponse(result,"contents");
        var end = Instant.now();
        System.out.println(String.format("Request has taken: '%s'ms", Duration.between(start, end).toMillis()));

        return ResponseEntity.ok(bodyResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getContentById(@PathVariable Integer id) {
        var content = contentsService.findByIdAsView(id);
        if(content == null) {
            var respBody = ApiResponses.GetErrResponse(String.format("Content with id: %s", id));
            return ResponseEntity.badRequest().body(respBody);
        }

        var bodyResponse = ApiResponses.OneKey("content",content);

        return ResponseEntity.ok(bodyResponse);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> getContentsByUserId(
        @PathVariable Long userId, @RequestParam(name ="page",defaultValue = "1") Integer page, 
        @RequestParam(name ="size",defaultValue = "9") Integer size) {
        
        var result = contentsService.findContentsByUserId(page, size,userId);
        var bodyResponse = ApiResponses.GetAllResponse(result,"contents");
        
        return ResponseEntity.ok().body(bodyResponse);
    }

    @PostMapping("")
    public ResponseEntity<Object> createContent(@Valid @RequestBody ContentCreateDTO dto) {
        var content = this.contentsService.createByUser(dto.toModel());
        var contentView = content.toViewDTO();
        var responseBody = ApiResponses.OneKey("content", contentView);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateContent(@PathVariable Integer id, @Valid @RequestBody ContentUpdateDTO dto) {
        var updatedContent = contentsService.updateByUser(id, dto.toModel());
        if(updatedContent == null) {
            var respBody = ApiResponses.GetErrResponse(String.format("content with id: '%s' does not exist", id));
            return ResponseEntity.badRequest().body(respBody);
        } 

        var responseBody = ApiResponses.OneKey("content", updatedContent.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        contentsService.deleteByUser(id);
        return ResponseEntity.noContent().build();
    }
    
}
