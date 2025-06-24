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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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
        // Usa la lista observable persistente
        messageList.setItems(observableMessages);

        messageList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessage message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String senderName = usernameCache.getOrDefault(
                            message.getSenderId(),
                            "Usuario desconocido"
                    );

                    // Comparación correcta de IDs para determinar si es el usuario actual
                    boolean isCurrentUser = message.getSenderId().equals(currentUser.getId());

                    if (isCurrentUser) {
                        setStyle("-fx-background-color: #DCF8C6; -fx-alignment: center-right;");
                        setText("Tú: " + message.getContent()); // Identificación clara
                    } else {
                        setStyle("-fx-background-color: #FFFFFF; -fx-alignment: center-left;");
                        setText(senderName + ": " + message.getContent());
                    }
                }
            }
        });

        chatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedConversation = newVal;
                loadMessages(newVal.getId());
            }
        });

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

    public void setCurrentUser(Usuario currentUser) {
        this.currentUser = currentUser;
        // Precarga el nombre del usuario actual
        usernameCache.put(currentUser.getId(), currentUser.getUsername());
        loadConversations();
        connectToWebSocket();
    }

    private void connectToWebSocket() {
        try {
            String uri = "ws://localhost:8080/ws/chat?userId=" + currentUser.getId();
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
                    ex.printStackTrace();
                }
            };
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void addMessageToUI(ChatMessage message) {
        // Añade el mensaje a la lista observable existente
        observableMessages.add(message);
        preloadUsernames(Collections.singletonList(message));
        messageList.scrollTo(observableMessages.size() - 1);
    }

    private void loadConversations() {
        if (currentUser == null) return;

        List<Conversation> conversations = chatService.findByUserId(currentUser.getId());
        chatList.setItems(FXCollections.observableArrayList(conversations));

        chatList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Conversation conversation, boolean empty) {
                super.updateItem(conversation, empty);
                if (empty || conversation == null) {
                    setText(null);
                } else {
                    ObjectId otherUserId = conversation.getUserId1().equals(currentUser.getId()) ?
                            conversation.getUserId2() : conversation.getUserId1();
                    Usuario otherUser = usuarioRepository.findById(otherUserId).orElse(null);
                    String otherUserName = otherUser != null ? otherUser.getUsername() : "Usuario desconocido";
                    setText(otherUserName + ": " + conversation.getLastMessage());
                }
            }
        });
    }

    private void loadMessages(ObjectId conversationId) {
        currentConversationId = conversationId;
        selectedConversation = chatService.getConversationById(conversationId);

        // Limpia y añade mensajes a la lista observable existente
        observableMessages.clear();
        observableMessages.addAll(chatService.getMessagesByConversationOrdered(conversationId));

        preloadUsernames(observableMessages);
        if (!observableMessages.isEmpty()) {
            messageList.scrollTo(observableMessages.size() - 1);
        }
    }

    private void preloadUsernames(List<ChatMessage> messages) {
        Set<ObjectId> userIds = messages.stream()
                .map(ChatMessage::getSenderId)
                .collect(Collectors.toSet());

        usuarioRepository.findAllById(userIds).forEach(user ->
                usernameCache.put(user.getId(), user.getUsername())
        );
    }

    @FXML
    private void handleSendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty() || selectedConversation == null) return;

        ChatMessage newMessage = new ChatMessage();
        newMessage.setConversationId(selectedConversation.getId());
        newMessage.setSenderId(currentUser.getId());
        newMessage.setContent(content);
        newMessage.setTimestamp(new Date());

        chatMessageRepository.save(newMessage);
        messageField.clear();

        // Añade el mensaje localmente
        addMessageToUI(newMessage);

        if (webSocketClient != null && webSocketClient.isOpen()) {
            String jsonMessage = gson.toJson(newMessage);
            webSocketClient.send(jsonMessage);
        }

        selectedConversation.setLastMessage(content);
        chatService.saveConversation(selectedConversation);
        loadConversations();
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
}




