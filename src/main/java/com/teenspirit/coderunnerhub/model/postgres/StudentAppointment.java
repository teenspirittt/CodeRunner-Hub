package com.teenspirit.coderunnerhub.model.postgres;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "student_appointment")
public class StudentAppointment {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "task_id")
    private int taskId;
}