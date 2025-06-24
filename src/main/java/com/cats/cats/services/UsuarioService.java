package com.cats.cats.services;

import com.cats.cats.entities.Usuario;
import com.cats.cats.repository.UsuarioRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario get(ObjectId id) {
        // Cambiar a Optional y manejar el caso de no encontrado
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        return usuarioOpt.orElse(null);
    }

    public Usuario update(ObjectId id, Usuario usuario) {
        // Cambiar a Optional y manejar el caso de no encontrado
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuarioActual = usuarioOpt.get();
            usuarioActual.setUsername(usuario.getUsername());
            usuarioActual.setAge(usuario.getAge());
            usuarioActual.setEmail(usuario.getEmail());
            usuarioActual.setPassword(usuario.getPassword());
            return usuarioRepository.save(usuarioActual);
        }
        return null; // o lanzar una excepción
    }

    public void delete(ObjectId id) {
        usuarioRepository.deleteById(id);
    }
}
