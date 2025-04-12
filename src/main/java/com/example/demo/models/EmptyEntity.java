package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * This is a Dummy Entity, not used for anything but to pass an error.
 * {@see UnImplementedRepository} for more explanation.
 */
@Entity
public class EmptyEntity {
    @Id
    private Long id;
}