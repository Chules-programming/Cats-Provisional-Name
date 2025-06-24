package com.cats.cats.services;

import com.cats.cats.entities.Adopcion;
import com.cats.cats.repository.AdopcionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdopcionService {

    @Autowired
    private AdopcionRepository adopcionRepository;

    public Adopcion save(Adopcion adopcion){
        return adopcionRepository.save(adopcion);
    }
    public boolean existsByUserId(ObjectId userId) {
        return adopcionRepository.existsByUserId(userId);
    }

    public List<Adopcion> getAdopciones(){
        return adopcionRepository.findAll();
    }
}
