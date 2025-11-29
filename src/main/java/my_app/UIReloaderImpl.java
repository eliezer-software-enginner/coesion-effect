// my_app/UIReloaderImpl.java (Classe de UI)
package my_app;

import javafx.scene.layout.StackPane;

public class UIReloaderImpl implements Reloader {
    @Override
    public void reload(StackPane root) {
        // Agora, 'new MainView()' usa a DEFINIÇÃO da MainView
        // carregada pelo HotReloadClassLoader.
        root.getChildren().setAll(new MainView());
        System.out.println("[UIReloader] UI updated by new class definition.");
    }
}