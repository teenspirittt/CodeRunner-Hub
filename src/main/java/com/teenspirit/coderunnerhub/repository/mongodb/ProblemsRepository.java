package com.teenspirit.coderunnerhub.repository.mongodb;

import com.teenspirit.coderunnerhub.model.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemsRepository extends MongoRepository<Problem, Integer> {
}