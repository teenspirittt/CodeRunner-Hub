package com.teenspirit.coderunnerhub.repository.postgres;

import com.teenspirit.coderunnerhub.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestsRepository extends JpaRepository<Test, Integer> {
}
