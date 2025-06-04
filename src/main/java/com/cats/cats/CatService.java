package com.cats.cats;

import javafx.scene.image.Image;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class CatService {
    @Autowired
    private CatRepository catRepository;

    public Cat save(Cat cat) {
        return catRepository.save(cat);
    }

    public List<Cat> getAllCats() {
        return catRepository.findAll();
    }

    public Cat getCatById(ObjectId id) {
        return catRepository.findById(id).orElse(null);
    }

    public void delete(ObjectId id) {
        catRepository.deleteById(id);
    }

    public Image getImageFromDatabase(String imageId) {
        try {
            // Construir URL del endpoint (ajusta según tu configuración)
            String imageUrl = "http://localhost:8080/api/usuario/image/" + imageId;

            // Crear solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .GET()
                    .build();

            // Enviar solicitud y obtener respuesta
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            // Procesar respuesta exitosa
            if (response.statusCode() == 200) {
                return new Image(new ByteArrayInputStream(response.body()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Fallback si hay errores
    }
}

