package com.cats.cats;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Main extends Application {

	static ConfigurableApplicationContext context;
	private static CountDownLatch springLatch = new CountDownLatch(1);
	public static Locale currentLocale = Locale.getDefault();

	private static final SimpleBooleanProperty isGuest = new SimpleBooleanProperty(false);

	public static boolean isGuest() {
		return isGuest.get();
	}

	public static void setGuest(boolean value) {
		isGuest.set(value);
	}

	public static BooleanProperty isGuestProperty() {
		return isGuest;
	}

	private HostServices appHostServices;

	public static void main(String[] args) {
		Locale.setDefault(new Locale("es"));
		System.out.println("🔷 Starting application...");

		// Crear directorios persistentes y copiar imagen por defecto
		try {
			Path userHomeDir = Paths.get(System.getProperty("user.home"));
			Path appDir = userHomeDir.resolve(".catsapp");
			Path assetsDir = appDir.resolve("assets");
			Path profileImagesDir = appDir.resolve("profile_images");

			// Crear directorios si no existen
			if (!Files.exists(assetsDir)) {
				Files.createDirectories(assetsDir);
				System.out.println("📁 Directorio assets creado: " + assetsDir.toAbsolutePath());
			}
			if (!Files.exists(profileImagesDir)) {
				Files.createDirectories(profileImagesDir);
				System.out.println("📁 Directorio profile_images creado: " + profileImagesDir.toAbsolutePath());
			}

			// Copiar imagen por defecto si no existe
			Path defaultImageDest = assetsDir.resolve("profile_icon.png");
			if (!Files.exists(defaultImageDest)) {
				try (InputStream defaultStream = Main.class.getResourceAsStream("/assets/profile_icon.png")) {
					if (defaultStream != null) {
						Files.copy(defaultStream, defaultImageDest);
						System.out.println("✅ Imagen por defecto copiada a: " + defaultImageDest.toAbsolutePath());
					} else {
						System.err.println("❌ No se encontró la imagen por defecto en los recursos");
					}
				}
			} else {
				System.out.println("ℹ️ Imagen por defecto ya existe en: " + defaultImageDest.toAbsolutePath());
			}
		} catch (IOException e) {
			System.err.println("❌ Error creando directorios: " + e.getMessage());
			e.printStackTrace();
		}

		new Thread(() -> {
			context = SpringApplication.run(Main.class, args);
			springLatch.countDown();
			System.out.println("✅ Spring context initialized");
		}).start();

		try {
			springLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Spring initialization interrupted", e);
		}

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.appHostServices = getHostServices();

		try {
			ResourceBundle bundle = getResourceBundle();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/main.fxml"));
			loader.setControllerFactory(context::getBean);
			loader.setResources(bundle);

			Parent root = loader.load();
			Scene scene = new Scene(root);
			UsuarioController controller = loader.getController();
			controller.setHostServices(this.appHostServices);

			primaryStage.setScene(scene);
			primaryStage.setTitle("Cats Application");
			primaryStage.setMinWidth(600);
			primaryStage.setMinHeight(700);
			primaryStage.setWidth(600);
			primaryStage.setHeight(700);
			primaryStage.centerOnScreen();

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
			Platform.exit();
		}
	}

	@Override
	public void stop() {
		if (context != null) {
			context.close();
		}
		Platform.exit();
	}

	@Bean
	public ResourceBundle resourceBundle() {
		return ResourceBundle.getBundle("Messages", getCurrentLocale());
	}

	public static ResourceBundle getResourceBundle() {
		return context.getBean(ResourceBundle.class);
	}

	public static Locale getCurrentLocale() {
		return currentLocale;
	}

	public static void setCurrentLocale(Locale locale) {
		currentLocale = locale;
		Locale.setDefault(locale);
		ResourceBundle.clearCache();
		printBundleInfo();
	}

	public static void printBundleInfo() {
		System.out.println("Current locale: " + currentLocale);
		ResourceBundle bundle = ResourceBundle.getBundle("Messages", currentLocale);
		System.out.println("Bundle locale: " + bundle.getLocale());
	}

	public static void resetGuestState() {
		isGuest.set(false);
	}
}