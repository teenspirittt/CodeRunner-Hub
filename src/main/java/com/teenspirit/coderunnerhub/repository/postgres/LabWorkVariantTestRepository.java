package com.teenspirit.coderunnerhub.repository.postgres;

import com.teenspirit.coderunnerhub.model.postgres.LabWorkVariantTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabWorkVariantTestRepository extends JpaRepository<LabWorkVariantTest, Integer> {

    List<LabWorkVariantTest> findAllByLabWorkVariantIdAndDeletedFalse(int labWorkVariantId);

}

