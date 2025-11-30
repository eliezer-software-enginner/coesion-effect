package my_app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import my_app.screens.HomeScreen;
import plantfall.CoesionApp;
import plantfall.HotReload;

import java.util.HashSet;
import java.util.Set;

@CoesionApp(stylesheets = {"/styles.css"}, mainViewClass = HomeScreen.class)
public class App extends Application {

    public static StackPane ROOT = new StackPane();

    private HotReload hotReload;

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(ROOT, 850, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Coesion Effect");
        primaryStage.setResizable(false);
        primaryStage.show();

        // Configure Exclusions (Always exclude the App entry point)
        Set<String> exclusions = new HashSet<>();
        //exclusions.add("my_app.App");

        // Initialize Hot Reload
        hotReload = new HotReload(
                "src/main/java",     // Path to .java files
                "target/classes",           // Path to compiled .class files
                "src/main/resources",       // Path to resources (css/fxml)
                "my_app.UIReloaderImpl",    // Your Implementation Class Name
                primaryStage,               // Context (Main Stage)
                exclusions                  // Classes to exclude from reloading
        );
        hotReload.start();
    }



    @Override
    public void stop() throws Exception {
        // opcional: encerra o HotReload watchService ao fechar a aplicação
        if (hotReload != null) hotReload.stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
