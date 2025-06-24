package com.cats.cats.repository;

import com.cats.cats.entities.Adopcion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdopcionRepository extends MongoRepository<Adopcion, ObjectId> {
    boolean existsByUserId(ObjectId userId);
    List<Adopcion> findByUserId(ObjectId userId);
    List<Adopcion> findByCaregiverId(ObjectId caregiverId);
    List<Adopcion> findByCatId(ObjectId catId);
}
