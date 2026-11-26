package vn.iotstar.config;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.WebResourceRoot;
import org.springframework.util.ResourceUtils;

public class JSPStaticResourceConfigurer implements LifecycleListener {

    private final Context context;
    private final String subPath = "/META-INF";

    public JSPStaticResourceConfigurer(Context context) {
        this.context = context;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (!Lifecycle.CONFIGURE_START_EVENT.equals(event.getType())) {
            return;
        }
        URL finalLocation = getUrl();
        if (finalLocation == null) {
            return;
        }
        this.context.getResources().createWebResourceSet(
                WebResourceRoot.ResourceSetType.RESOURCE_JAR,
                "/",
                finalLocation,
                subPath);
    }

    private URL getUrl() {
        URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        if (location == null) {
            return null;
        }
        if (ResourceUtils.isFileURL(location)) {
            File file = new File(URI.create(location.toString()));
            // Only register directory resource set when META-INF/resources exists (exploded IDE run).
            if (file.isDirectory() && !new File(file, "META-INF/resources").isDirectory()) {
                return null;
            }
            return location;
        }
        if (ResourceUtils.isJarURL(location) || "nested".equals(location.getProtocol())) {
            try {
                String raw = location.toString();
                if (raw.contains("WEB-INF/classes")) {
                    // Packaged WAR already exposes webapp resources directly.
                    return null;
                }
                String locationStr = location.getPath()
                        .replaceFirst("^nested:", "")
                        .replaceFirst("/!BOOT-INF/classes/!/$", "!/");
                return new URI("jar:file", locationStr, null).toURL();
            } catch (Exception e) {
                throw new IllegalStateException("Unable to add JSP source URI to Tomcat resources", e);
            }
        }
        return null;
    }
}
