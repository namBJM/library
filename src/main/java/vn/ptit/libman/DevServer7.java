package vn.ptit.libman;

import java.nio.file.Path;
import java.nio.file.Files;
import org.apache.catalina.startup.Tomcat;

/** Starts the packaged WAR on localhost; run `mvn package exec:java`. */
public final class DevServer7 {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Tomcat tomcat = new Tomcat();
        Files.createDirectories(Path.of(".tomcat/webapps"));
        tomcat.setBaseDir(Path.of(".tomcat").toAbsolutePath().toString());
        tomcat.setHostname("localhost");
        tomcat.setPort(port);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        Path webapp=Path.of("target/libman").toAbsolutePath();
        if (!Files.isDirectory(webapp)) throw new IllegalStateException("Run mvn package before starting the server");
        var context=tomcat.addWebapp("", webapp.toString());
        context.setParentClassLoader(DevServer7.class.getClassLoader());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { tomcat.stop(); tomcat.destroy(); } catch (Exception ignored) { }
        }));
        tomcat.start();
        if (!context.getState().isAvailable()) { tomcat.stop(); tomcat.destroy(); throw new IllegalStateException("Web application failed to start"); }
        System.out.println("LibMan: http://localhost:" + port);
        tomcat.getServer().await();
    }
}
