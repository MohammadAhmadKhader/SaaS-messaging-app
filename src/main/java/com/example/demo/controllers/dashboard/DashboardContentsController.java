package com.example.demo.controllers.dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.contents.ContentsService;
import com.example.demo.services.users.UsersService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardContentsController {

    private final ContentsService contentsService;
    private final UsersService usersService;

    public DashboardContentsController(ContentsService contentsService,UsersService usersService) {
        this.contentsService = contentsService;
        this.usersService = usersService;
    }

    // @GetMapping("")
    // public String getContents() {
    //     var s = "";
    //     this.contentsService.
    //     return new String();
    // }
    
}