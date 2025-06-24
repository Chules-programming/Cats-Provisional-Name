package com.cats.cats;

import com.cats.cats.entities.Adopcion;
import com.cats.cats.entities.UserRating;
import com.cats.cats.entities.Usuario;
import com.cats.cats.repository.AdopcionRepository;
import com.cats.cats.repository.UserRatingRepository;
import com.cats.cats.repository.UsuarioRepository;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdopcionController {

    @Autowired private AdopcionRepository adopcionRepository;
    @Autowired private UserRatingRepository userRatingRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private Usuario currentUser;

    public void setCurrentUser(Usuario currentUser) {
        this.currentUser = currentUser;
    }

    public void confirmAdoption(ObjectId adoptionId) {
        Adopcion adoption = adopcionRepository.findById(adoptionId).orElseThrow();

        if (currentUser.getId().equals(adoption.getUserId())) {
            adoption.setConfirmedByAdopter(true);
        } else if (currentUser.getId().equals(adoption.getCaregiverId())) {
            adoption.setConfirmedByCaregiver(true);
        }

        if (adoption.isConfirmedByAdopter() && adoption.isConfirmedByCaregiver()) {
            showRatingScreen(adoption);
        }
        adopcionRepository.save(adoption);
    }

    private void showRatingScreen(Adopcion adoption) {
        // Mostrar interfaz de calificación
        Stage stage = new Stage();
        VBox root = new VBox(10);

        Usuario otherUser = getOtherUser(adoption);
        Label title = new Label("Califica a " + otherUser.getUsername());
        HBox stars = new HBox(5);

        // Crear 5 estrellas clickeables
        for (int i = 1; i <= 5; i++) {
            Label star = new Label("☆");
            star.setUserData(i);
            star.setOnMouseClicked(e -> {
                saveRating(adoption, (int) star.getUserData(), otherUser);
                stage.close();
            });
            stars.getChildren().add(star);
        }

        root.getChildren().addAll(title, stars);
        stage.setScene(new Scene(root, 300, 200));
        stage.show();
    }

    private Usuario getOtherUser(Adopcion adoption) {
        if (currentUser.getId().equals(adoption.getUserId())) {
            return usuarioRepository.findById(adoption.getCaregiverId()).orElse(null);
        } else {
            return usuarioRepository.findById(adoption.getUserId()).orElse(null);
        }
    }

    private void saveRating(Adopcion adoption, int rating, Usuario ratedUser) {
        UserRating userRating = new UserRating();
        userRating.setRaterId(currentUser.getId());
        userRating.setRatedId(ratedUser.getId());
        userRating.setRating(rating);
        userRating.setAdoptionId(adoption.getId());
        userRatingRepository.save(userRating);
    }
}
