package com.teenspirit.coderunnerhub.model.postgres;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lab_work_variant_appointment")
public class LabWorkVariantAppointment {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "lab_work_variant_id")
    private int labWorkVariantId;


    @Column(columnDefinition = "boolean default false")
    private boolean deleted;

}