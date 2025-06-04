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

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Main extends Application {
	static ConfigurableApplicationContext context;
	private static CountDownLatch springLatch = new CountDownLatch(1);

	public static void main(String[] args) {
		System.out.println("🔷 Starting application...");

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
		Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/main.fxml"));
				loader.setControllerFactory(context::getBean);

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

	// Añade este bean para CatDetailController
	@Bean
	public CatDetailController catDetailController() {
		return new CatDetailController();
	}
}

