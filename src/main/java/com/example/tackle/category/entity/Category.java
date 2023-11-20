package com.example.tackle.category.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    private Long categoryId;

    private String categoryName;


}
