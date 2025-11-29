package my_app;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainView extends StackPane {

    Button btn = new Button("Ola mundo2");
    public MainView() {
        getChildren().add(btn);
    }
}
