package com.cats.cats;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Main extends Application {
	static ConfigurableApplicationContext context;
	private static CountDownLatch springLatch = new CountDownLatch(1);
	private static Locale currentLocale = Locale.getDefault();

	public static void main(String[] args) {
		// Fuerza español genérico al inicio
		Locale.setDefault(new Locale("es"));
		System.out.println("🔷 Starting application...");

		// Iniciar contexto de Spring en otro hilo
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

		// Lanzar JavaFX
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Platform.runLater(() -> {
			try {
				// 1. Cargar el ResourceBundle con el locale actual (CORREGIDO)
				ResourceBundle bundle = ResourceBundle.getBundle(
						"Messages",  // ¡Solo el nombre base!
						currentLocale
				);

				// 2. Configurar FXMLLoader con el bundle y el controllerFactory de Spring
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/main.fxml"));
				loader.setControllerFactory(context::getBean);
				loader.setResources(bundle);

				Scene scene = new Scene(loader.load());
				primaryStage.setScene(scene);
				primaryStage.setTitle("Cats Application");
				primaryStage.show();
			} catch (Exception e) {
				e.printStackTrace();
				Platform.exit();
			}
		});
	}

	@Override
	public void stop() {
		if (context != null) {
			context.close();
		}
		Platform.exit();
	}

	public static void setLocale(Locale locale) {
		currentLocale = locale;
	}

	@Bean
	public CatDetailController catDetailController() {
		return new CatDetailController();
	}
}


