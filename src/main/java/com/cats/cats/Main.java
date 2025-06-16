package com.cats.cats;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
	public static Locale currentLocale = Locale.getDefault();

	private HostServices appHostServices;

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

			// 1. Establecer dimensiones mínimas
			primaryStage.setMinWidth(600);
			primaryStage.setMinHeight(700);

			// 2. Establecer tamaño inicial
			primaryStage.setWidth(600);
			primaryStage.setHeight(700);

			// 3. Centrar la ventana
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

	// Bean para ResourceBundle
	@Bean
	public ResourceBundle resourceBundle() {
		return ResourceBundle.getBundle("Messages", getCurrentLocale());
	}

	// Método estático para acceder al ResourceBundle
	public static ResourceBundle getResourceBundle() {
		return context.getBean(ResourceBundle.class);
	}

	public static Locale getCurrentLocale() {
		return currentLocale;
	}

	public static void setCurrentLocale(Locale locale) {
		currentLocale = locale;
		// Forzamos que el locale por defecto de Java cambie también
		Locale.setDefault(locale);
		ResourceBundle.clearCache();
		printBundleInfo();
	}

	public static void printBundleInfo() {
		System.out.println("Current locale: " + currentLocale);
		ResourceBundle bundle = ResourceBundle.getBundle("Messages", currentLocale);
		System.out.println("Bundle locale: " + bundle.getLocale());
	}
}


