package com.cats.cats;



import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.application.HostServices;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import javafx.scene.Cursor;

import javax.imageio.ImageIO;
import java.net.URL;
import java.util.*;
import java.util.Map;
import java.util.function.Supplier;

import static com.cats.cats.Main.currentLocale;


@Component
@RestController
@RequestMapping("/api/usuario")
public class UsuarioController implements Initializable {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AdopcionService adopcionService;
    @Autowired
    private CatService catService;
    @Autowired
    private CatRepository catRepository;
    @Autowired
    public void setUsuarioService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Autowired
    public void setCatService(CatService catService) {
        this.catService = catService;
    }
    @Autowired
    private ReviewService reviewService;

    private Usuario currentUser;
    private Map<String, ObjectId> catNameToIdMap = new HashMap<>();
    private ObservableList<String> catNames = FXCollections.observableArrayList();
    private List<Cat> allCats;// Lista completa de gatos
    private List<Cat> searchResults;
    private int currentPage = 0;
    private static final int CATS_PER_PAGE = 6;
    private static final int MAX_SUGGESTION_CHARS = 400;
    private static final int MAX_SUGGESTION_LINES = 10;

    private static final int MAX_REVIEW_CHARS = 400;
    private static final int MAX_REVIEW_LINES = 10;
    private void safeSetText(Labeled component, String key, ResourceBundle bundle) {
        if (component == null) return;

        if (bundle != null && bundle.containsKey(key)) {
            component.setText(bundle.getString(key));
        } else {
            // Fallback para desarrollo
            component.setText("[" + key + "]");
        }
    }
    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
    private static final int MAX_DESCRIPTION_CHARS = 100;
    private static final int MAX_DESCRIPTION_LINES = 2;


    @FXML
    private AnchorPane AnchorMain, AnchorMain2, AnchorRegister, anchorweb, anchorweb2, anchorweb3, AnchorAdopt, contentPane,
             AnchorConditions, AnchorOng, AnchorMenuAdd, imageContainer, AnchorGeneric;

    @FXML
    private ListView<Usuario> listview;

    @FXML
    private ListView<Cat> listview2;

    @FXML
    private TextField textfield1, textusername, textemail, textage, fieldname, fieldadress, fieldpostal, fieldphone, fieldaddbreed, fieldaddage, fieldaddsex, fieldaddcolor, fieldaddwidth, fieldaddheight, fieldaddname, fieldRegisterUsername, fieldRegisterAge, fieldRegisterEmail,
    fieldaddong1, fieldaddplace1, searchField, fieldaddphone, fieldAdditionalContact, recoverEmail, recoverAge;

    @FXML
    private PasswordField textpassword, textfield2, accessfield, fieldRegisterPassword, recoverPassword;

    @FXML
    private TextArea areaadd, reviewComment, suggestionTextArea;

    @FXML
    private Label welcome, username, password, registeredusers, addusername, addemail, addage, addpassword, statusLabel,
            tituloregister, warning, congratulations, warningUsername, warningAge, warningEmail, warningPassword,
            found, information, name, adress, postal, select, phone, warning2, warning3, warning4, warningPostal, warningPhone, warningAdress, warningNameSur,
            count, theconditions, errorDuplicateName, errorphone, additionalContactLabel, recoverMessage, suggestionMessage, suggestionCounter, reviewCounter,
            label1ong, label2ong, label3ong, label4ong, label5ong, label6ong, label7ong, label8ong, label9ong, errorongpassword, breedadd, ageadd, coloradd, sexadd, heightadd, widthadd, image1add, image2add, image3add, video1add, friendlykidsadd, friendlyanimalsadd, descriptionadd, labelmenuadd1,
            errorbreed, errorage, errorsex, errorcolor, errorheight, errorwidth, erroroption1, erroroption2, errordescription, errorimage1, errorimage2, errorimage3, errorvideo1, successMessage, nameadd1, errorname1,
            errorage2, errorheight2, errorwidth2, heightLabel, widthLabel, colorLabel, catNameLabel, breedLabel, ageLabel, personality1Label, sexLabel, personality2Label, friendlyKidsLabel, friendlyAnimalsLabel, DateLabel, bornDateLabel, errorBornDate, theconditions2, ongname, catplace, errorongname, errorcatplace1, descriptionCounter;

    @FXML
    private DatePicker fieldaddBornDate;

    @FXML
    private ChoiceBox<Integer> ratingChoice;

    @FXML
    private Button register, registeruser, goback, login, adopt, nextpage, previouspage, gomenu, back, back1, confirmation, play1, play2,
             notaccept, aceptbutton, ong, accessbutton, menuaddtomenu, submitadd, add1button, add2button, add3button, add4button, adoptButton, returnButton, playVideoButton, goToLoginButton, searchButton;

    @FXML
    private HBox buttonContainer;

    @FXML
    private ResourceBundle resources;

    @FXML
    private ImageView ximage, linkedinimage, gmailimage, addimage1, addimage2, addimage3, catImage1, catImage2, catImage3, spanish, english;

    @FXML
    private ScrollPane scro, scrol;

    @FXML
    private CheckBox accept, pleasecheck;

    @FXML
    private ChoiceBox<String> selection, choiceadd1, choiceadd2;
    @FXML
    private VBox catsContainer, searchResultsContainer, reviewsContainer;

    @FXML
    private MediaView currentMediaView, addvideo1, catVideo;

    private File file;
    private Media media;
    private Media currentMedia;
    private MediaPlayer mediaPlayer;
    private Cat currentCat;
    private MongoDatabase mongoDatabase;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB en bytes
    private static final long MAX_VIDEO_SIZE = 10 * 1024 * 1024; //10MB en bytes

    private FileChooser fileChooser = new FileChooser();



    private MongoDatabase getMongoDatabase() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        return mongoClient.getDatabase("test"); //Name of the database
    }

    private File selectedImageFile;
    private File selectedImageFile1;
    private File selectedImageFile2;
    private File selectedImageFile3;
    private String currentFxmlPath;

    public void setCurrentFxmlPath(String path) {
        this.currentFxmlPath = path;
    }

    public void displayCatImage(String imageId) {
        try {
            String imageUrl = "http://localhost:8080/api/usuario/image/" + imageId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                Platform.runLater(() -> {
                    Image image = new Image(new ByteArrayInputStream(response.body()));
                    ImageView imageView = new ImageView(image);  // Usar el constructor correcto
                    imageView.setFitWidth(300);
                    imageView.setPreserveRatio(true);
                    imageContainer.getChildren().clear();
                    imageContainer.getChildren().add(imageView);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image loadImageFromDatabase(String imageId) {
        try {
            Document imageDoc = mongoDatabase.getCollection("images").find(Filters.eq("_id", new ObjectId(imageId))).first();
            if (imageDoc != null) {
                String base64 = imageDoc.getString("data");
                byte[] decodedBytes = Base64.getDecoder().decode(base64);
                return new Image(new ByteArrayInputStream(decodedBytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Media loadVideoFromDatabase(String videoId) {
        try {
            Document videoDoc = mongoDatabase.getCollection("videos").find(Filters.eq("_id", new ObjectId(videoId))).first();
            if (videoDoc != null) {
                String base64 = videoDoc.getString("data");
                byte[] decodedBytes = Base64.getDecoder().decode(base64);

                File tempVideo = File.createTempFile("video", ".mp4");
                tempVideo.deleteOnExit(); // Se borra cuando se cierra la app

                try (FileOutputStream fos = new FileOutputStream(tempVideo)) {
                    fos.write(decodedBytes);
                }

                return new Media(tempVideo.toURI().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void displayCatVideo(String videoId) {
        try {
            String videoUrl = "http://localhost:8080/api/usuario/video/" + videoId;

            // Crear un archivo temporal para el video
            File tempFile = File.createTempFile("video_", ".mp4");
            tempFile.deleteOnExit();

            // Descargar el video desde el endpoint
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(videoUrl))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                Files.write(tempFile.toPath(), response.body());

                // Mostrar el video
                Platform.runLater(() -> {
                    Media media = new Media(tempFile.toURI().toString());
                    MediaPlayer player = new MediaPlayer(media);
                    addvideo1.setMediaPlayer(player);
                    player.play();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentCat(Cat cat) {
        this.currentCat = cat;
    }

    public void updateCatDetails() {
        if (currentCat == null) return;

        Platform.runLater(() -> {
            try {
                // Actualizar todos los campos
                if (catNameLabel != null) catNameLabel.setText(currentCat.getName());
                if (breedLabel != null) breedLabel.setText("Breed: " + currentCat.getBreed());
                if (ageLabel != null) ageLabel.setText("Age: " + currentCat.getAge());
                if (heightLabel != null) heightLabel.setText("Height: " + currentCat.getHeight() + " cm");
                if (widthLabel != null) widthLabel.setText("Width: " + currentCat.getWidth() + " cm");
                if (colorLabel != null) colorLabel.setText("Color: " + currentCat.getColor());
                if (sexLabel != null) sexLabel.setText("Sex: " + currentCat.getSex());
                if (friendlyKidsLabel != null) friendlyKidsLabel.setText("Friendly with kids: " + currentCat.getFriendlyWithKids());
                if (friendlyAnimalsLabel != null) friendlyAnimalsLabel.setText("Friendly with animals: " + currentCat.getFriendlyWithAnimals());
                if (statusLabel != null) {
                    statusLabel.setText("Status: " + (currentCat.isAdopted() ? "Adopted" : "Not adopted"));
                    statusLabel.setTextFill(currentCat.isAdopted() ? Color.RED : Color.GREEN);
                    bornDateLabel.setText("Born: " + currentCat.getBornDate());
                }



                // Descripción
                if (personality1Label != null && personality2Label != null) {
                    String[] descParts = splitDescription(currentCat.getDescription());
                    personality1Label.setText(descParts[0]);
                    personality2Label.setText(descParts.length > 1 ? descParts[1] : "");
                }

                // Cargar imágenes
                loadCatImages(currentCat);

            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Error updating cat details: " + e.getMessage());
            }
        });
    }

    private String[] splitDescription(String description) {
        // Dividir cada 150 caracteres manteniendo palabras completas
        int splitLength = 150;
        if (description.length() <= splitLength) {
            return new String[]{description};
        }

        int splitAt = description.lastIndexOf(' ', splitLength);
        if (splitAt <= 0) splitAt = splitLength;

        return new String[]{
                description.substring(0, splitAt),
                description.substring(splitAt)
        };
    }

    private void loadCatImages(Cat cat) {
        if (cat == null || catImage1 == null || catImage2 == null || catImage3 == null) return;

        // Limpiar imágenes anteriores
        catImage1.setImage(null);
        catImage2.setImage(null);
        catImage3.setImage(null);

        // Cargar imágenes en paralelo
        CompletableFuture.runAsync(() -> {
            try {
                // Imagen 1
                if (cat.getImageId1() != null && !cat.getImageId1().isEmpty()) {
                    Image image1 = getImageFromDatabase(cat.getImageId1());
                    Platform.runLater(() -> {
                        catImage1.setImage(image1 != null ? image1 : getDefaultCatImage());
                        catImage1.setFitWidth(200);
                        catImage1.setPreserveRatio(true);
                        catImage1.setOnMouseClicked(e -> loadImage(cat.getImageId1(), catImage1));
                    });
                }

                // Imagen 2
                if (cat.getImageId2() != null && !cat.getImageId2().isEmpty()) {
                    Image image2 = getImageFromDatabase(cat.getImageId2());
                    Platform.runLater(() -> {
                        catImage2.setImage(image2 != null ? image2 : getDefaultCatImage());
                        catImage2.setFitWidth(200);
                        catImage2.setPreserveRatio(true);
                        catImage2.setOnMouseClicked(e -> loadImage(cat.getImageId2(), catImage2));
                    });
                }

                // Imagen 3
                if (cat.getImageId3() != null && !cat.getImageId3().isEmpty()) {
                    Image image3 = getImageFromDatabase(cat.getImageId3());
                    Platform.runLater(() -> {
                        catImage3.setImage(image3 != null ? image3 : getDefaultCatImage());
                        catImage3.setFitWidth(200);
                        catImage3.setPreserveRatio(true);
                        catImage3.setOnMouseClicked(e -> loadImage(cat.getImageId3(), catImage3));
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> showErrorAlert("Error loading cat images: " + e.getMessage()));
            }
        });
    }


    private void setupButtons(Cat cat) {
        if (adoptButton != null) {
            adoptButton.setText("Adopt " + cat.getName());
            adoptButton.setOnMouseClicked(e -> {
                try {
                    handleAdopt(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }

        if (returnButton != null) {
            returnButton.setOnAction(e -> {
                try {
                    handleGoCatSection(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }

        if (playVideoButton != null && catVideo.getMediaPlayer() != null) {
            playVideoButton.setOnAction(e -> {
                catVideo.getMediaPlayer().play();
            });
        }
    }

    private void loadImageIntoView(String imageId, ImageView imageView) {
        try {
            String imageUrl = "http://localhost:8080/api/usuario/image/" + imageId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                Platform.runLater(() -> {
                    Image image = new Image(new ByteArrayInputStream(response.body()));
                    imageView.setImage(image);
                    imageView.setFitWidth(200);  // Ajustar según necesidad
                    imageView.setPreserveRatio(true);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImage(getDefaultCatImage());
        }
    }

    private void loadVideoIntoView(String videoId, MediaView mediaView) {
        if (videoId == null || videoId.isEmpty() || mediaView == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                String videoUrl = "http://localhost:8080/api/usuario/video/" + videoId;

                // Crear archivo temporal
                File tempFile = File.createTempFile("video_", ".mp4");
                tempFile.deleteOnExit();

                // Descargar el video
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(videoUrl))
                        .GET()
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    Files.write(tempFile.toPath(), response.body());

                    Platform.runLater(() -> {
                        try {
                            // Detener reproducción anterior
                            if (mediaView.getMediaPlayer() != null) {
                                mediaView.getMediaPlayer().stop();
                                mediaView.getMediaPlayer().dispose();
                            }

                            Media media = new Media(tempFile.toURI().toString());
                            MediaPlayer player = new MediaPlayer(media);
                            mediaView.setMediaPlayer(player);

                            // Configurar botón de play si existe
                            if (playVideoButton != null) {
                                playVideoButton.setOnAction(e -> {
                                    if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                                        player.pause();
                                        playVideoButton.setText("Play");
                                    } else {
                                        player.play();
                                        playVideoButton.setText("Pause");
                                    }
                                });
                            }
                        } catch (Exception e) {
                            showErrorAlert("Error setting up video player: " + e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> showErrorAlert("Error loading video: " + e.getMessage()));
            }
        });
    }

    @FXML
    private void handleForgotPassword(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/RecoverAccount.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/RecoverAccount.fxml"));
        loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
    }

    @FXML
    private void handleReviews(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/Reviews.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/Reviews.fxml"));
        loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        // Cargar reseñas existentes
        UsuarioController controller = loader.getController();
        controller.loadReviews();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
    }

    public void loadReviews() {
        reviewsContainer.getChildren().clear();
        List<Review> reviews = reviewService.findAll();

        for (Review review : reviews) {
            reviewsContainer.getChildren().add(createReviewCard(review));
        }
    }

    private Node createReviewCard(Review review) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(new Label(review.getUsername()));

        // Mostrar estrellas según la calificación
        HBox stars = new HBox(5);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < review.getRating() ? "★" : "☆");
            star.setStyle("-fx-text-fill: " + (i < review.getRating() ? "gold" : "gray") + ";");
            stars.getChildren().add(star);
        }

        Label dateLabel = new Label(review.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        Label commentLabel = new Label(review.getComment());
        commentLabel.setWrapText(true);

        card.getChildren().addAll(header, stars, dateLabel, commentLabel);
        return card;
    }

    @FXML
    private void handleSubmitReview(MouseEvent event) {
        if (currentUser == null) {
            showErrorAlert("Debes iniciar sesión para agregar una reseña");
            return;
        }

        Integer rating = ratingChoice.getValue();
        String comment = reviewComment.getText().trim();
        int lineCount = comment.split("\n").length;

        // Validaciones
        if (rating == null) {
            showErrorAlert("Por favor selecciona una calificación");
            return;
        }

        if (comment.isEmpty()) {
            showErrorAlert("Por favor escribe un comentario");
            return;
        }

        if (comment.length() > MAX_REVIEW_CHARS) {
            showErrorAlert("El comentario excede el límite de " + MAX_REVIEW_CHARS + " caracteres");
            return;
        }

        if (lineCount > MAX_REVIEW_LINES) {
            showErrorAlert("El comentario excede el límite de " + MAX_REVIEW_LINES + " líneas");
            return;
        }

        // Crear y guardar nueva reseña
        Review newReview = new Review();
        newReview.setUserId(currentUser.getId());
        newReview.setUsername(currentUser.getUsername());
        newReview.setRating(rating);
        newReview.setComment(comment);
        newReview.setDate(LocalDate.now());

        reviewService.save(newReview);

        // Actualizar la lista de reseñas
        loadReviews();

        // Limpiar el formulario
        ratingChoice.setValue(null);
        reviewComment.clear();

        // Mostrar mensaje de éxito
        showInfoAlert("¡Reseña enviada con éxito!");
    }


    private void showInfoAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Método para manejar el botón de sugerencias
    @FXML
    private void handleSuggestions(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/Suggest.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/Suggest.fxml"));
        loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Método para enviar sugerencias
    @FXML
    private void handleSubmitSuggestion(MouseEvent event) {
        if (currentUser == null) {
            showErrorAlert("Debes iniciar sesión para enviar una sugerencia");
            return;
        }

        String suggestion = suggestionTextArea.getText().trim();
        int lineCount = suggestion.split("\n").length;

        // Validaciones
        if (suggestion.isEmpty()) {
            showErrorAlert("Por favor, escribe tu sugerencia");
            return;
        }

        if (suggestion.length() > MAX_SUGGESTION_CHARS) {
            showErrorAlert("La sugerencia excede el límite de " + MAX_SUGGESTION_CHARS + " caracteres");
            return;
        }

        if (lineCount > MAX_SUGGESTION_LINES) {
            showErrorAlert("La sugerencia excede el límite de " + MAX_SUGGESTION_LINES + " líneas");
            return;
        }

        // Simular envío (aquí iría la lógica real)
        String email = currentUser.getEmail();
        System.out.println("Sugerencia enviada a " + email + ": " + suggestion);

        // Mostrar mensaje de confirmación
        String message = resources.getString("suggestions.success")
                .replace("{0}", email);
        suggestionMessage.setText(message);
        suggestionMessage.setVisible(true);
        suggestionTextArea.clear();
    }


    @FXML
    private void handleRecoverAccount(MouseEvent event) {
        String email = recoverEmail.getText();
        String password = recoverPassword.getText();
        String ageText = recoverAge.getText();

        // Validar campos
        if (email.isEmpty() || password.isEmpty() || ageText.isEmpty()) {
            recoverMessage.setText(resources.getString("recover.error.incomplete_fields"));
            recoverMessage.setTextFill(Color.RED);
            recoverMessage.setVisible(true);
            return;
        }

        // Verificar si el correo existe en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            recoverMessage.setText(resources.getString("error.email.not.registered"));
            recoverMessage.setTextFill(Color.RED);
            recoverMessage.setVisible(true);
            return;
        }

        // Simular proceso de recuperación
        recoverMessage.setText(resources.getString("recover.success"));
        recoverMessage.setTextFill(Color.GREEN);
        recoverMessage.setVisible(true);

        // Limpiar campos después de 3 segundos
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    recoverEmail.clear();
                    recoverPassword.clear();
                    recoverAge.clear();
                    recoverMessage.setVisible(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @FXML
    private void handleBackFromRecover(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/main.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/main.fxml"));
        loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
    }

    public File downloadFileFromGridFS(String fileId) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("catsdb");
            GridFSBucket gridFSBucket = GridFSBuckets.create(database);

            GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(fileId))).first();
            if (gridFSFile == null) {
                System.err.println("❌ Archivo no encontrado en GridFS con ID: " + fileId);
                return null;
            }

            File tempFile = File.createTempFile("media_", "_" + gridFSFile.getFilename());
            try (FileOutputStream streamToDownloadTo = new FileOutputStream(tempFile)) {
                gridFSBucket.downloadToStream(gridFSFile.getObjectId(), streamToDownloadTo);
            }

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadImage(String fileId, ImageView imageView) {
        try {
            String imageUrl = "http://localhost:8080/api/usuario/image/" + fileId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                Platform.runLater(() -> {
                    Image image = new Image(new ByteArrayInputStream(response.body()));
                    imageView.setImage(image);
                    imageView.setFitWidth(200);  // Adjust as needed
                    imageView.setPreserveRatio(true);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImage(getDefaultCatImage());
        }
    }






    // Ejemplo de uso cuando seleccionas un gato
    @FXML
    private void handleCatSelection(MouseEvent event, Cat cat) {
        Cat selectedCat = listview2.getSelectionModel().getSelectedItem();
        if (selectedCat != null) {
            // Mostrar primera imagen si existe
            if (selectedCat.getImageId1() != null) {
                displayCatImage(selectedCat.getImageId1());
            }

            // Mostrar video si existe
            if (selectedCat.getVideoID() != null) {
                displayCatVideo(selectedCat.getVideoID());
            }
        }
    }
    @FXML
    private void handleRegisterUser(MouseEvent event) {
        String username = fieldRegisterUsername.getText();
        String email = fieldRegisterEmail.getText();
        String password = fieldRegisterPassword.getText();
        String ageText = fieldRegisterAge.getText();

        // Ocultar advertencias previas
        warningUsername.setVisible(false);
        warningAge.setVisible(false);
        warningEmail.setVisible(false);
        warningPassword.setVisible(false);

        // Validar campos vacíos
        boolean hasError = false;
        if (username.isEmpty()) {
            warningUsername.setText(resources.getString("warning.username_empty"));
            warningUsername.setVisible(true);
            hasError = true;
        }
        if (ageText.isEmpty()) {
            warningAge.setText(resources.getString("warning.age_empty"));
            warningAge.setVisible(true);
            hasError = true;
        }
        if (email.isEmpty()) {
            warningEmail.setText(resources.getString("warning.email_empty"));
            warningEmail.setVisible(true);
            hasError = true;
        }
        if (password.isEmpty()) {
            warningPassword.setText(resources.getString("warning.password_empty"));
            warningPassword.setVisible(true);
            hasError = true;
        }

        if (hasError) return;

        // Validar edad numérica
        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            warningAge.setText(resources.getString("warning.age_number"));
            warningAge.setVisible(true);
            return;
        }

        // Crear y guardar usuario
        Usuario newUser = new Usuario();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setAge(age);

        usuarioService.save(newUser);

        // Mostrar éxito
        warning2.setText(resources.getString("warning.registration_success"));
        warning2.setTextFill(Color.GREEN);
        warning2.setVisible(true);

        // Limpiar campos
        fieldRegisterUsername.clear();
        fieldRegisterAge.clear();
        fieldRegisterEmail.clear();
        fieldRegisterPassword.clear();
    }


    @FXML
    private void cargarSigFXML() throws IOException {
        setCurrentFxmlPath("/com/java/fx/RegisterUser.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/RegisterUser.fxml"));
        loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) AnchorMain.getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
    }


    //New page for cats and stuff
    @FXML
    public void initialize() {
        this.resources = ResourceBundle.getBundle("Messages", Main.getCurrentLocale());

    }

    private void loadCats() {
        if (catRepository == null) {
            System.err.println("catRepository is null!");
            return;
        }

        this.allCats = catRepository.findAll();
        System.out.println("Loaded " + allCats.size() + " cats from repository");
    }

    private void updatePage() {
        if (catsContainer == null || allCats == null) return;

        catsContainer.getChildren().clear();
        catsContainer.setSpacing(20);
        catsContainer.setPadding(new Insets(20));

        int fromIndex = currentPage * CATS_PER_PAGE;
        int toIndex = Math.min(fromIndex + CATS_PER_PAGE, allCats.size());

        // Crear un GridPane para organizar los gatos en 2 columnas
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        int col = 0;
        int row = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            Cat cat = allCats.get(i);
            Node catCard = createCatCard(cat); // Método que crea la tarjeta individual

            grid.add(catCard, col, row);
            col++;

            if (col > 1) {
                col = 0;
                row++;
            }
        }

        // Añadir el grid al contenedor principal
        catsContainer.getChildren().add(grid);
    }

    private void initializeChoiceBoxes() {
        if (choiceadd1 != null && choiceadd2 != null) {
            ObservableList<String> friendlyOptions = FXCollections.observableArrayList(
                    resources.getString("friendly.yes"),
                    resources.getString("friendly.no"),
                    resources.getString("friendly.dont_know"),
                    resources.getString("friendly.takes_time")
            );

            choiceadd1.setItems(friendlyOptions);
            choiceadd2.setItems(friendlyOptions);
        }
    }




    //Datos de registro
    @FXML
    private void AddData(MouseEvent event) {
        String username = textusername.getText();
        String email = textemail.getText();
        String password = textpassword.getText();
        String ageText = textage.getText();

        //Checkear los datos
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || ageText.isEmpty()) {
            warning2.setText("Please fill all the fields");
            warning2.setTextFill(javafx.scene.paint.Color.RED);
            warning2.setVisible(true);
            return;

        }
        int age;
        try {
            age = Integer.parseInt(ageText);

        } catch (NumberFormatException e) {
            warning3.setText("Age must be a number");
            warning3.setTextFill(javafx.scene.paint.Color.RED);
            warning3.setVisible(true);
            return;
        }
        Usuario newUser = new Usuario();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setAge(age);

        usuarioService.save(newUser);
        warning4.setText("User registered correctly :)");
        warning4.setTextFill(Color.GREEN);
        warning4.setVisible(true);
        textusername.clear();
        textemail.clear();
        textpassword.clear();
        textage.clear();

    }

    //Go back from register to log in
    @FXML
    private void goingback(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/main.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/main.fxml"));
        loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
    }

    //Login and go to website
    @FXML
    private void handleLogin(MouseEvent event) throws IOException {
        String enteredUsername = textfield1.getText();
        String enteredPassword = textfield2.getText();

        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario u : usuarios) {
            if (u.getUsername() != null
                    && u.getPassword() != null
                    && u.getUsername().equals(enteredUsername)
                    && u.getPassword().equals(enteredPassword)) {

                currentUser = u;
                setCurrentFxmlPath("/com/java/fx/web2.fxml");

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/java/fx/web2.fxml"),
                        ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
                );
                loader.setControllerFactory(Main.context::getBean);
                Parent root = loader.load();

                // Crear la escena sin dimensiones fijas
                Scene scene = new Scene(root);

                // Obtener el Stage actual
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Asignar la escena y permitir redimensionamiento
                stage.setScene(scene);
                stage.setResizable(true);
                stage.setMinWidth(600);
                stage.setMinHeight(700);

                // Asegurarse de que el ScrollPane vuelva al inicio
                Platform.runLater(() -> {
                    ScrollPane scroll = (ScrollPane) root.lookup("#scrol");
                    if (scroll != null) {
                        scroll.setVvalue(0.0);
                    }
                });

                return;
            }
        }

        // Si no se encuentra el usuario, mostrar advertencia
        Label warning = (Label) AnchorMain.lookup("#warning");
        if (warning != null) {
            warning.setVisible(true);
        }
    }



    @FXML
    private void handleMenu(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/main.fxml");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/main.fxml"),
                ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
        );
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(700);
    }

    @FXML
    private void handleAdopt(MouseEvent event) throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        setCurrentFxmlPath("/com/java/fx/adoptweb.fxml");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/adoptweb.fxml"),
                ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
        );
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(700);
    }


    @FXML
    private void onCatImageClicked(MouseEvent event) {
        // Obtener el gato correspondiente al ImageView clickeado
        ImageView clickedImage = (ImageView) event.getSource();
        Cat cat = findCatByImageViewId(clickedImage.getId()); // Implementa esta lógica

        if (cat != null) {
            handleCatClick(event, cat);
        }
    }

    // Método auxiliar para encontrar un gato por el ID del ImageView
    private Cat findCatByImageViewId(String imageViewId) {
        List<Cat> cats = catRepository.findAll();
        for (Cat cat : cats) {
            if (imageViewId.toLowerCase().contains(cat.getBreed().toLowerCase())) {
                return cat;
            }
        }
        return null;
    }

    private boolean validateForm() {
        boolean isValid = true;
        hideAllErrorMessages();
        errorBornDate.setVisible(false);

        // Validar Breed
        if (fieldaddbreed.getText().trim().isEmpty()) {
            errorbreed.setText(resources.getString("errorbreed"));
            errorbreed.setVisible(true);
            isValid = false;
        }

        if (fieldaddphone.getText().trim().isEmpty()) {
            errorphone.setText(resources.getString("error.ongphone"));
            errorphone.setVisible(true);
            isValid = false;
        }

        // Validar fecha de nacimiento
        if (fieldaddBornDate.getValue() == null) {
            errorBornDate.setText(resources.getString("error.born_date"));
            errorBornDate.setVisible(true);
            isValid = false;
        }

        // Validar Descripción
        if (areaadd.getText().trim().isEmpty()) {
            errordescription.setText(resources.getString("errordescription"));
            errordescription.setVisible(true);
            isValid = false;
        } else {
            // Validaciones de longitud y líneas
            if (areaadd.getText().length() > MAX_DESCRIPTION_CHARS) {
                errordescription.setText(
                        resources.getString("error.description_chars") +
                                " (" + areaadd.getText().length() + "/" + MAX_DESCRIPTION_CHARS + ")"
                );
                errordescription.setVisible(true);
                isValid = false;
            }

            int lineCount = areaadd.getText().split("\n").length;
            if (lineCount > MAX_DESCRIPTION_LINES) {
                errordescription.setText(
                        resources.getString("error.description_lines") +
                                " (" + lineCount + "/" + MAX_DESCRIPTION_LINES + ")"
                );
                errordescription.setVisible(true);
                isValid = false;
            }
        }

        // Validar Age
        if (fieldaddage.getText().trim().isEmpty()) {
            errorage.setText(resources.getString("errorage"));
            errorage.setVisible(true);
            isValid = false;
        }

        // Validar Sex
        if (fieldaddsex.getText().trim().isEmpty()) {
            errorsex.setText(resources.getString("errorsex"));
            errorsex.setVisible(true);
            isValid = false;
        }

        // Validar Color
        if (fieldaddcolor.getText().trim().isEmpty()) {
            errorcolor.setText(resources.getString("errorcolor"));
            errorcolor.setVisible(true);
            isValid = false;
        }

        // Validar Height
        if (fieldaddheight.getText().trim().isEmpty()) {
            errorheight.setText(resources.getString("errorheight"));
            errorheight.setVisible(true);
            isValid = false;
        } else {
            try {
                Double.parseDouble(fieldaddheight.getText().trim());
            } catch (NumberFormatException e) {
                errorheight.setText(resources.getString("errorheight2"));
                errorheight.setVisible(true);
                isValid = false;
            }
        }

        // Validar Width
        if (fieldaddwidth.getText().trim().isEmpty()) {
            errorwidth.setText(resources.getString("errorwidth"));
            errorwidth.setVisible(true);
            isValid = false;
        } else {
            try {
                Double.parseDouble(fieldaddwidth.getText().trim());
            } catch (NumberFormatException e) {
                errorwidth.setText(resources.getString("errorwidth2"));
                errorwidth.setVisible(true);
                isValid = false;
            }
        }

        // Validar nombre de ONG
        if (fieldaddong1.getText().trim().isEmpty()) {
            errorongname.setText(resources.getString("errorongname"));
            errorongname.setVisible(true);
            isValid = false;
        }

        // Validar ubicación
        if (fieldaddplace1.getText().trim().isEmpty()) {
            errorcatplace1.setText(resources.getString("errorcatplace1"));
            errorcatplace1.setVisible(true);
            isValid = false;
        }

        // Validar Friendly with kids
        if (choiceadd1.getValue() == null || choiceadd1.getValue().isEmpty()) {
            erroroption1.setText(resources.getString("erroroption1"));
            erroroption1.setVisible(true);
            isValid = false;
        }

        // Validar Friendly with animals
        if (choiceadd2.getValue() == null || choiceadd2.getValue().isEmpty()) {
            erroroption2.setText(resources.getString("erroroption2"));
            erroroption2.setVisible(true);
            isValid = false;
        }

        // Validar Imágenes (presencia)
        if (addimage1.getImage() == null) {
            errorimage1.setText(resources.getString("errorimage1"));
            errorimage1.setVisible(true);
            isValid = false;
        }

        if (addimage2.getImage() == null) {
            errorimage2.setText(resources.getString("errorimage2"));
            errorimage2.setVisible(true);
            isValid = false;
        }

        if (addimage3.getImage() == null) {
            errorimage3.setText(resources.getString("errorimage3"));
            errorimage3.setVisible(true);
            isValid = false;
        }

        // Validar Video (presencia)
        if (addvideo1.getMediaPlayer() == null || addvideo1.getMediaPlayer().getMedia() == null) {
            errorvideo1.setText(resources.getString("errorvideo1"));
            errorvideo1.setVisible(true);
            isValid = false;
        }

        // Validar tamaño de imágenes
        if (selectedImageFile1 != null && selectedImageFile1.length() > MAX_IMAGE_SIZE) {
            errorimage1.setText(resources.getString("error.image_too_big"));
            errorimage1.setVisible(true);
            isValid = false;
        }

        if (selectedImageFile2 != null && selectedImageFile2.length() > MAX_IMAGE_SIZE) {
            errorimage2.setText(resources.getString("error.image_too_big"));
            errorimage2.setVisible(true);
            isValid = false;
        }

        if (selectedImageFile3 != null && selectedImageFile3.length() > MAX_IMAGE_SIZE) {
            errorimage3.setText(resources.getString("error.image_too_big"));
            errorimage3.setVisible(true);
            isValid = false;
        }

        // Validar tamaño del video
        if (addvideo1.getMediaPlayer() != null && addvideo1.getMediaPlayer().getMedia() != null) {
            File videoFile = new File(addvideo1.getMediaPlayer().getMedia().getSource().replace("file:/", ""));
            if (videoFile.length() > MAX_VIDEO_SIZE) {
                errorvideo1.setText(resources.getString("error.video_too_big"));
                errorvideo1.setVisible(true);
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean validateCatName() {
        String catName = fieldaddname.getText().trim();
        boolean isValid = true;

        // Validar que el nombre no esté vacío
        if (catName.isEmpty()) {
            errorname1.setVisible(true);
            isValid = false;
        } else {
            errorname1.setVisible(false);
        }

        // Validar que el nombre sea único
        Cat existingCat = catRepository.findByNameIgnoreCase(catName);
        if (existingCat != null) {
            errorDuplicateName.setVisible(true);
            isValid = false;
        } else {
            errorDuplicateName.setVisible(false);
        }

        return isValid;
    }



    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        successMessage.setVisible(false);
        hideAllErrorMessages(); // Ocultar mensajes de error anteriores

        // Validación de nombre obligatorio
        String catName = fieldaddname.getText().trim();
        if (catName.isEmpty()) {
            errorname1.setVisible(true);
            return;
        }

        // Validación de nombre único
        Cat existingCat = catRepository.findByNameIgnoreCase(catName);
        if (existingCat != null) {
            errorDuplicateName.setVisible(true);
            return;
        }

        // Validar el resto del formulario
        if (!validateForm()) return;

        try {
            Cat nuevoGato = new Cat();
            nuevoGato.setAdopted(false);
            nuevoGato.setName(catName);
            nuevoGato.setBreed(fieldaddbreed.getText().trim());
            nuevoGato.setAge(fieldaddage.getText().trim());
            nuevoGato.setSex(fieldaddsex.getText().trim());
            nuevoGato.setColor(fieldaddcolor.getText().trim());
            nuevoGato.setHeight(Double.parseDouble(fieldaddheight.getText().trim()));
            nuevoGato.setWidth(Double.parseDouble(fieldaddwidth.getText().trim()));
            nuevoGato.setFriendlyWithKids(choiceadd1.getValue());
            nuevoGato.setFriendlyWithAnimals(choiceadd2.getValue());
            nuevoGato.setDescription(areaadd.getText().trim());
            nuevoGato.setOngName(fieldaddong1.getText().trim());
            nuevoGato.setCatLocation(fieldaddplace1.getText().trim());
            nuevoGato.setOngPhone(fieldaddphone.getText().trim());
            if (fieldaddBornDate.getValue() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                nuevoGato.setBornDate(fieldaddBornDate.getValue().format(formatter));
            }

            // Guardar imágenes
            String imageId1 = saveImageToDatabase(addimage1.getImage(), selectedImageFile1);
            String imageId2 = saveImageToDatabase(addimage2.getImage(), selectedImageFile2);
            String imageId3 = saveImageToDatabase(addimage3.getImage(), selectedImageFile3);

            nuevoGato.setImageId1(imageId1);
            nuevoGato.setImageId2(imageId2);
            nuevoGato.setImageId3(imageId3);

            // Guardar video si existe
            if (addvideo1.getMediaPlayer() != null && addvideo1.getMediaPlayer().getMedia() != null) {
                String videoId = saveVideoToDatabase(addvideo1.getMediaPlayer().getMedia());
                nuevoGato.setVideoID(videoId);
            }
            // Validar nombre primero
            if (!validateCatName()) {
                return; // Detener si hay errores en el nombre
            }

            // Validar el resto del formulario
            if (!validateForm()) {
                return;
            }

            // Guardar gato en la base de datos
            catService.save(nuevoGato);

            // Actualizar vistas necesarias
            loadCatsIntoView();
            updateCatStatusDisplays();

            // Mostrar mensaje de éxito y limpiar formulario
            successMessage.setVisible(true);
            clearForm();

            // Navegar a web2.fxml
            setCurrentFxmlPath("/com/java/fx/web2.fxml");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/java/fx/web2.fxml"),
                    ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
            );
            loader.setControllerFactory(Main.context::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(600);
            stage.setMinHeight(700);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error saving the cat: " + e.getMessage());
        }
    }


    @FXML
    private void handleSearchBreed(MouseEvent event) {
        String breed = searchField.getText().trim();
        if (breed.isEmpty()) {
            showErrorAlert("search.empty");
            return;
        }

        List<Cat> searchResults = catRepository.findByBreedIgnoreCaseAndAdoptedFalse(breed);
        if (searchResults.isEmpty()) {
            String message = resources.getString("search.notfound").replace("{0}", breed);
            showErrorAlert(message);
            return;
        }

        try {
            setCurrentFxmlPath("/com/java/fx/BreedSearch.fxml");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/java/fx/BreedSearch.fxml"),
                    ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
            );
            loader.setControllerFactory(Main.context::getBean);
            Parent root = loader.load();

            UsuarioController controller = loader.getController();
            controller.showSearchResults(searchResults);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(600);
            stage.setMinHeight(700);

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading search view");
        }
    }


    public void showSearchResults(List<Cat> results) {
        this.searchResults = results;
        searchResultsContainer.getChildren().clear();

        for (Cat cat : results) {
            Node catCard = createCatCard(cat);
            searchResultsContainer.getChildren().add(catCard);
        }
    }

    @FXML
    private void handleBackFromSearch(MouseEvent event) throws IOException {
        handleGoToWeb2(event);
    }



    private void hideAllErrorMessages() {
        errorbreed.setVisible(false);
        errorage.setVisible(false);
        errorsex.setVisible(false);
        errorcolor.setVisible(false);
        errorheight.setVisible(false);
        errorwidth.setVisible(false);
        erroroption1.setVisible(false);
        erroroption2.setVisible(false);
        errordescription.setVisible(false);
        errorimage1.setVisible(false);
        errorimage2.setVisible(false);
        errorimage3.setVisible(false);
        errorvideo1.setVisible(false);
        errorname1.setVisible(false);
        errorongname.setVisible(false);
        errorcatplace1.setVisible(false);
        if (errorongname != null) errorongname.setVisible(false);
        if (errorcatplace1 != null) errorcatplace1.setVisible(false);
        if (errorDuplicateName != null) errorDuplicateName.setVisible(false);
        if (errorphone != null) errorphone.setVisible(false);
    }

    private void clearForm() {
        fieldaddbreed.clear();
        fieldaddage.clear();
        fieldaddname.clear();
        fieldaddphone.clear();
        fieldaddsex.clear();
        fieldaddcolor.clear();
        fieldaddheight.clear();
        fieldaddBornDate.setValue(null);
        fieldaddwidth.clear();
        choiceadd1.setValue(null);
        choiceadd2.setValue(null);
        areaadd.clear();
        addimage1.setImage(null);
        addimage2.setImage(null);
        addimage3.setImage(null);
        fieldaddong1.clear();
        fieldaddplace1.clear();
        if (addvideo1.getMediaPlayer() != null) {
            addvideo1.getMediaPlayer().stop();
            addvideo1.setMediaPlayer(null);
        }
        if (fieldAdditionalContact != null) fieldAdditionalContact.clear();
        if (errorDuplicateName != null) errorDuplicateName.setVisible(false);
    }

    private String saveImageToDatabase(javafx.scene.image.Image image, File originalFile) {
        if (image == null || originalFile == null) return null;

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // 1. Obtener el bucket de GridFS
            MongoDatabase database = mongoClient.getDatabase("test");
            GridFSBucket gridFSBucket = GridFSBuckets.create(database, "cats_images"); // ✅ Crear el bucket aquí

            // 2. Convertir Image de JavaFX a BufferedImage
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, getFileExtension(originalFile.getName()), baos);
            byte[] imageData = baos.toByteArray();

            // 3. Crear miniatura (opcional)
            BufferedImage thumbnail = createThumbnail(bImage, 100, 100);
            ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "jpg", thumbOutput);
            String thumbnailBase64 = Base64.getEncoder().encodeToString(thumbOutput.toByteArray());

            // 4. Subir a GridFS
            Document metadata = new Document("contentType", "image/" + getFileExtension(originalFile.getName()))
                    .append("uploadDate", new Date())
                    .append("thumbnail", thumbnailBase64);

            ObjectId fileId = gridFSBucket.uploadFromStream(
                    originalFile.getName(),
                    new ByteArrayInputStream(imageData),
                    new GridFSUploadOptions().metadata(metadata)
            );

            return fileId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateCatStatusDisplays() {
        List<Cat> cats = catRepository.findAll();

        for (Cat cat : cats) {
            String catName = cat.getName();
            if (catName == null) continue;
        }
    }


    private BufferedImage createThumbnail(BufferedImage original, int width, int height) {
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnail.createGraphics();

        // Usar la constante directamente con el nombre completo de la clase
        g.drawImage(original.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

        g.dispose();
        return thumbnail;
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public Image getImageFromDatabase(String imageId) {
        try {
            String imageUrl = "http://localhost:8080/api/usuario/image/" + imageId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                return new Image(new ByteArrayInputStream(response.body()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDefaultCatImage();
    }


    private String saveVideoToDatabase(Media media) {
        if (media == null) return null;

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Obtener el bucket de GridFS para videos
            MongoDatabase database = mongoClient.getDatabase("test");
            GridFSBucket gridFSBucket = GridFSBuckets.create(database, "cats_videos"); // Bucket separado para videos

            // Obtener el archivo de video desde la URL del Media
            URL mediaUrl = new URL(media.getSource());
            try (InputStream videoStream = mediaUrl.openStream()) {
                // Configurar metadata
                Document metadata = new Document()
                        .append("contentType", "video/mp4")
                        .append("uploadDate", new Date());

                // Subir el video a GridFS
                ObjectId fileId = gridFSBucket.uploadFromStream(
                        "cat_video_" + System.currentTimeMillis() + ".mp4",
                        videoStream,
                        new GridFSUploadOptions().metadata(metadata)
                );

                return fileId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error saving video: " + e.getMessage());
            return null;
        }
    }


    @FXML
    private void handleGoConditionsSection(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/termsandconditions.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/termsandconditions.fxml"));
        loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
        loader.setControllerFactory(Main.context::getBean);
        AnchorPane adoptPane = loader.load();
        AnchorPane currentRoot = (AnchorPane) ((Button) event.getSource()).getScene().getRoot();
        currentRoot.getChildren().setAll(adoptPane);
    }


    @FXML
    private void handleGoCatSection(Event event) throws IOException {
        // Detener cualquier reproducción en los controladores de detalles
        Stage[] stages = Stage.getWindows().toArray(new Stage[0]);
        for (Stage stage : stages) {
            if (stage.getScene() != null &&
                    stage.getScene().getRoot() != null &&
                    stage.getScene().getRoot().getUserData() instanceof CatDetailController) {

                CatDetailController controller = (CatDetailController) stage.getScene().getRoot().getUserData();
                controller.stopVideo();
            }
        }

        // También detener cualquier reproducción en el controlador principal
        stopCurrentPlayback();

        setCurrentFxmlPath("/com/java/fx/web2.fxml");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/web2.fxml"),
                ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
        );
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(700);

        Platform.runLater(() -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrol");
            if (scroll != null) {
                scroll.setVvalue(0.0);
            }
        });
        updateCatStatusDisplays();
    }



    @FXML
    private void handleGoCatSection2(MouseEvent event) throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        setCurrentFxmlPath("/com/java/fx/web2.fxml");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/web2.fxml"),
                ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
        );
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        // Crear escena sin dimensiones fijas
        Scene scene = new Scene(root);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(700);

        Platform.runLater(() -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrol");
            if (scroll != null) {
                scroll.setVvalue(0.0);
            }
        });
    }


    @FXML
    private void handleAdoptToMenu(MouseEvent event) throws IOException {
        setCurrentFxmlPath("/com/java/fx/main.fxml");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/main.fxml"),
                ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
        );
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();
        stage.getScene().setRoot(root);
        if (wasMaximized) {
            stage.setMaximized(true);
        }
    }


    @FXML
    private void handleONG(MouseEvent event) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            setCurrentFxmlPath("/com/java/fx/ongmenu.fxml");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/java/fx/ongmenu.fxml"),
                    ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
            );
            loader.setControllerFactory(Main.context::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();
            stage.getScene().setRoot(root);
            if (wasMaximized) {
                stage.setMaximized(true);
            }
        } catch (IOException e) {
            System.err.println("Error loading ONG menu: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Could not load ONG menu. File may be missing or corrupted.");
        }
    }

    @FXML
    private void handleAccessButton(MouseEvent event) throws IOException {
        String inputPassword = accessfield.getText();

        if ("Password".equals(inputPassword)) {
            errorongpassword.setVisible(false);

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            setCurrentFxmlPath("/com/java/fx/MenuAdd.fxml");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/java/fx/MenuAdd.fxml"),
                    ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
            );
            loader.setControllerFactory(Main.context::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();
            stage.getScene().setRoot(root);
            if (wasMaximized) {
                stage.setMaximized(true);
            }
        } else {
            errorongpassword.setText("Wrong password");
            errorongpassword.setVisible(true);
        }
    }

    @FXML
    private void handleAddImage1(MouseEvent event) {
        handleAddImageGeneric(event, () -> {
            this.selectedImageFile1 = fileChooser.showOpenDialog(null);
            return this.selectedImageFile1;
        }, addimage1);
    }

    @FXML
    private void handleAddImage2(MouseEvent event) {
        handleAddImageGeneric(event, () -> {
            this.selectedImageFile2 = fileChooser.showOpenDialog(null);
            return this.selectedImageFile2;
        }, addimage2);
    }

    @FXML
    private void handleAddImage3(MouseEvent event) {
        handleAddImageGeneric(event, () -> {
            this.selectedImageFile3 = fileChooser.showOpenDialog(null);
            return this.selectedImageFile3;
        }, addimage3);
    }

    private void handleAddImageGeneric(MouseEvent event, Supplier<File> fileSupplier, ImageView targetImageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del gato");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de imagen", "*.jpg", "*.png", "*.jpeg")
        );

        File selectedFile = fileSupplier.get();

        if (selectedFile != null) {
            if (selectedFile.length() > MAX_IMAGE_SIZE) {
                showErrorAlert("La imagen no puede ser mayor a 5MB");
                return;
            }

            try {
                Image image = new Image(selectedFile.toURI().toString());
                targetImageView.setImage(image);
            } catch (Exception e) {
                showErrorAlert("Error al cargar la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddVideo1(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Videos", "*.mp4"));

        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            if (file.length() > MAX_VIDEO_SIZE) {
                showErrorAlert("El video no puede ser mayor a 10MB");
                return;
            }

            try {
                Media media = new Media(file.toURI().toString());
                MediaPlayer player = new MediaPlayer(media);
                addvideo1.setMediaPlayer(player);
                player.play();
            } catch (Exception e) {
                showErrorAlert("Error al cargar el video: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleTwitter(MouseEvent event) {
        openURL("https://x.com/home");
    }

    @FXML
    private void handleLinkedin(MouseEvent event) {
        openURL("https://www.linkedin.com/feed/");
    }

    @FXML
    private void handlegmail(MouseEvent event) {
        openURL("https://www.gmail.com");
    }

    private void openURL(String url) {
        try {
            if (hostServices != null) {
                hostServices.showDocument(url);
            } else {
                // Fallback opcional
                System.out.println("HostServices no disponible. URL: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirmation(MouseEvent event) {
        // Ocultar todos los mensajes de advertencia
        warningPostal.setVisible(false);
        warningPhone.setVisible(false);
        warningAdress.setVisible(false);
        warningNameSur.setVisible(false);

        String name = fieldname.getText();
        String adress = fieldadress.getText();
        String postal = fieldpostal.getText();
        String phone = fieldphone.getText();
        String selectedCatName = selection.getValue();
        String additionalContact = fieldAdditionalContact.getText();

        boolean hasError = false;

        // Validaciones de campos
        if (name.isEmpty()) {
            warningNameSur.setText(resources.getString("error.name.empty"));
            warningNameSur.setVisible(true);
            hasError = true;
        }
        if (adress.isEmpty()) {
            warningAdress.setText(resources.getString("error.address.empty"));
            warningAdress.setVisible(true);
            hasError = true;
        }
        if (postal.isEmpty()) {
            warningPostal.setText(resources.getString("error.postal.empty"));
            warningPostal.setVisible(true);
            hasError = true;
        } else if (!postal.matches("\\d+")) {
            warningPostal.setText(resources.getString("error.postal.non_numeric"));
            warningPostal.setVisible(true);
            hasError = true;
        }
        if (phone.isEmpty()) {
            warningPhone.setText(resources.getString("error.phone.empty"));
            warningPhone.setVisible(true);
            hasError = true;
        } else if (!phone.matches("\\d+")) {
            warningPhone.setText(resources.getString("error.phone.non_numeric"));
            warningPhone.setVisible(true);
            hasError = true;
        }

        // Validar selección del gato
        if (selectedCatName == null || selectedCatName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resources.getString("error.title"));
            alert.setHeaderText(null);
            alert.setContentText(resources.getString("error.cat_not_selected"));
            alert.showAndWait();
            return;
        }

        ObjectId catId = catNameToIdMap.get(selectedCatName);
        if (catId == null) {
            showErrorAlert(resources.getString("error.cat_not_found"));
            return;
        }

        if (hasError) return;

        // Mostrar diálogo de confirmación
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(resources.getString("confirmation.title"));
        confirmationDialog.setHeaderText(MessageFormat.format(
                resources.getString("confirmation.header"),
                selectedCatName
        ));
        confirmationDialog.setContentText(MessageFormat.format(
                resources.getString("confirmation.content"),
                selectedCatName
        ));

        // Botones traducidos
        ButtonType yesButton = new ButtonType(resources.getString("yes"));
        ButtonType noButton = new ButtonType(resources.getString("no"));
        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            processAdoption(catId, additionalContact);
        }
    }



    private void processAdoption(ObjectId catId, String additionalContact) {
        // Obtener el gato seleccionado por ID
        Cat selectedCat = catRepository.findById(catId).orElse(null);

        if (selectedCat == null) {
            showErrorAlert(resources.getString("error.cat_not_found"));
            return;
        }

        if (selectedCat.isAdopted()) {
            showErrorAlert(resources.getString("error.cat_already_adopted"));
            return;
        }

        // Marcar como adoptado y guardar
        selectedCat.setAdopted(true);
        catService.save(selectedCat);

        // Crear y guardar objeto de adopción
        Adopcion adopcion = new Adopcion();
        adopcion.setNameSurname(fieldname.getText());
        adopcion.setAdress(fieldadress.getText());
        adopcion.setPostal(fieldpostal.getText());
        adopcion.setPhone(fieldphone.getText());
        adopcion.setCatName(selectedCat.getName());
        adopcion.setAdditionalContact(additionalContact);

        if (currentUser != null) {
            adopcion.setUserId(currentUser.getId());
        }

        adopcionService.save(adopcion);

        // Mostrar mensaje de éxito
        congratulations.setText(resources.getString("adoption.success"));
        congratulations.setTextFill(Color.GREEN);
        congratulations.setVisible(true);

        // Limpiar campos del formulario
        fieldname.clear();
        fieldadress.clear();
        fieldpostal.clear();
        fieldphone.clear();
        fieldAdditionalContact.clear();
        selection.setValue(null);
    }



    public void openAdoptionScreen(Cat selectedCat) {
        try {
            setCurrentCat(selectedCat); // Guardar el gato seleccionado

            setCurrentFxmlPath("/com/java/fx/adoptweb.fxml");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/java/fx/adoptweb.fxml"),
                    ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
            );
            loader.setControllerFactory(Main.context::getBean);
            Parent root = loader.load();

            UsuarioController controller = loader.getController();
            controller.setCurrentCat(selectedCat); // Pasar el gato al controlador

            // Añade esta línea para preseleccionar el gato
            controller.preselectCatInChoiceBox(selectedCat.getName());

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(600);
            stage.setMinHeight(700);
            stage.setTitle("Adopt " + selectedCat.getName());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Could not open adoption screen: " + e.getMessage());
        }
    }

    public void preselectCatInChoiceBox(String catName) {
        if (selection != null && selection.getItems().contains(catName)) {
            selection.setValue(catName);
        }
    }



    private void handleCatClick(MouseEvent event, Cat cat) {
        try {
            stopCurrentPlayback();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/Generic.fxml"));
            loader.setResources(ResourceBundle.getBundle("Messages", Main.getCurrentLocale()));
            loader.setControllerFactory(Main.context::getBean);

            Parent root = loader.load();
            CatDetailController controller = loader.getController();
            controller.setCurrentCat(cat);
            controller.updateCatDetails();

            // Asociar controlador con la raíz para poder encontrarlo después
            root.setUserData(controller);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle(cat.getName() + " - Details");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.centerOnScreen();
            stage.show();

            stage.setOnCloseRequest(e -> controller.cleanUp());

        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorAlert("Error loading cat details: " + ex.getMessage());
        }
    }


    private void handlePlayVideoGeneric(MouseEvent event, String videoPath, MediaView targetMediaView) {
        playVideo(videoPath, targetMediaView);
    }

    private void playVideo(String videoPath, MediaView targetMediaView) {
        try {
            // Limpiar reproducción anterior
            stopCurrentPlayback();

            // Cargar nuevo video desde recursos -> archivo temporal
            InputStream inputStream = getClass().getResourceAsStream(videoPath);
            if (inputStream == null) {
                throw new RuntimeException("Video resource not found: " + videoPath);
            }

            // Crear archivo temporal
            File tempFile = File.createTempFile("video_", ".mp4");
            tempFile.deleteOnExit();

            // Copiar contenido del recurso al archivo temporal
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            currentMedia = new Media(tempFile.toURI().toString());
            mediaPlayer = new MediaPlayer(currentMedia);
            currentMediaView = targetMediaView;
            currentMediaView.setMediaPlayer(mediaPlayer);

            // Reproducción en bucle
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setOnError(() ->
                    System.err.println("Media error: " + mediaPlayer.getError().getMessage())
            );

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopCurrentPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (currentMediaView != null) {
            currentMediaView.setMediaPlayer(null);
            currentMediaView = null;
        }
        currentMedia = null;
    }


    @FXML
    public void setView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(Main.context::getBean);
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PostMapping
    public ResponseEntity<Usuario> save(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.save(usuario), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Usuario>> getUsuarios() {
        return new ResponseEntity<>(usuarioService.getUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoClient.getDatabase("test"), "cats_images");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(new ObjectId(id), outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            // Determinar el tipo de contenido basado en la metadata
            GridFSFile file = gridFSBucket.find(new Document("_id", new ObjectId(id))).first();
            String contentType = "image/jpeg"; // valor por defecto
            if (file != null && file.getMetadata() != null) {
                contentType = file.getMetadata().getString("contentType");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/images")
    public ResponseEntity<List<Map<String, Object>>> getAllImages() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoClient.getDatabase("test"), "cats_images");

            List<Map<String, Object>> images = new ArrayList<>();

            gridFSBucket.find().forEach(file -> {
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("id", file.getObjectId().toString());
                imageInfo.put("filename", file.getFilename());
                imageInfo.put("uploadDate", file.getUploadDate());
                imageInfo.put("length", file.getLength());
                imageInfo.put("contentType", file.getMetadata() != null ?
                        file.getMetadata().getString("contentType") : "unknown");

                images.add(imageInfo);
            });

            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<byte[]> getVideo(@PathVariable String id) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoClient.getDatabase("test"), "cats_videos");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(new ObjectId(id), outputStream);

            byte[] videoData = outputStream.toByteArray();

            // Obtener metadata para el tipo de contenido
            GridFSFile file = gridFSBucket.find(new Document("_id", new ObjectId(id))).first();
            String contentType = "video/mp4"; // valor por defecto
            if (file != null && file.getMetadata() != null) {
                contentType = file.getMetadata().getString("contentType");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(videoData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable ObjectId id, @RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.update(id, usuario), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ObjectId id) {
        usuarioService.delete(id);
        return new ResponseEntity<>("Eliminated user!!", HttpStatus.OK);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Locale actual del sistema: " + Locale.getDefault());

        try {
            // 1. Usar el bundle proporcionado por FXMLLoader si está disponible
            if (resources != null) {
                this.resources = resources;
            } else {
                // Fallback si no viene del FXMLLoader
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                this.resources = ResourceBundle.getBundle("Messages", Main.getCurrentLocale(), classLoader);
            }

            System.out.println("Locale actual: " + Main.getCurrentLocale());
            System.out.println("Recursos cargados en initialize: " + this.resources.getLocale());

            // 2. Actualizar textos UI traducidos
            updateUITexts(this.resources);

            // 3. Inicializar la ruta FXML actual si estamos en la vista principal
            if (AnchorMain != null && currentFxmlPath == null) {
                currentFxmlPath = "/com/java/fx/main.fxml";
            }


            // 4. Reiniciar la paginación
            currentPage = 0;

            // 5. Inicializar componentes UI
            initializeUIComponents();

            // 6. Configurar cursores para imágenes clicables
            setupClickableCursors();

            // 7. Configurar manejadores de clic
            setupImageClickHandlers();

            // 8. Configurar fuentes para etiquetas de error si existen
            if (errorage2 != null) errorage2.setFont(Font.font(5));
            if (errorwidth2 != null) errorwidth2.setFont(Font.font(5));
            if (errorheight2 != null) errorheight2.setFont(Font.font(5));
            if (errorname1 != null) errorname1.setFont(Font.font(7));

            // 9. Depuración de campos nulos (opcional)
            System.out.println("errorage2 is null: " + (errorage2 == null));
            System.out.println("errorwidth2 is null: " + (errorwidth2 == null));
            System.out.println("errorheight2 is null: " + (errorheight2 == null));

            // 10. Cargar imágenes sociales si el panel está visible
            if (AnchorOng != null && AnchorOng.isVisible()) {
                safeLoadImage(ximage, "twitter.jpg", true);
                safeLoadImage(linkedinimage, "LinkedIn.jpg", true);
                safeLoadImage(gmailimage, "gmail.jpg", true);
            }
            //Checkear si hay algo duplicao
            if (errorDuplicateName != null) {
                errorDuplicateName.setVisible(false);
                errorDuplicateName.setFont(Font.font(7)); // Tamaño consistente
            }

            // 11. Inicializar opciones en ChoiceBoxes con traducciones
            ObservableList<String> friendlyOptions = FXCollections.observableArrayList(
                    this.resources.getString("friendly.yes"),
                    this.resources.getString("friendly.no"),
                    this.resources.getString("friendly.dont_know"),
                    this.resources.getString("friendly.takes_time")
            );
            if (choiceadd1 != null) choiceadd1.setItems(friendlyOptions);
            if (choiceadd2 != null) choiceadd2.setItems(friendlyOptions);

            // 12. Configurar tooltips para campos adicionales
            if (fieldaddong1 != null) {
                Tooltip.install(fieldaddong1, new Tooltip(this.resources.getString("tooltip.ongname")));
            }
            if (fieldaddplace1 != null) {
                Tooltip.install(fieldaddplace1, new Tooltip(this.resources.getString("tooltip.catplace")));
            }

            // 13. Cargar datos de gatos
            loadCats();

            // 14. Ejecutar tareas diferidas en UI thread
            Platform.runLater(() -> {
                // Configuración SOLO para elementos visibles
                if (previouspage != null && previouspage.isVisible()) {
                    previouspage.setMinWidth(100);
                }
                if (nextpage != null && nextpage.isVisible()) {
                    nextpage.setMinWidth(100);
                }
                if (adopt != null) {
                    adopt.setMinWidth(80);
                }
                if (searchButton != null) {
                    searchButton.setMinWidth(80);
                }
                if (searchField != null) {
                    searchField.setMinWidth(120);
                    HBox.setHgrow(searchField, Priority.ALWAYS);
                }

                // Configuración especial para "Ir a log in"
                if (goToLoginButton != null && goToLoginButton.isVisible()) {
                    goToLoginButton.setMinWidth(100);
                }

                // Actualizar vista
                updateCatStatusDisplays();
                if (catsContainer != null) {
                    loadCatsIntoView();
                }
                updatePaginationButtons();
            });

            // 15. Inicializar conexión a base de datos
            mongoDatabase = getMongoDatabase();

            // 16. Asegurar que AnchorAdopt sea redimensionable al 100%
            Platform.runLater(() -> {
                if (AnchorAdopt != null) {
                    AnchorPane.setTopAnchor(AnchorAdopt, 0.0);
                    AnchorPane.setBottomAnchor(AnchorAdopt, 0.0);
                    AnchorPane.setLeftAnchor(AnchorAdopt, 0.0);
                    AnchorPane.setRightAnchor(AnchorAdopt, 0.0);
                }
            });

        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();
            // Fallback si falla la carga con locale
            this.resources = ResourceBundle.getBundle("Messages");
        }

        // Validación en tiempo real para la descripción
        if (areaadd != null) {
            areaadd.textProperty().addListener((observable, oldValue, newValue) -> {
                // Validar longitud máxima
                if (newValue.length() > MAX_DESCRIPTION_CHARS) {
                    Platform.runLater(() -> {
                        areaadd.setText(oldValue);
                        errordescription.setText(
                                resources.getString("error.description_chars") +
                                        " (" + newValue.length() + "/" + MAX_DESCRIPTION_CHARS + ")"
                        );
                        errordescription.setVisible(true);
                    });
                    return;
                }

                // Validar número de líneas
                int lineCount = newValue.split("\n").length;
                if (lineCount > MAX_DESCRIPTION_LINES) {
                    Platform.runLater(() -> {
                        areaadd.setText(oldValue);
                        errordescription.setText(
                                resources.getString("error.description_lines") +
                                        " (" + lineCount + "/" + MAX_DESCRIPTION_LINES + ")"
                        );
                        errordescription.setVisible(true);
                    });
                    return;
                }

                // Si pasa las validaciones, ocultar el error
                errordescription.setVisible(false);

                // ——— AÑADIDO: contador y cambio de color ———
                if (descriptionCounter != null) {
                    descriptionCounter.setText(
                            newValue.length() + "/" + MAX_DESCRIPTION_CHARS +
                                    " | Lines: " + lineCount + "/" + MAX_DESCRIPTION_LINES
                    );

                    // Cambiar color si se exceden los límites
                    if (newValue.length() > MAX_DESCRIPTION_CHARS || lineCount > MAX_DESCRIPTION_LINES) {
                        descriptionCounter.setTextFill(Color.RED);
                    } else {
                        descriptionCounter.setTextFill(Color.BLACK);
                    }
                }
                // ————————————————————————————————
            });
        }
        if (suggestionMessage != null) {
            suggestionMessage.setVisible(false);
        }
        if (suggestionTextArea != null) {
            suggestionTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                validateTextArea(
                        suggestionTextArea,
                        suggestionCounter,
                        MAX_SUGGESTION_CHARS,
                        MAX_SUGGESTION_LINES,
                        oldValue
                );
            });
        }
        if (reviewComment != null) {
            reviewComment.textProperty().addListener((observable, oldValue, newValue) -> {
                validateTextArea(
                        reviewComment,
                        reviewCounter,
                        MAX_REVIEW_CHARS,
                        MAX_REVIEW_LINES,
                        oldValue
                );
            });
        }
    }

    // Método reusable para validación
    private void validateTextArea(TextArea textArea, Label counter,
                                  int maxChars, int maxLines, String oldValue) {
        String newValue = textArea.getText();

        // Validar longitud máxima
        if (newValue.length() > maxChars) {
            Platform.runLater(() -> textArea.setText(oldValue));
            return;
        }

        // Validar número de líneas
        int lineCount = newValue.split("\n").length;
        if (lineCount > maxLines) {
            Platform.runLater(() -> textArea.setText(oldValue));
            return;
        }

        // Actualizar contador
        if (counter != null) {
            counter.setText(lineCount + "/" + maxLines + " líneas | " +
                    newValue.length() + "/" + maxChars + " caracteres");

            // Cambiar color si se acerca al límite
            if (newValue.length() > maxChars * 0.9 || lineCount > maxLines * 0.9) {
                counter.setTextFill(Color.ORANGE);
            } else {
                counter.setTextFill(Color.GRAY);
            }
        }
    }




    private void updateUITexts(ResourceBundle bundle) {
        if (bundle == null) {
            System.out.println("Bundle es nulo!");
            return;
        }

        System.out.println("Actualizando textos con bundle: " + bundle.getLocale());

        // Componentes principales
        safeSetText(welcome, "welcome", bundle);
        safeSetText(username, "username", bundle);
        safeSetText(password, "password", bundle);
        safeSetText(suggestionMessage, "suggestions.success", bundle);
        safeSetText(registeredusers, "registeredusers", bundle);
        safeSetText(addusername, "addusername", bundle);
        safeSetText(addemail, "addemail", bundle);
        safeSetText(addage, "addage", bundle);
        safeSetText(addpassword, "addpassword", bundle);
        safeSetText(statusLabel, "statusLabel", bundle);
        safeSetText(catNameLabel, "cat.name", bundle);
        safeSetText(breedLabel, "cat.breed", bundle);
        safeSetText(ageLabel, "cat.age", bundle);
        safeSetText(heightLabel, "cat.height", bundle);
        safeSetText(widthLabel, "cat.width", bundle);
        safeSetText(colorLabel, "cat.color", bundle);
        safeSetText(errorphone, "error.ongphone", bundle);
        safeSetText(sexLabel, "cat.sex", bundle);
        safeSetText(friendlyKidsLabel, "cat.friendly.kids", bundle);
        safeSetText(friendlyAnimalsLabel, "cat.friendly.animals", bundle);
        safeSetText(personality1Label, "cat.personality", bundle);
        safeSetText(DateLabel, "cat.born.date", bundle);

        // Sección de registro
        safeSetText(tituloregister, "tituloregister", bundle);
        safeSetText(warning, "warning", bundle);
        safeSetText(congratulations, "congratulations", bundle);
        safeSetText(found, "found", bundle);
        safeSetText(information, "information", bundle);
        safeSetText(name, "name", bundle);
        safeSetText(adress, "adress", bundle);
        safeSetText(postal, "postal", bundle);
        safeSetText(select, "select", bundle);
        safeSetText(phone, "phone", bundle);

        // Advertencias
        safeSetText(warning2, "warning2", bundle);
        safeSetText(warning3, "warning3", bundle);
        safeSetText(warning4, "warning4", bundle);
        safeSetText(warningPostal, "warningPostal", bundle);
        safeSetText(warningPhone, "warningPhone", bundle);
        safeSetText(warningAdress, "warningAdress", bundle);
        safeSetText(warningNameSur, "warningNameSur", bundle);

        // Sección ONG
        safeSetText(theconditions, "theconditions", bundle);
        safeSetText(label1ong, "label1ong", bundle);
        safeSetText(label2ong, "label2ong", bundle);
        safeSetText(label3ong, "label3ong", bundle);
        safeSetText(label4ong, "label4ong", bundle);
        safeSetText(label5ong, "label5ong", bundle);
        safeSetText(label6ong, "label6ong", bundle);
        safeSetText(label7ong, "label7ong", bundle);
        safeSetText(label8ong, "label8ong", bundle);
        safeSetText(label9ong, "label9ong", bundle);
        safeSetText(errorongpassword, "errorongpassword", bundle);
        safeSetText(errorBornDate, "error.born_date", bundle);
        safeSetText(DateLabel, "dateLabel", bundle);
        safeSetText(statusLabel, "statusLabel", bundle);

        // Sección añadir gatos
        safeSetText(breedadd, "breedadd", bundle);
        safeSetText(ageadd, "ageadd", bundle);
        safeSetText(coloradd, "coloradd", bundle);
        safeSetText(sexadd, "sexadd", bundle);
        safeSetText(heightadd, "heightadd", bundle);
        safeSetText(widthadd, "widthadd", bundle);
        safeSetText(image1add, "image1add", bundle);
        safeSetText(image2add, "image2add", bundle);
        safeSetText(image3add, "image3add", bundle);
        safeSetText(video1add, "video1add", bundle);
        safeSetText(friendlykidsadd, "friendlykidsadd", bundle);
        safeSetText(friendlyanimalsadd, "friendlyanimalsadd", bundle);
        safeSetText(descriptionadd, "descriptionadd", bundle);
        safeSetText(labelmenuadd1, "labelmenuadd1", bundle);

        // Botones
        safeSetText(register, "register", bundle);
        safeSetText(registeruser, "registeruser", bundle);
        safeSetText(goback, "goback", bundle);
        safeSetText(login, "login", bundle);
        safeSetText(adopt, "adopt", bundle);
        safeSetText(nextpage, "nextpage", bundle);
        safeSetText(previouspage, "previouspage", bundle);
        safeSetText(gomenu, "gomenu", bundle);
        safeSetText(back, "back", bundle);
        safeSetText(back1, "back1", bundle);
        safeSetText(confirmation, "confirmation", bundle);
        safeSetText(notaccept, "notaccept", bundle);
        safeSetText(aceptbutton, "aceptbutton", bundle);
        safeSetText(ong, "ong", bundle);
        safeSetText(accessbutton, "accessbutton", bundle);
        safeSetText(menuaddtomenu, "menuaddtomenu", bundle);
        safeSetText(submitadd, "submitadd", bundle);
        safeSetText(add1button, "add1button", bundle);
        safeSetText(add2button, "add2button", bundle);
        safeSetText(add3button, "add3button", bundle);
        safeSetText(add4button, "add4button", bundle);
        safeSetText(adoptButton, "adoptButton", bundle);
        safeSetText(returnButton, "returnButton", bundle);
        safeSetText(playVideoButton, "playVideoButton", bundle);
        safeSetText(goToLoginButton, "goToLoginButton", bundle);

        // CheckBoxes
        if (accept != null) {
            accept.setText(bundle.getString("accept.text"));
        }
        //safeSetText(accept, "accept.text", bundle); // Cambiado a "accept.text"
        safeSetText(pleasecheck, "pleasecheck", bundle);
        updateDynamicTexts(bundle);
        if (welcome != null) {
            System.out.println("Welcome text: " + welcome.getText());
        }
        if (register != null) {
            System.out.println("Register button text: " + register.getText());
        }
    }

    @FXML
    private void handleSpanish(MouseEvent event) {
        Main.setCurrentLocale(new Locale("es"));
        reloadCurrentView(event);
    }

    @FXML
    private void handleEnglish(MouseEvent event) {
        Main.setCurrentLocale(Locale.ENGLISH);
        reloadCurrentView(event);
    }


    private void reloadCurrentView(MouseEvent event) {
        try {
            stopCurrentPlayback();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(currentFxmlPath));

            // Forzar recarga completa del bundle
            ResourceBundle.clearCache();
            loader.setResources(ResourceBundle.getBundle("Messages", Main.currentLocale));

            loader.setControllerFactory(Main.context::getBean);
            Parent root = loader.load();

            UsuarioController controller = loader.getController();
            controller.setCurrentFxmlPath(this.currentFxmlPath);

            // Actualizar estado interno
            controller.currentPage = this.currentPage;
            controller.allCats = this.allCats;

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.sizeToScene();

            // Actualizar UI después de cargar
            Platform.runLater(() -> {
                controller.updatePaginationButtons();
                controller.loadCatsIntoView();
            });
        } catch (Exception e) {
            showErrorAlert("error.reloading_view");
        }
    }

    private void updateDynamicTexts(ResourceBundle bundle) {
        // Actualizar contador de usuarios
        if (count != null && listview != null) {
            count.setText(String.valueOf(listview.getItems().size()));
        }

        // Actualizar ChoiceBox si está visible
        if (selection != null) {
            List<Cat> availableCats = catRepository.findByAdoptedFalse();
            ObservableList<String> catNames = FXCollections.observableArrayList();

            for (Cat cat : availableCats) {
                catNames.add(cat.getName());
            }

            selection.setItems(catNames);
            selection.setValue(null);
        }
    }









    private void setupImageClickHandlers() {
        setClickHandler(catImage1, e -> {
            if (currentCat != null) {
                loadImage(currentCat.getImageId1(), catImage1);
            }
        });

        setClickHandler(catImage2, e -> {
            if (currentCat != null) {
                loadImage(currentCat.getImageId2(), catImage2);
            }
        });

        setClickHandler(catImage3, e -> {
            if (currentCat != null) {
                loadImage(currentCat.getImageId3(), catImage3);
            }
        });
    }


    public void updateResourcesForCurrentView() {
        System.out.println("Actualizando recursos para vista: " + currentFxmlPath);

        // 1. Recargar ResourceBundle
        this.resources = ResourceBundle.getBundle("Messages", Main.currentLocale);

        // 2. Actualizar textos estáticos
        updateUITexts(this.resources);

        // 3. Actualizar ChoiceBoxes
        initializeChoiceBoxes();

        // 4. Actualizar lista de gatos disponibles
        if (selection != null) {
            initializeAdoptView();
        }

        // 5. Actualizar paginación
        updatePaginationButtons();

        // 6. Forzar actualización de UI
        Platform.runLater(() -> {
            if (catsContainer != null) {
                catsContainer.requestLayout();
            }
        });
    }

    private void safeLoadImage(ImageView imageView, String imagePath, boolean debug) {
        if (imageView == null) return;

        try (InputStream stream = getClass().getResourceAsStream("/assets/" + imagePath)) {
            if (stream != null) {
                imageView.setImage(new javafx.scene.image.Image(stream)); // Especificar el tipo completo
            }
        } catch (Exception e) {
            if (debug) e.printStackTrace();
        }
    }

    private void loadAllImagesAndMedia() {
        // No mostrar mensajes de error para componentes nulos
        boolean debug = false;

        if (isViewActive(AnchorMain) || isViewActive(AnchorMain2)) {
            loadMainViewResources(debug);
        } else if (isViewActive(AnchorRegister)) {
            // No necesita recursos
        } else if (isViewActive(AnchorAdopt)) {
            // No necesita recursos específicos
        } else if (isViewActive(AnchorGeneric)) {
            // No necesita recursos específicos
        } else if (isViewActive(AnchorConditions)) {
            // No necesita recursos
        }
    }

    private boolean isViewActive(AnchorPane pane) {
        return pane != null && pane.isVisible();
    }

    private void loadMainViewResources(boolean debug) {

    }

    private void loadRegisterViewResources() {
        // No necesita cargar imágenes específicas
    }



    private void loadConditionsViewResources() {
        // No necesita cargar imágenes específicas
    }

    private void loadMediaSafe(MediaView mediaView, String mediaPath, boolean debug) {
        if (mediaView == null) {
            if (debug) System.err.println("MediaView es nulo, no se puede cargar: " + mediaPath);
            return;
        }

        try {
            URL mediaUrl = getClass().getResource("/assets/" + mediaPath);
            if (mediaUrl != null) {
                Media media = new Media(mediaUrl.toExternalForm());
                MediaPlayer player = new MediaPlayer(media);
                mediaView.setMediaPlayer(player);
            } else if (debug) {
                System.err.println("No se encontró el recurso de video: " + mediaPath);
            }
        } catch (Exception e) {
            if (debug) System.err.println("Error cargando video " + mediaPath + ": " + e.getMessage());
        }
    }


    private void setClickHandler(ImageView imageView, Consumer<MouseEvent> handler) {
        if (imageView != null) {
            imageView.setOnMouseClicked(event -> {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    System.err.println("Error al manejar clic: " + e.getMessage());
                    e.printStackTrace();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error al cargar página");
                        alert.setContentText("No se pudo cargar la página solicitada.");
                        alert.showAndWait();
                    });
                }
            });
        }
    }

    private void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resources.getString("alert.error.title"));
            alert.setHeaderText(null);

            // Intentar obtener el mensaje del bundle
            try {
                alert.setContentText(resources.getString(message));
            } catch (MissingResourceException e) {
                // Si no existe la clave, usar el mensaje directamente
                alert.setContentText(message);
            }

            alert.showAndWait();
        });
    }


    private void setupClickableCursors() {
        // Lista segura de todos los ImageView clickeables
        List<ImageView> clickableImages = Arrays.asList(
                 ximage, linkedinimage, gmailimage, spanish, english
                // ... agregar todos los ImageView necesarios
        );

        // Configurar cursores solo para ImageView no nulos
        clickableImages.forEach(imageView -> {
            if (imageView != null) {
                imageView.setOnMouseEntered(e -> imageView.setCursor(Cursor.HAND));
                imageView.setOnMouseExited(e -> imageView.setCursor(Cursor.DEFAULT));
            }
        });
    }

    private void initializeUIComponents() {
        try {
            System.out.println("Inicializando componentes UI...");

            // Inicializar ListView
            if (listview != null && usuarioRepository != null) {
                listview.setItems(FXCollections.observableArrayList(usuarioRepository.findAll()));
                System.out.println("ListView actualizado con " + listview.getItems().size() + " usuarios");
            }

            // Inicializar contador
            if (count != null && listview != null) {
                count.setText(String.valueOf(listview.getItems().size()));
                System.out.println("Contador actualizado: " + count.getText());
            }

            // Inicializar ChoiceBox con datos de MongoDB
            if (selection != null && catRepository != null) {
                List<Cat> availableCats = catRepository.findByAdoptedFalse();
                catNames.clear();  // Limpiar lista existente
                catNameToIdMap.clear();  // Limpiar mapa existente

                for (Cat cat : availableCats) {
                    String catName = cat.getName();
                    if (catName != null && !catName.trim().isEmpty()) {
                        catNames.add(catName);
                        catNameToIdMap.put(catName, cat.getId());
                    }
                }

                selection.setItems(catNames);
                System.out.println("ChoiceBox cargado con " + catNames.size() + " gatos disponibles");
            }

            // Configurar CheckBoxes
            setupCheckBox(pleasecheck, confirmation);
            setupCheckBox(accept, aceptbutton);

        } catch (Exception e) {
            System.err.println("Error inicializando componentes UI: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void initializeAdoptView() {
        if (selection == null) return;

        List<Cat> availableCats = catRepository.findByAdoptedFalse();
        ObservableList<String> catNames = FXCollections.observableArrayList();
        catNameToIdMap.clear(); // Limpiar el mapa previo

        for (Cat cat : availableCats) {
            if (cat.getName() != null && !cat.getName().trim().isEmpty()) {
                String name = cat.getName().trim();
                catNames.add(name);
                catNameToIdMap.put(name, cat.getId());
            }
        }

        selection.setItems(catNames);
    }

    private void setupCheckBox(CheckBox checkBox, Button button) {
        if (checkBox != null && button != null) {
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                button.setDisable(!newVal);
                button.setStyle(newVal ?
                        "-fx-opacity: 1.0; -fx-cursor: hand;" :
                        "-fx-opacity: 0.7; -fx-cursor: default;");
            });
        }
    }

    private void loadCatsIntoView() {
        if (catsContainer == null) {
            System.out.println("catsContainer es nulo!");
            return;
        }

        System.out.println("Cargando gatos en vista...");

        Platform.runLater(() -> {
            updatePage();
            updatePaginationButtons();

            // Depuración adicional
            System.out.println("Gatos cargados: " + catsContainer.getChildren().size());
            if (nextpage != null) {
                System.out.println("Botón siguiente habilitado: " + !nextpage.isDisabled());
            }
        });
    }

    private VBox createCatCard(Cat cat) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
        card.setPrefSize(250, 300);

        ImageView catImage = new ImageView();
        catImage.setFitWidth(200);
        catImage.setFitHeight(200);
        catImage.setPreserveRatio(true);
        catImage.setCursor(Cursor.HAND);

        // Cargar imagen
        if (cat.getImageId1() != null) {
            Image image = getImageFromDatabase(cat.getImageId1());
            catImage.setImage(image != null ? image : getDefaultCatImage());
        } else {
            catImage.setImage(getDefaultCatImage());
        }

        // Configurar manejador de clic SIMPLIFICADO
        catImage.setOnMouseClicked(ev -> handleCatClick(ev, cat));

        Label nameLabel = new Label(cat.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label breedLabel = new Label("Breed: " + cat.getBreed());
        Label statusLabel = new Label("Status: " + (cat.isAdopted() ? "Adopted" : "Available"));
        statusLabel.setTextFill(cat.isAdopted() ? Color.RED : Color.GREEN);

        card.getChildren().addAll(catImage, nameLabel, breedLabel, statusLabel);
        return card;
    }


    private VBox createCatBox(Cat cat) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setCursor(Cursor.HAND); // Cambiar cursor a mano al pasar sobre la imagen

        // Cargar imagen desde MongoDB
        if (cat.getImageId1() != null) {
            Image image = getImageFromDatabase(cat.getImageId1());
            imageView.setImage(image != null ? image : getDefaultCatImage());
        } else {
            imageView.setImage(getDefaultCatImage());
        }

        // Configurar manejador de clic para la imagen
        imageView.setOnMouseClicked(e -> {
            try {
                // Cargar la vista de detalles del gato
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/Generic.fxml"));
                loader.setControllerFactory(Main.context::getBean);
                Parent root = loader.load();

                // Obtener el controlador y establecer el gato actual
                UsuarioController controller = loader.getController();
                controller.setCurrentCat(cat);
                controller.updateCatDetails();

                // Mostrar la nueva escena
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle(cat.getName() + " - Details");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                showErrorAlert("Could not load cat details: " + ex.getMessage());
            }
        });

        Label nameLabel = new Label(cat.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label breedLabel = new Label("Breed: " + cat.getBreed());
        Label statusLabel = new Label("Status: " + (cat.isAdopted() ? "Adopted" : "Available"));
        statusLabel.setTextFill(cat.isAdopted() ? Color.RED : Color.GREEN);

        card.getChildren().addAll(imageView, nameLabel, breedLabel, statusLabel);
        return card;
    }



    private Node createCatPane(Cat cat) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
        card.setPrefSize(250, 300);

        ImageView catImage = new ImageView();
        catImage.setFitWidth(200);
        catImage.setFitHeight(200);
        catImage.setPreserveRatio(true);
        catImage.setCursor(Cursor.HAND);
        Label bornDateLabel = new Label("Born: " + cat.getBornDate());
        card.getChildren().add(bornDateLabel);

        // Cargar imagen
        if (cat.getImageId1() != null) {
            Image image = getImageFromDatabase(cat.getImageId1());
            catImage.setImage(image != null ? image : getDefaultCatImage());
        } else {
            catImage.setImage(getDefaultCatImage());
        }

        // Configurar manejador de clic
        catImage.setOnMouseClicked(e -> handleCatClick(e, cat));

        Label nameLabel = new Label(cat.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label breedLabel = new Label("Breed: " + cat.getBreed());

        Label statusLabel = new Label("Status: " + (cat.isAdopted() ? "Adopted" : "Available"));
        statusLabel.setTextFill(cat.isAdopted() ? Color.RED : Color.GREEN);

        card.getChildren().addAll(catImage, nameLabel, breedLabel, statusLabel);
        return card;
    }

    private Image getDefaultCatImage() {
        try {
            InputStream stream = getClass().getResourceAsStream("/assets/default_cat.jpg");
            return stream != null ? new Image(stream) : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Llamar este método al cargar web2.fxml
    @FXML
    private void handleNextPage(MouseEvent event) {
        if (catRepository == null) return;

        List<Cat> cats = catRepository.findAll();
        int totalPages = (int) Math.ceil((double) cats.size() / CATS_PER_PAGE);

        if (currentPage < totalPages - 1) {
            currentPage++;
            loadCatsIntoView();
            updatePaginationButtons(); // Actualizar botones después de cambiar

            Platform.runLater(() -> {
                if (scrol != null) scrol.setVvalue(0.0);
            });
        }
    }

    @FXML
    private void handlePreviousPage(MouseEvent event) {
        if (currentPage > 0) {
            currentPage--;
            loadCatsIntoView();
            updatePaginationButtons(); // Actualizar botones después de cambiar

            Platform.runLater(() -> {
                if (scrol != null) scrol.setVvalue(0.0);
            });
        }
    }

    @FXML
    private void handleGoToLogin(MouseEvent event) throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        setCurrentFxmlPath("/com/java/fx/main.fxml");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/java/fx/main.fxml"),
                ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
        );
        loader.setControllerFactory(Main.context::getBean);
        Parent root = loader.load();

        // Crear escena sin dimensiones fijas
        Scene scene = new Scene(root);

        // Obtener el Stage actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Asignar la escena y permitir redimensionamiento
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(700);
    }


    @FXML
    private void handleGoToWeb2(MouseEvent event) {
        try {
            setCurrentFxmlPath("/com/java/fx/web2.fxml");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/java/fx/web2.fxml"),
                    ResourceBundle.getBundle("Messages", Main.getCurrentLocale())
            );
            loader.setControllerFactory(Main.context::getBean);
            Parent root = loader.load();

            UsuarioController controller = loader.getController();
            controller.currentPage = 0;
            controller.loadCatsIntoView();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();

            stage.getScene().setRoot(root); // Solo cambia el contenido

            if (wasMaximized) {
                stage.setMaximized(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updatePaginationButtons() {
        List<Cat> cats = catRepository.findAll();
        int totalPages = (int) Math.ceil((double) cats.size() / CATS_PER_PAGE);

        if (nextpage != null) {
            nextpage.setDisable(currentPage >= totalPages - 1);
            nextpage.setVisible(!(currentPage >= totalPages - 1));
            nextpage.setManaged(!(currentPage >= totalPages - 1));
        }

        if (previouspage != null) {
            previouspage.setDisable(currentPage <= 0);
            previouspage.setVisible(!(currentPage <= 0));
            previouspage.setManaged(!(currentPage <= 0));
        }

        // SOLUCIÓN DEFINITIVA PARA "Ir a log in"
        if (goToLoginButton != null) {
            boolean showGoToLogin = currentPage == 0;
            goToLoginButton.setVisible(showGoToLogin);
            goToLoginButton.setManaged(showGoToLogin);

            // Solo establecer minWidth cuando sea visible
            if (showGoToLogin) {
                goToLoginButton.setMinWidth(100);
            } else {
                goToLoginButton.setMinWidth(0); // Ancho cero cuando no está visible
            }
        }
    }




    // Método auxiliar para determinar la vista FXML basada en la raza
    private String determineFxmlPathForBreed(String breed) {
        switch (breed.toLowerCase()) {
            case "abyssinian":
                return "/com/java/fx/Abyssinian.fxml";
            case "american curl":
                return "/com/java/fx/AmericanCurl.fxml";
            // Añadir más casos según las razas
            default:
                return "/com/java/fx/Generic.fxml"; // Vista genérica si no hay coincidencia
        }
    }
}









