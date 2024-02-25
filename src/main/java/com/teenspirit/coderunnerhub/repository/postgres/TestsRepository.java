package com.teenspirit.coderunnerhub.repository.postgres;

import com.teenspirit.coderunnerhub.model.postgres.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface TestsRepository extends JpaRepository<Test, Integer> {

    List<Test> findAllByTaskIdAndDeletedFalse(int taskId);

}
