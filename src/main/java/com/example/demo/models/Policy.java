package com.example.demo.models;

import org.hibernate.annotations.ManyToAny;

import com.example.demo.models.enums.PolicyEffect;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

// @Getter
// @Setter
// public class Policy {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     // attribute
//     // value

//     @OneToOne
//     @JoinColumn(name = "resource_id", nullable = false)
//     private Resource resource;

//     @OneToOne(cascade = CascadeType.ALL)
//     @JoinColumn(name = "action_id", nullable = false)
//     private Permission action;

//     @ManyToOne
//     @JoinColumn(name = "organization_id", nullable = false)
//     private Organization organization;
    
//     @Enumerated(EnumType.STRING)
//     @Column(name = "effect")
//     private PolicyEffect effect;
// }
