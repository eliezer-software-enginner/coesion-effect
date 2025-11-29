package my_app;// my_app/HotReloadClassLoader.java

import java.net.URL;
import java.net.URLClassLoader;

public class HotReloadClassLoader extends URLClassLoader {
    public HotReloadClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // NUNCA recarrega: classes do sistema, JavaFX, e a interface de contrato.
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("javafx.") ||
                name.equals("my_app.Reloader")) { // <<< ADICIONAR REGRA PARA A INTERFACE

            // Força o carregamento pela hierarquia (parent), garantindo uma única definição.
            return super.loadClass(name, resolve);
        }

        // Tenta SEMPRE buscar a versão nova (recarregável) na pasta target/classes
        try {
            Class<?> c = findClass(name);
            if (resolve) resolveClass(c);
            return c;
        } catch (ClassNotFoundException e) {
            // Se não encontrou a nova versão, volta para o ClassLoader pai
            return super.loadClass(name, resolve);
        }
    }
}