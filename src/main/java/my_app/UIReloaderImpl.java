package my_app;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import javafx.application.Platform;
// Importações presumidas da sua biblioteca plantfall
import plantfall.CoesionApp;
import plantfall.FXHelper;
import plantfall.ReloadableWindow;
import plantfall.Reloader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class UIReloaderImpl implements Reloader {
    private static final String INIT_METHOD_NAME = "initView";

    @Override
    public void reload(Object context) {
        if (context instanceof Stage mainStage) {

            Platform.runLater(() -> {
                try {
                    // --- Passo 1: Obter Metadados ---
                    // Carrega a App class via System ClassLoader (a App está excluída)
                    Class<?> originalAppClass = Class.forName("my_app.App");
                    var globalAnnotation = originalAppClass.getAnnotation(CoesionApp.class);

                    if (globalAnnotation == null) {
                        System.err.println("Annotation @CoesionApp not found on App class.");
                        return;
                    }

                    List<String> globalStyles = Arrays.asList(globalAnnotation.stylesheets());

                    // --- Passo 2: Carregar a Nova View Principal ---
                    String mainViewClassName = globalAnnotation.mainViewClass().getName();
                    ClassLoader currentClassLoader = this.getClass().getClassLoader();

                    // Adicionado Log de Debug
                    System.out.println("[UIReloader] Attempting to load new view class: " + mainViewClassName +
                            " with ClassLoader: " + currentClassLoader.getClass().getName());

                    // Carrega a versão mais recente da View usando o HotReloadClassLoader
                    Class<? extends Region> actualMainViewClass = (Class<? extends Region>) currentClassLoader
                            .loadClass(mainViewClassName);

                    Region newMainView = FXHelper.createNewViewInstance(actualMainViewClass);

                    // Adicionado Log de Debug - O hash da classe DEVE mudar a cada recarregamento
                    System.out.println("[UIReloader] New View Instance created from class: " + actualMainViewClass.getName() +
                            " (Class Hash: " + actualMainViewClass.hashCode() + ")");


                    // Tenta encontrar o método initView() na nova classe (ainda não recarregada)
                    Method initMethod = null;
                    try {
                        initMethod = actualMainViewClass.getDeclaredMethod(INIT_METHOD_NAME);
                        initMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        // Método initView é opcional, então ignora.
                    }

                    // --- Passo 3: Recarregar Views e Estilos em Windows Secundárias (@ReloadableWindow) e Injetar ---
                    this.injectAndSetupWindows(newMainView, currentClassLoader, globalStyles, originalAppClass);

                    // --- Passo 4: Chamada do Ciclo de Vida (initView) ---
                    if (initMethod != null) {
                        initMethod.invoke(newMainView);
                        System.out.println("[UIReloader] Executed initView() on new view instance.");
                    }

                    // --- Passo 5: Recria a Estrutura da UI Principal ---
                    Scene mainScene = mainStage.getScene();
                    if (mainScene != null) {
                        // Assumimos que o root da Scene é o StackPane estático App.ROOT
                        StackPane root = (StackPane) mainScene.getRoot();

                        // Substitui o conteúdo do root pelo novo MainView recarregado
                        root.getChildren().setAll(newMainView);

                        // Aplica estilos na SCENE PRINCIPAL
                        mainScene.getStylesheets().clear();
                        FXHelper.applyStylesToScene(mainScene, globalStyles, originalAppClass);
                        System.out.println("[UIReloader] Main UI updated and styles re-applied.");
                    } else {
                        // Esta branch é para o caso de o stage não ter uma scene ainda,
                        // o que não deve acontecer no seu setup atual, mas é bom ter.
                        System.err.println("[UIReloader] Main Stage Scene is null.");
                    }


                } catch (Exception e) {
                    System.err.println("Critical Error during UI Reload execution.");
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Lógica isolada para injeção de Stage e recarga de conteúdo em janelas secundárias.
     */
    private void injectAndSetupWindows(Region newMainViewInstance, ClassLoader cl, List<String> globalStyles, Class<?> appClass) throws Exception {
        for (Field field : newMainViewInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ReloadableWindow.class) && field.getType().equals(Stage.class)) {

                field.setAccessible(true);
                ReloadableWindow windowAnnotation = field.getAnnotation(ReloadableWindow.class);
                String windowTitle = windowAnnotation.title();

                // 1. Tenta encontrar a Stage Ativa (para recarga)
                Stage stageToModify = FXHelper.findActiveStageByTitle(windowTitle);

                if (stageToModify == null) {
                    // Se não existir, cria uma nova
                    stageToModify = new Stage();
                    stageToModify.setTitle(windowTitle);
                    System.out.println("[UIReloader] New Secondary Stage created: " + windowTitle);
                }

                // Injeta a Stage na nova MainView
                field.set(newMainViewInstance, stageToModify);

                // 2. Carrega e configura o conteúdo secundário
                Class<? extends Region> contentClassOld = windowAnnotation.contentClass();
                Class<? extends Region> actualContentClass = (Class<? extends Region>) cl
                        .loadClass(contentClassOld.getName());

                Region newContent = FXHelper.createNewViewInstance(actualContentClass);

                Scene secondaryScene = stageToModify.getScene();
                if (secondaryScene == null) {
                    // Inicialização completa (Scene não existe)
                    secondaryScene = new Scene(newContent, windowAnnotation.width(), windowAnnotation.height());
                    stageToModify.setScene(secondaryScene);
                } else {
                    // Recarga de Conteúdo (Scene já existe)
                    secondaryScene.setRoot(newContent);
                }

                // Aplica Tamanho e Estilos
                Window window = secondaryScene.getWindow();
                if (window != null) {
                    window.setWidth(windowAnnotation.width());
                    window.setHeight(windowAnnotation.height());
                }

                List<String> localStyles = Arrays.asList(windowAnnotation.stylesheets());
                secondaryScene.getStylesheets().clear();
                FXHelper.applyStylesToScene(secondaryScene, globalStyles, appClass);
                FXHelper.applyStylesToScene(secondaryScene, localStyles, appClass);

                System.out.println("[UIReloader] Secondary Window '" + windowTitle + "' updated.");
            }
        }
    }
}