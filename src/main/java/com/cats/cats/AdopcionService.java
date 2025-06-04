package com.cats.cats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdopcionService {

    @Autowired
    private AdopcionRespository adopcionRespository;

    public Adopcion save(Adopcion adopcion){
        return adopcionRespository.save(adopcion);
    }

    public List<Adopcion> getAdopciones(){
        return adopcionRespository.findAll();
    }
}
