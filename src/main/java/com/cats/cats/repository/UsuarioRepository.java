package com.cats.cats.repository;


import com.cats.cats.entities.Usuario;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, ObjectId> {

        Usuario findByEmail(String email);
        Usuario findByUsername(String username);
}
