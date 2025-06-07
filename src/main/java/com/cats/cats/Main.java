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
		// Guardar referencia a HostServices
		this.appHostServices = getHostServices();

		Platform.runLater(() -> {
			try {
				// 1. Cargar el ResourceBundle con el locale actual
				ResourceBundle bundle = getResourceBundle();

				// 2. Configurar FXMLLoader con el bundle y el controllerFactory de Spring
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/java/fx/main.fxml"));
				loader.setControllerFactory(context::getBean);
				loader.setResources(bundle);

				Parent root = loader.load();
				Scene scene = new Scene(root);

				// 3. Configurar HostServices en el controlador principal
				UsuarioController controller = loader.getController();
				controller.setHostServices(this.appHostServices);

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


