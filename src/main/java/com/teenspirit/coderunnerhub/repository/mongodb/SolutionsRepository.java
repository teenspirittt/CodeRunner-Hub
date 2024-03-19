package com.teenspirit.coderunnerhub.repository.mongodb;

import com.teenspirit.coderunnerhub.model.Solution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionsRepository extends MongoRepository<Solution, Integer> {
}