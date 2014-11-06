package common.web;

import common.support.SystemProperties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class SystemPropertiesSupportListener implements ServletContextListener {

    private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
    private static final String PRODUCT = "Product";
    private static final String VERSION = "Version";

    private static Manifest readManifest(final ServletContext servletContext) {
        Manifest manifest = null;

        try {
            final InputStream inputStream = servletContext.getResourceAsStream(MANIFEST_PATH);
            if (inputStream != null) {
                manifest = new Manifest(inputStream);
            }
        } catch (IOException e) {
            manifest = null;
        }

        return manifest;
    }

    public static String readApplicationInfo(final ServletContext servletContext) {
        final Manifest manifest = readManifest(servletContext);

        final String serviceName;
        final String serviceVersion;

        if (manifest != null) {
            final Attributes attributes = manifest.getMainAttributes();
            serviceName = attributes.getValue(PRODUCT);
            serviceVersion = attributes.getValue(VERSION);
        } else {
            serviceName = "UNKNOWN";
            serviceVersion = "UNKNOWN";
        }
        SystemProperties.setServiceName(serviceName);
        SystemProperties.setServiceVersion(serviceVersion);

        return serviceVersion;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        readApplicationInfo(servletContextEvent.getServletContext());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
    }
}