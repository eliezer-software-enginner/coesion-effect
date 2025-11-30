package my_app.screens;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import toolkit.Component;

import java.awt.*;
import java.net.URI;

public class HomeScreen extends StackPane {
    @Component
    ImageView logo = new ImageView(new Image(getClass().getResource("/logo_256.png").toExternalForm()));
    @Component
    Text title = new Text("Coesion Effect"), description = new Text("Your JavaFX base project for you getting started");

    @Component
    Button bt1 = new Button("Give star ⭐");

    @Component
    VBox layout = new VBox(logo, title, description, bt1);
    public HomeScreen() {
        getChildren().add(layout);
        getStyleClass().add("bg");

        title.getStyleClass().addAll("text","title");
        description.getStyleClass().add("text");

        layout.getStyleClass().add("content");

        var animation = new ScaleTransition(Duration.millis(500), logo);
        animation.setFromX(1.5);
        animation.setToX(1);
        animation.play();

        bt1.setOnMouseClicked(ev->{
            var rotate = new RotateTransition(Duration.millis(700), bt1);
            rotate.setFromAngle(1);
            rotate.setToAngle(30);
            rotate.play();

            rotate.setOnFinished(e->{
                new Thread(()->{
                    try {
                        Desktop.getDesktop().browse(URI.create("https://github.com/eliezer-software-enginner/coesion-effect"));

                    } catch (Exception _) {
                    }
                }).start();
            });
        });
    }
}
