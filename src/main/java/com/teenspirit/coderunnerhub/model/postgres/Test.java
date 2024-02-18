package com.teenspirit.coderunnerhub.model.postgres;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_test")
@NoArgsConstructor
@Getter
@Setter
public class Test {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "task_id")
    private int taskId;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Column(columnDefinition = "boolean default false")
    private boolean deleted;
}
