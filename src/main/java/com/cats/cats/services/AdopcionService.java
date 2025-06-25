package com.cats.cats.services;

import com.cats.cats.entities.Adopcion;
import com.cats.cats.repository.AdopcionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdopcionService {

    private final AdopcionRepository adopcionRepository;

    @Autowired
    public AdopcionService(AdopcionRepository adopcionRepository) {
        this.adopcionRepository = adopcionRepository;
    }

    public Adopcion save(Adopcion adopcion) {
        return adopcionRepository.save(adopcion);
    }

    public boolean existsByUserId(ObjectId userId) {
        return adopcionRepository.existsByUserId(userId);
    }

    public List<Adopcion> findByUserId(ObjectId userId) {
        return adopcionRepository.findByUserId(userId);
    }

    public List<Adopcion> findByCaregiverId(ObjectId caregiverId) {
        return adopcionRepository.findByCaregiverId(caregiverId);
    }

    public List<Adopcion> findByCatId(ObjectId catId) {
        return adopcionRepository.findByCatId(catId);
    }

    public List<Adopcion> getAdopciones() {
        return adopcionRepository.findAll();
    }
}


