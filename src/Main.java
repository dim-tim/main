import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by dima on 3.10.14.
 */
public class Main {

    private static final String FILE_PATH_NAME = "jar/functionality-1.0-SNAPSHOT.jar";
    private static final String PACKAGE_NAME = "com.epam.sample";

    private Map<String, Class<?>> localCache = new HashMap<String, Class<?>>();

    public static void main(String[] args) {
        Main main = new Main();
        main.cacheAllClasses();
    }

    private void cacheAllClasses() {
        try {
            JarFile jarFile = new JarFile(FILE_PATH_NAME);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (match(normalize(jarEntry.getName()), PACKAGE_NAME)) {
                    byte[] classData = loadClassData(jarFile, jarEntry);
                    if (classData != null) {
                        System.out.println("its ok!");
                    }
                }
            }
        } catch (IOException e) {
            //TODO : log
            e.printStackTrace();
        }
    }

    /**
     * Преобразуем имя в файловой системе в имя класса
     * (заменяем слэши на точки)
     *
     * @param className
     * @return
     */
    private String normalize(String className) {
        return className.replace('/', '.');
    }

    /**
     * Валидация класса - проверят принадлежит ли класс заданному пакету и имеет ли
     * он расширение .class
     *
     * @param className
     * @param packageName
     * @return
     */
    private boolean match(String className, String packageName) {
        return className.startsWith(packageName) && className.endsWith(".class");
    }

    /**
     * Извлекаем файл из заданного JarEntry
     *
     * @param jarFile  - файл jar-архива из которого извлекаем нужный файл
     * @param jarEntry - jar-сущность которую извлекаем
     * @return null если невозможно прочесть файл
     */
    private byte[] loadClassData(JarFile jarFile, JarEntry jarEntry) throws IOException {
        long size = jarEntry.getSize();
        if (size == -1 || size == 0) {
            return null;
        }
        byte[] data = new byte[(int) size];
        InputStream is = jarFile.getInputStream(jarEntry);
        try {
            is.read(data);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                //LOG.error(e.getMessage());
            }
        }

        return data;

    }

}
