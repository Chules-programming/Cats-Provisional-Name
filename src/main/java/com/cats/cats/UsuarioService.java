package com.cats.cats;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario save(Usuario usuario){
        return usuarioRepository.save(usuario);

    }

    public List<Usuario> getUsuarios(){
        return usuarioRepository.findAll();
    }

    public Usuario get(ObjectId id){
        return usuarioRepository.findById(id);
    }
    public Usuario update(ObjectId id, Usuario usuario){
        Usuario usuarioActual = usuarioRepository.findById(id);
        usuarioActual.setUsername(usuario.getUsername());
        usuarioActual.setAge(usuario.getAge());
        usuarioActual.setEmail(usuario.getEmail());
        usuarioActual.setPassword(usuario.getPassword());

        return usuarioRepository.save(usuario);
    }

    public void delete(ObjectId id){
        usuarioRepository.deleteById(id);
    }


}
