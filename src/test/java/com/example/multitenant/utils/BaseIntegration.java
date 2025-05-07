package com.example.multitenant.utils;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BaseIntegration extends BaseTests {
    
}
