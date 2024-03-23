package com.teenspirit.coderunnerhub.repository.postgres;


import com.teenspirit.coderunnerhub.model.postgres.StudentAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabWorkVariantAppointmentRepository extends JpaRepository<StudentAppointment, Integer> {

}
