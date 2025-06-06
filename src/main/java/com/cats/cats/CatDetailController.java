package com.cats.cats;

import javafx.fxml.FXML;
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

import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class CatDetailController {

    @FXML private ImageView catImage1, catImage2, catImage3;
    @FXML private MediaView catVideo;
    @FXML private Label catNameLabel, breedLabel, ageLabel, heightLabel, widthLabel,
            colorLabel, sexLabel, friendlyKidsLabel, friendlyAnimalsLabel,
            statusLabel, personality1Label, personality2Label, bornDateLabel, ongLabel, locationLabel;
    @FXML private Button returnButton, playVideoButton, adoptButton;

    @Autowired private CatService catService;
    @Autowired private UsuarioController usuarioController;
    private ResourceBundle resources;

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

        // Etiquetas traducidas con ResourceBundle
        this.resources = Main.getResourceBundle();
        catNameLabel.setText(currentCat.getName());
        breedLabel.setText(resources.getString("cat.breed") + ": " + currentCat.getBreed());
        ageLabel.setText(resources.getString("cat.age") + ": " + currentCat.getAge());
        heightLabel.setText(resources.getString("cat.height") + ": " + currentCat.getHeight() + " " + resources.getString("cat.unit.cm"));
        widthLabel.setText(resources.getString("cat.width") + ": " + currentCat.getWidth() + " " + resources.getString("cat.unit.cm"));
        colorLabel.setText(resources.getString("cat.color") + ": " + currentCat.getColor());
        sexLabel.setText(resources.getString("cat.sex") + ": " + currentCat.getSex());
        friendlyKidsLabel.setText(resources.getString("cat.friendly.kids") + ": " + currentCat.getFriendlyWithKids());
        friendlyAnimalsLabel.setText(resources.getString("cat.friendly.animals") + ": " + currentCat.getFriendlyWithAnimals());

        // NUEVOS CAMPOS AÑADIDOS
        ongLabel.setText(resources.getString("cat.ong") + ": " + currentCat.getOngName());
        locationLabel.setText(resources.getString("cat.location") + ": " + currentCat.getCatLocation());

        statusLabel.setText(resources.getString("cat.status") + ": " +
                (currentCat.isAdopted() ? resources.getString("cat.adopted") : resources.getString("cat.available")));
        statusLabel.setTextFill(currentCat.isAdopted() ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN);
        bornDateLabel.setText(resources.getString("cat.born.date") + ": " +
                (currentCat.getBornDate() != null ? currentCat.getBornDate() : resources.getString("cat.unknown")));

        // Descripción dividida en dos partes
        String[] descParts = splitDescription(currentCat.getDescription());
        personality1Label.setText(descParts[0]);
        personality2Label.setText(descParts.length > 1 ? descParts[1] : "");

        loadCatImages();
        loadCatVideo();
        setupButtons();
    }

    private void setupButtons() {
        adoptButton.setText(resources.getString("cat.adopt.button") + " " + currentCat.getName());
        playVideoButton.setText(resources.getString("cat.play"));
        playVideoButton.setOnAction(e -> toggleVideoPlayback());
        returnButton.setOnAction(e -> returnButton.getScene().getWindow().hide());
        adoptButton.setOnAction(e -> handleAdopt());
    }

    private void handleAdopt() {
        try {
            Stage currentStage = (Stage) adoptButton.getScene().getWindow();
            currentStage.close();

            UsuarioController usuarioController = Main.context.getBean(UsuarioController.class);
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
                playVideoButton.setText(resources.getString("cat.play"));
            } else {
                player.play();
                playVideoButton.setText(resources.getString("cat.pause"));
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
        // Se inicializa tras setCurrentCat()
    }
}



