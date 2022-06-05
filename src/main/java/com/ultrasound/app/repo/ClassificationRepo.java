package com.ultrasound.app.repo;

import com.ultrasound.app.model.data.Classification;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClassificationRepo extends MongoRepository<Classification, String> {
    Optional<Classification> findByName(String name);
    @NotNull Optional<Classification> findById(@NotNull String id);
    Boolean existsByName(String name);
}
