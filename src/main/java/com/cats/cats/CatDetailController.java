package com.cats.cats;

import com.cats.cats.entities.Cat;
import com.cats.cats.services.CatService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class CatDetailController {

    @FXML private ImageView catImage1, catImage2, catImage3, playPauseOverlay;
    @FXML private MediaView catVideo;
    @FXML private Label catNameLabel, breedLabel, ageLabel, heightLabel, widthLabel,
            colorLabel, sexLabel, friendlyKidsLabel, friendlyAnimalsLabel,
            statusLabel, personality1Label, personality2Label, bornDateLabel, ongLabel, locationLabel;
    @FXML private Button returnButton, adoptButton;
    @FXML private StackPane videoContainer;

    @Autowired private UsuarioController usuarioController;
    @Autowired private ApplicationContext applicationContext;
    private ResourceBundle resources;

    private Cat currentCat;
    private CatService catService;
    private MediaPlayer mediaPlayer;
    private final Image playImage = new Image(getClass().getResourceAsStream("/assets/reproduce.png"));
    private final Image pauseImage = new Image(getClass().getResourceAsStream("/assets/pause.png"));


    public void setCurrentCat(Cat cat) {
        this.currentCat = cat;

        // Inicializa el servicio usando el contexto
        this.catService = applicationContext.getBean(CatService.class);

        updateCatDetails();
    }

    public void cleanUp() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
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

    public void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void setupButtons() {
        adoptButton.setText(resources.getString("cat.adopt.button") + " " + currentCat.getName());

        // Modificar manejador de botón Volver
        returnButton.setOnAction(e -> {
            stopVideo();
            returnButton.getScene().getWindow().hide();
        });

        // Modificar manejador de botón Adoptar
        adoptButton.setOnAction(e -> {
            stopVideo();
            handleAdopt();
        });
    }

    private void handleAdopt() {
        if (Main.isGuest()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(resources.getString("restriction.title"));
            alert.setHeaderText(null);
            alert.setContentText(resources.getString("restriction.message"));
            alert.showAndWait();
            return;
        }

        try {
            Stage currentStage = (Stage) adoptButton.getScene().getWindow();
            currentStage.close();

            // Obtener el controlador UsuarioController desde el contexto de Spring
            UsuarioController usuarioController = Main.context.getBean(UsuarioController.class);
            usuarioController.setCurrentCat(currentCat);
            usuarioController.openAdoptionScreen(currentCat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCatVideo() {
        if (currentCat.getVideoID() == null || currentCat.getVideoID().isEmpty()) {
            return;
        }

        try {
            // Usar el servicio directamente en lugar de UsuarioController
            String videoUrl = usuarioController.getServerBaseUrl() + "/api/usuario/video/" + currentCat.getVideoID();
            Media media = new Media(videoUrl);
            mediaPlayer = new MediaPlayer(media);
            catVideo.setMediaPlayer(mediaPlayer);

            setupVideoControls();
        } catch (Exception e) {
            System.err.println("Error loading video: " + e.getMessage());
        }
    }


    private void setupVideoControls() {
        // 1. Comportamiento del overlay
        playPauseOverlay.setOnMouseClicked(e -> togglePlayPause());

        // 2. Comportamiento al pasar el ratón
        videoContainer.setOnMouseEntered(e -> playPauseOverlay.setVisible(true));
        videoContainer.setOnMouseExited(e -> {
            if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                playPauseOverlay.setVisible(false);
            }
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseOverlay.setImage(playImage);
        } else {
            mediaPlayer.play();
            playPauseOverlay.setImage(pauseImage);
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
        if (imageId == null) {
            imageView.setImage(getDefaultCatImage());
            return;
        }

        try {
            String imageUrl = usuarioController.getServerBaseUrl() + "/api/usuario/image/" + imageId;
            Image image = new Image(imageUrl, true);

            // Mostrar imagen de carga inmediatamente
            imageView.setImage(new Image(getClass().getResourceAsStream("/assets/loading.gif")));

            image.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() == 1.0 && !image.isError()) {
                    Platform.runLater(() -> imageView.setImage(image));
                }
            });

            image.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    Platform.runLater(() -> {
                        imageView.setImage(getDefaultCatImage());
                        imageView.setStyle("-fx-border-color: red;");
                    });
                }
            });
        } catch (Exception e) {
            imageView.setImage(getDefaultCatImage());
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
        // Inicialización básica del overlay
        playPauseOverlay.setImage(playImage);
        playPauseOverlay.setVisible(true);
    }
}



