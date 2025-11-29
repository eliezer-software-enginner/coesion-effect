package my_app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

@CoesionApp
public class App extends Application {

    public static App INSTANCE;
    public static Stage MAIN_STAGE;

    public static StackPane ROOT = new StackPane();

    // opcional: armazena o reloader pra poder parar na saída
    private HotReload hotReload;

    @Override
    public void start(Stage stage) {
        INSTANCE = this;
        MAIN_STAGE = stage;

        loadMainView();
        // root sempre o mesmo
        Scene scene = new Scene(ROOT, 700, 500);
        stage.setScene(scene);
        stage.setTitle("JavaFX + HotReload (in-process)");
        stage.show();

        // *** INICIA O HOTRELOAD AQUI MESMO (NA MESMA JVM) ***
        // paths relativos ao seu projeto; ajuste se necessário
        hotReload = new HotReload("src/main/java/my_app", "target/classes");
        hotReload.start();
    }

    public void loadMainView() {
        ROOT.getChildren().setAll(new MainView());
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
