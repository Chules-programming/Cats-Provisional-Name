package com.cats.cats;

import com.cats.cats.entities.*;
import com.cats.cats.repository.ChatMessageRepository;
import com.cats.cats.repository.UsuarioRepository;
import com.cats.cats.services.AdopcionService;
import com.cats.cats.services.CatService;
import com.cats.cats.services.ChatService;
import com.cats.cats.services.ReviewService;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cats.cats.Main.context;

@Component
public class ProfileController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private ChatService chatService;
    @Autowired private AdopcionService adopcionService;
    @Autowired private CatService catService;
    @Autowired private ReviewService reviewService;

    @FXML private ListView<Conversation> chatList;
    @FXML private ListView<ChatMessage> messageList;
    @FXML private TextField messageField;
    @FXML private Label profileUsername;
    @FXML private Label userRole;
    @FXML private HBox ratingStars;
    @FXML private TableView<Adopcion> adoptionTable;
    @FXML private TableColumn<Adopcion, String> catNameColumn;
    @FXML private TableColumn<Adopcion, String> adopterNameColumn;
    @FXML private TableColumn<Adopcion, Void> actionsColumn;
    @FXML private Button giveCatButton;
    @FXML private Button receiveCatButton;

    private Usuario currentUser;
    private ResourceBundle resources;
    private ObjectId currentConversationId;
    private Conversation selectedConversation;
    private WebSocketClient webSocketClient;
    private Map<ObjectId, String> usernameCache = new HashMap<>();

    // Lista observable persistente para los mensajes
    private ObservableList<ChatMessage> observableMessages = FXCollections.observableArrayList();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        // Configuración del messageList con visualización personalizada
        messageList.setItems(observableMessages);

        messageList.setCellFactory(param -> new ListCell<ChatMessage>() {
            @Override
            protected void updateItem(ChatMessage message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setStyle("");
                    setGraphic(null);
                } else {
                    HBox container = new HBox();
                    container.setSpacing(10);

                    Label senderLabel = new Label();
                    senderLabel.setStyle("-fx-font-weight: bold;");

                    Label contentLabel = new Label(message.getContent());
                    contentLabel.setWrapText(true);

                    boolean isCurrentUser = message.getSenderId().equals(currentUser.getId());

                    if (isCurrentUser) {
                        container.setAlignment(Pos.CENTER_RIGHT);
                        senderLabel.setText("Tú:");
                        container.getChildren().addAll(senderLabel, contentLabel);
                        container.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 5px;");
                    } else {
                        container.setAlignment(Pos.CENTER_LEFT);
                        String senderName = usernameCache.getOrDefault(
                                message.getSenderId(), "Usuario desconocido");
                        senderLabel.setText(senderName + ":");
                        container.getChildren().addAll(senderLabel, contentLabel);
                        container.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 5px;");
                    }

                    setGraphic(container);
                }
            }
        });

        // Listener para selección de conversación
        chatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedConversation = newVal;
                loadMessages(newVal.getId());

                Cat cat = catService.getCatById(newVal.getCatId());
                if (cat != null) {
                    if (currentUser.getId().equals(cat.getCaregiverId())) {
                        // Usuario es el cuidador
                        giveCatButton.setDisable(false);
                        giveCatButton.setVisible(true);
                        receiveCatButton.setVisible(false);
                    } else {
                        // Usuario es el adoptante
                        receiveCatButton.setDisable(false);
                        receiveCatButton.setVisible(true);
                        giveCatButton.setVisible(false);
                    }
                } else {
                    giveCatButton.setVisible(false);
                    receiveCatButton.setVisible(false);
                }
            }
        });

        // Desactivar ambos botones al inicio
        Platform.runLater(() -> {
            giveCatButton.setDisable(true);
            giveCatButton.setVisible(false);
            receiveCatButton.setDisable(true);
            receiveCatButton.setVisible(false);
        });

        // Configuración de columnas en la tabla de adopciones
        catNameColumn.setCellValueFactory(cellData -> {
            Cat cat = catService.getCatById(cellData.getValue().getCatId());
            return new SimpleStringProperty(cat != null ? cat.getName() : "Unknown");
        });

        adopterNameColumn.setCellValueFactory(cellData -> {
            Usuario user = usuarioRepository.findById(cellData.getValue().getUserId()).orElse(null);
            return new SimpleStringProperty(user != null ? user.getUsername() : "Unknown");
        });

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button confirmButton = new Button("Confirmar");
            private final Button rejectButton = new Button("Rechazar");

            {
                confirmButton.setOnAction(event -> {
                    Adopcion adoption = getTableView().getItems().get(getIndex());
                    handleConfirmAdoption(adoption);
                });

                rejectButton.setOnAction(event -> {
                    Adopcion adoption = getTableView().getItems().get(getIndex());
                    handleRejectAdoption(adoption);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Adopcion adoption = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    if (!adoption.isConfirmedByCaregiver()) {
                        buttons.getChildren().addAll(confirmButton, rejectButton);
                    }
                    setGraphic(buttons);
                }
            }
        });
    }

    @FXML
    private void handleReceiveCat() {
        if (selectedConversation == null) {
            showAlert("Selecciona una conversación primero");
            return;
        }

        // Obtener el cuidador (X)
        Cat cat = catService.getCatById(selectedConversation.getCatId());
        if (cat == null) return;

        ObjectId caregiverId = cat.getCaregiverId();

        // Crear diálogo de calificación
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Calificar Adopción");
        dialog.setHeaderText("¿Cómo calificas la adopción con " +
                usernameCache.getOrDefault(caregiverId, "el cuidador") + "?");

        ToggleGroup group = new ToggleGroup();
        VBox vbox = new VBox(10);
        for (int i = 1; i <= 5; i++) {
            RadioButton rb = new RadioButton(i + " estrella" + (i > 1 ? "s" : ""));
            rb.setToggleGroup(group);
            rb.setUserData(i);
            vbox.getChildren().add(rb);
        }

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK && group.getSelectedToggle() != null) {
                return (Integer) group.getSelectedToggle().getUserData();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(rating -> {
            saveRating(caregiverId, rating);
            showAlert("¡Calificación guardada! Gracias por tu feedback");
        });
    }



    public void setCurrentUser(Usuario currentUser) {
        this.currentUser = currentUser;
        usernameCache.put(currentUser.getId(), currentUser.getUsername());

        // Cierra cualquier conexión WebSocket existente antes de crear una nueva
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }

        loadConversations();
        connectToWebSocket();
    }

    private void connectToWebSocket() {
        try {
            String uri = "ws://localhost:8080/ws/chat?userId=" + currentUser.getId();
            System.out.println("Conectando a WebSocket: " + uri);

            webSocketClient = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("WebSocket conectado");
                }

                @Override
                public void onMessage(String jsonMessage) {
                    Platform.runLater(() -> {
                        ChatMessage message = gson.fromJson(jsonMessage, ChatMessage.class);
                        if (currentConversationId != null &&
                                currentConversationId.equals(message.getConversationId())) {
                            addMessageToUI(message);
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket cerrado: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("Error en WebSocket:");
                    ex.printStackTrace();
                }
            };

            webSocketClient.connect();
        } catch (URISyntaxException e) {
            System.err.println("Error en la URI del WebSocket: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general al conectar WebSocket: " + e.getMessage());
        }
    }


    private void addMessageToUI(ChatMessage message) {
        observableMessages.add(message);
        preloadUsernamesForMessages(Collections.singletonList(message));
        messageList.scrollTo(observableMessages.size() - 1);
    }

    private void loadConversations() {
        if (currentUser == null) return;

        List<Conversation> conversations = chatService.findByUserId(currentUser.getId());
        chatList.setItems(FXCollections.observableArrayList(conversations));
        preloadUsernamesForConversations(conversations);

        chatList.setCellFactory(param -> new ListCell<Conversation>() {
            @Override
            protected void updateItem(Conversation conversation, boolean empty) {
                super.updateItem(conversation, empty);
                if (empty || conversation == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Determinar el otro usuario en la conversación
                    ObjectId otherUserId = conversation.getUserId1().equals(currentUser.getId()) ?
                            conversation.getUserId2() : conversation.getUserId1();
                    String otherUserName = usernameCache.getOrDefault(otherUserId, "Usuario");
                    setText(otherUserName + ": " + conversation.getLastMessage());
                }
            }
        });
    }

    private void loadMessages(ObjectId conversationId) {
        currentConversationId = conversationId;
        selectedConversation = chatService.getConversationById(conversationId);

        observableMessages.clear();
        List<ChatMessage> messages = chatMessageRepository
                .findByConversationIdOrderByTimestampAsc(conversationId);

        // Asegúrate de que los mensajes se añadan a la lista observable
        observableMessages.addAll(messages);

        // Precarga los nombres de usuario para estos mensajes
        preloadUsernamesForMessages(messages);

        // Fuerza una actualización de la vista
        messageList.setItems(observableMessages);
        messageList.refresh();

        if (!messages.isEmpty()) {
            messageList.scrollTo(messages.size() - 1);
        }
    }

    // Precargar nombres para mensajes
    private void preloadUsernamesForMessages(List<ChatMessage> messages) {
        Set<ObjectId> userIds = messages.stream()
                .map(ChatMessage::getSenderId)
                .collect(Collectors.toSet());

        usuarioRepository.findAllById(userIds).forEach(user ->
                usernameCache.put(user.getId(), user.getUsername())
        );
    }

    // Precargar nombres para conversaciones
    private void preloadUsernamesForConversations(List<Conversation> conversations) {
        Set<ObjectId> userIds = conversations.stream()
                .flatMap(conv -> Stream.of(conv.getUserId1(), conv.getUserId2()))
                .collect(Collectors.toSet());

        usuarioRepository.findAllById(userIds).forEach(user ->
                usernameCache.put(user.getId(), user.getUsername())
        );
    }

    @FXML
    private void handleSendMessage() {
        if (currentUser == null) {
            System.out.println("Usuario actual es nulo. No se puede enviar mensaje.");
            return;
        }

        String content = messageField.getText().trim();
        if (content.isEmpty() || selectedConversation == null) return;

        try {
            ChatMessage newMessage = new ChatMessage();
            newMessage.setConversationId(selectedConversation.getId());
            newMessage.setSenderId(currentUser.getId());
            newMessage.setContent(content);
            newMessage.setTimestamp(new Date());

            chatMessageRepository.save(newMessage);
            messageField.clear();

            addMessageToUI(newMessage);

            if (webSocketClient != null && webSocketClient.isOpen()) {
                String jsonMessage = gson.toJson(newMessage);
                webSocketClient.send(jsonMessage);
                System.out.println("Mensaje enviado via WebSocket: " + jsonMessage);
            } else {
                System.out.println("WebSocket no está disponible. Reintentando conexión...");
                connectToWebSocket(); // Intenta reconectar
                if (webSocketClient != null && webSocketClient.isOpen()) {
                    webSocketClient.send(gson.toJson(newMessage));
                }
            }

            selectedConversation.setLastMessage(content);
            chatService.saveConversation(selectedConversation);
            loadConversations();
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
        }
    }

    private void handleOpenChat(Adopcion adoption) {
        Usuario adopter = usuarioRepository.findById(adoption.getUserId()).orElse(null);
        if (adopter == null) return;

        Conversation conversation = chatService.findOrCreateConversation(
                currentUser.getId(),
                adopter.getId(),
                adoption.getCatId()
        );

        selectedConversation = conversation;
        loadMessages(conversation.getId());
        selectConversationInList(conversation);
    }

    private void selectConversationInList(Conversation conversation) {
        for (Conversation conv : chatList.getItems()) {
            if (conv.getId().equals(conversation.getId())) {
                chatList.getSelectionModel().select(conv);
                break;
            }
        }
    }

    private void handleConfirmAdoption(Adopcion adoption) {
        adoption.setConfirmedByCaregiver(true);
        adoption.setChatEnabled(true);
        adopcionService.save(adoption);
        handleOpenChat(adoption);
        loadAdoptionRequests();
    }

    private void handleRejectAdoption(Adopcion adoption) {
        adoption.setConfirmedByCaregiver(false);
        adoption.setChatEnabled(false);
        adopcionService.save(adoption);
        loadAdoptionRequests();
    }

    public void loadProfileData() {
        if (currentUser == null) return;

        profileUsername.setText(currentUser.getUsername());

        boolean isAdopter = adopcionService.existsByUserId(currentUser.getId());
        boolean isCaregiver = catService.existsByCaregiverId(currentUser.getId());

        String roleText;
        if (isAdopter && isCaregiver) {
            roleText = resources.getString("role.both");
        } else if (isAdopter) {
            roleText = resources.getString("role.adopter");
        } else if (isCaregiver) {
            roleText = resources.getString("role.caregiver");
        } else {
            roleText = resources.getString("role.guest");
        }
        userRole.setText(roleText);

        double averageRating = reviewService.getAverageRatingByUserId(currentUser.getId());
        ratingStars.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < averageRating ? "★" : "☆");
            star.setStyle("-fx-text-fill: " + (i < averageRating ? "gold" : "gray") + "; -fx-font-size: 20px;");
            ratingStars.getChildren().add(star);
        }

        loadAdoptionRequests();
    }

    private void loadAdoptionRequests() {
        if (currentUser == null) return;
        List<Adopcion> requests = adopcionService.findByCaregiverId(currentUser.getId());
        adoptionTable.setItems(FXCollections.observableArrayList(requests));
    }

    @FXML
    private void handleGoBack(ActionEvent event) throws IOException {
        if (webSocketClient != null) {
            webSocketClient.close();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/web2.fxml"));
        loader.setControllerFactory(context::getBean);
        loader.setResources(resources);

        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    @FXML
    private void handleRateAdoption() {
        if (selectedConversation == null) {
            showAlert("Selecciona una conversación primero");
            return;
        }

        // Obtener el otro usuario de la conversación
        ObjectId otherUserId = selectedConversation.getUserId1().equals(currentUser.getId()) ?
                selectedConversation.getUserId2() : selectedConversation.getUserId1();

        // Crear diálogo de calificación
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Calificar Adopción");
        dialog.setHeaderText("¿Cómo calificas la adopción con " +
                usernameCache.getOrDefault(otherUserId, "el usuario") + "?");

        // Crear opciones de calificación (1-5 estrellas)
        ToggleGroup group = new ToggleGroup();
        VBox vbox = new VBox(10);
        for (int i = 1; i <= 5; i++) {
            RadioButton rb = new RadioButton(i + " estrella" + (i > 1 ? "s" : ""));
            rb.setToggleGroup(group);
            rb.setUserData(i);
            vbox.getChildren().add(rb);
        }

        // Configurar botones
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Procesar resultado
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK && group.getSelectedToggle() != null) {
                return (Integer) group.getSelectedToggle().getUserData();
            }
            return null;
        });

        // Mostrar diálogo y guardar calificación
        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(rating -> {
            saveRating(otherUserId, rating);
            showAlert("¡Calificación guardada! Gracias por tu feedback");
        });
    }
    private void saveRating(ObjectId ratedUserId, int rating) {
        Review review = new Review();
        review.setUserId(ratedUserId); // Usuario calificado
        review.setReviewerId(currentUser.getId()); // Usuario que califica
        review.setRating(rating);
        review.setDate(LocalDate.now());
        // Guardar en la base de datos
        reviewService.save(review);

        // Actualizar calificación promedio del usuario
        double newAverage = reviewService.getAverageRatingByUserId(ratedUserId);
        System.out.println("Nueva calificación promedio para " + ratedUserId + ": " + newAverage);
        if (currentUser.getId().equals(ratedUserId)) {
            updateRatingDisplay();
        }
    }

    private void updateRatingDisplay() {
        double averageRating = reviewService.getAverageRatingByUserId(currentUser.getId());
        ratingStars.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < averageRating ? "★" : "☆");
            star.setStyle("-fx-text-fill: " + (i < averageRating ? "gold" : "gray") + "; -fx-font-size: 20px;");
            ratingStars.getChildren().add(star);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}




