package com.cats.cats;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Scope("prototype")
public class CatDetailController {

    @FXML private ImageView catImage1, catImage2, catImage3;
    @FXML private MediaView catVideo;
    @FXML private Label catNameLabel, breedLabel, ageLabel, heightLabel, widthLabel,
            colorLabel, sexLabel, friendlyKidsLabel, friendlyAnimalsLabel,
            statusLabel, personality1Label, personality2Label;
    @FXML private Button returnButton, playVideoButton, adoptButton;

    @FXML private Label bornDateLabel;


    @Autowired private CatService catService;
    @Autowired private UsuarioController usuarioController;

    private Cat currentCat;

    public void setCurrentCat(Cat cat) {
        this.currentCat = cat;
        updateCatDetails();
    }

    public void cleanUp() {
        MediaPlayer player = catVideo.getMediaPlayer();
        if (player != null) {
            player.stop();
            player.dispose();
        }
    }

    public void updateCatDetails() {
        if (currentCat == null) return;

        catNameLabel.setText(currentCat.getName());
        breedLabel.setText("Breed: " + currentCat.getBreed());
        ageLabel.setText("Age: " + currentCat.getAge());
        heightLabel.setText("Height: " + currentCat.getHeight() + " cm");
        widthLabel.setText("Width: " + currentCat.getWidth() + " cm");
        colorLabel.setText("Color: " + currentCat.getColor());
        sexLabel.setText("Sex: " + currentCat.getSex());
        friendlyKidsLabel.setText("Friendly with kids: " + currentCat.getFriendlyWithKids());
        friendlyAnimalsLabel.setText("Friendly with animals: " + currentCat.getFriendlyWithAnimals());
        statusLabel.setText("Status: " + (currentCat.isAdopted() ? "Adopted" : "Not adopted"));
        statusLabel.setTextFill(currentCat.isAdopted() ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN);
        bornDateLabel.setText("Born Date: " + (currentCat.getBornDate() != null ? currentCat.getBornDate() : "Unknown"));

        String[] descParts = splitDescription(currentCat.getDescription());
        personality1Label.setText(descParts[0]);
        personality2Label.setText(descParts.length > 1 ? descParts[1] : "");

        loadCatImages();
        loadCatVideo();
        setupButtons();
    }

    private void setupButtons() {
        adoptButton.setText("Adopt " + currentCat.getName());
        playVideoButton.setText("Play");
        playVideoButton.setOnAction(e -> toggleVideoPlayback());
        returnButton.setOnAction(e -> returnButton.getScene().getWindow().hide());
        adoptButton.setOnAction(e -> handleAdopt());
    }

    private void handleAdopt() {
        try {
            // Cerrar la ventana actual de detalles
            Stage currentStage = (Stage) adoptButton.getScene().getWindow();
            currentStage.close();

            // Obtener el controlador principal desde Spring
            UsuarioController usuarioController = Main.context.getBean(UsuarioController.class);

            // Abrir la pantalla de adopción
            usuarioController.openAdoptionScreen(currentCat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void toggleVideoPlayback() {
        MediaPlayer player = catVideo.getMediaPlayer();
        if (player != null) {
            if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                player.pause();
                playVideoButton.setText("Play");
            } else {
                player.play();
                playVideoButton.setText("Pause");
            }
        }
    }

    private void loadCatVideo() {
        if (currentCat.getVideoID() != null && !currentCat.getVideoID().isEmpty()) {
            try {
                String videoUrl = "http://localhost:8080/api/usuario/video/" + currentCat.getVideoID();
                Media media = new Media(videoUrl);
                MediaPlayer player = new MediaPlayer(media);
                catVideo.setMediaPlayer(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCatImages() {
        if (catService == null || currentCat == null) return;

        try {
            setCatImage(currentCat.getImageId1(), catImage1);
            setCatImage(currentCat.getImageId2(), catImage2);
            setCatImage(currentCat.getImageId3(), catImage3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCatImage(String imageId, ImageView imageView) {
        if (imageId != null && !imageId.isEmpty()) {
            Image image = catService.getImageFromDatabase(imageId);
            imageView.setImage(image != null ? image : getDefaultCatImage());
        }
    }

    private Image getDefaultCatImage() {
        try {
            return new Image(getClass().getResourceAsStream("/assets/default_cat.jpg"));
        } catch (Exception e) {
            return null;
        }
    }

    private String[] splitDescription(String description) {
        int splitLength = 150;
        if (description == null || description.length() <= splitLength) {
            return new String[]{description != null ? description : ""};
        }

        int splitAt = description.lastIndexOf(' ', splitLength);
        if (splitAt <= 0) splitAt = splitLength;

        return new String[]{
                description.substring(0, splitAt),
                description.substring(splitAt).trim()
        };
    }

    @FXML
    private void initialize() {
        // La inicialización se hace en updateCatDetails() luego de setCurrentCat()
    }
}


