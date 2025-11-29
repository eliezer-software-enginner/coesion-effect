package my_app;

import javafx.application.Platform;

import javax.tools.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class HotReload {

    private final Path sourcePath;
    private final Path classesPath;

    private volatile boolean running = true;

    public HotReload(String src, String classes) {
        this.sourcePath = Paths.get(src);
        this.classesPath = Paths.get(classes);
    }

    public void start() {
        Thread t = new Thread(this::watchLoop, "HotReload");
        t.setDaemon(true);
        t.start();
    }

    private void watchLoop() {
        try (WatchService ws = FileSystems.getDefault().newWatchService()) {
            sourcePath.register(ws,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("[HotReload] started, watching: " + sourcePath);

            while (running) {
                WatchKey key = ws.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (!event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY) &&
                            !event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
                        continue;

                    Path changed = sourcePath.resolve((Path) event.context());
                    if (!changed.toString().endsWith(".java")) continue;

                    System.out.println("[HotReload] Change detected: " + changed);

                    if (compile()) {
                        callReloadEntry();
                    }
                }
                key.reset();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean compile() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("[HotReload] No Java compiler available.");
            return false;
        }

        // listar todos os arquivos .java
        List<String> files = new ArrayList<>();
        Files.walk(sourcePath)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> files.add(p.toString()));

        System.out.println("[HotReload] Compiling...");

        // argumentos do javac DEVEM ser separados
        List<String> args = new ArrayList<>();
        args.add("-d");
        args.add(classesPath.toString());
        args.addAll(files);

        int result = compiler.run(null, null, null,
                args.toArray(new String[0]));

        System.out.println("[HotReload] Compile status: " + (result == 0));
        return result == 0;
    }


    // HotReload.java
    // HotReload.java (Novo callReloadEntry)
    private void callReloadEntry() throws Exception {
        URL[] urls = new URL[]{classesPath.toUri().toURL()};
        ClassLoader cl = new HotReloadClassLoader(urls, ClassLoader.getSystemClassLoader());

        // Carrega a classe de recarga NOVO ClassLoader
        Class<?> reloaderClass = cl.loadClass("my_app.UIReloaderImpl");

        // Cria uma nova instância da classe de recarga
        Reloader reloader = (Reloader) reloaderClass.getDeclaredConstructor().newInstance();

        System.out.println("[HotReload] Invoking new Reloader class.");

        // Garante que a execução é na FX Thread
        Platform.runLater(() -> {
            // Passa a referência da raiz DA INSTÂNCIA ANTIGA (App.ROOT) para o NOVO objeto
            reloader.reload(App.ROOT);
        });
    }

    public void stop() {
        running = false;
    }
}
