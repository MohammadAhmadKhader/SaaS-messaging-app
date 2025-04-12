package com.example.demo.controllers.dashboard;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.resolvers.contract.HandlePage;
import com.example.demo.common.resolvers.contract.HandleSize;
import com.example.demo.common.validators.contract.ValidateNumberId;
import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.services.contents.ContentsService;

import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/dashboard/contents")
public class AppDashboardContentsController {
    private final ContentsService contentsService;
    public AppDashboardContentsController(ContentsService contentsService) {
        this.contentsService = contentsService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('app-dashboard:content:view')")
    public ResponseEntity<Object> getAllContents(@HandlePage Integer page, @HandleSize Integer size,
        @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir, 
        @RequestParam(defaultValue = "") List<String> filters) {
    
        var contents = this.contentsService.findAllPopulatedWithFilters(page, size, sortBy, sortBy, filters, null);
        var count = contents.getTotalElements();
        var contentsViews = contents.map((con) -> {
            return con.toViewDTO();
        }).toList();
        
        var bodyResponse = ApiResponses.GetAllResponse("contents", contentsViews, count, page, size);
        
        return ResponseEntity.ok(bodyResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('app-dashboard:content:view')")
    public ResponseEntity<Object> getContentById(@ValidateNumberId @PathVariable Integer id) {
        var content = this.contentsService.findById(id);
        if(content == null) {
            var respBody = ApiResponses.GetErrResponse(String.format("content with id: %s was not found", id));
            return ResponseEntity.badRequest().body(respBody);
        }

        var bodyResponse = ApiResponses.OneKey("content",content.toViewDTO());

        return ResponseEntity.ok(bodyResponse);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('app-dashboard:content:delete')")
    public ResponseEntity<Object> delete(@ValidateNumberId @PathVariable Integer id) {
        var isDeleted = this.contentsService.findThenDeleteById(id);
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("content", id));
        }

        return ResponseEntity.noContent().build();
    }
}
