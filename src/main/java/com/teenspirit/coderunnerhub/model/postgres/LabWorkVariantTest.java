package com.teenspirit.coderunnerhub.model.postgres;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lab_work_variant_test")
public class LabWorkVariantTest {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "lab_work_variant_id")
    private int labWorkVariant;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Column(name = "deleted")
    private boolean deleted;

}