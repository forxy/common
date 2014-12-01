package common.web

import common.support.SystemProperties

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import java.util.jar.Attributes
import java.util.jar.Manifest

class SystemPropertiesSupportListener implements ServletContextListener {

    private static final String MANIFEST_PATH = '/META-INF/MANIFEST.MF'
    private static final String PRODUCT = 'Product'
    private static final String VERSION = 'Version'

    private static Manifest readManifest(final ServletContext servletContext) {
        Manifest manifest = null

        try {
            final InputStream inputStream = servletContext.getResourceAsStream(MANIFEST_PATH)
            if (inputStream) {
                manifest = new Manifest(inputStream)
            }
        } catch (ignore) {
            manifest = null
        }

        return manifest
    }

    static String readApplicationInfo(final ServletContext servletContext) {
        final Manifest manifest = readManifest(servletContext)

        final String serviceName = manifest?.mainAttributes?.getValue(PRODUCT) ?: 'UNKNOWN'
        final String serviceVersion = manifest?.mainAttributes?.getValue(VERSION) ?: 'UNKNOWN'

        SystemProperties.serviceName = serviceName
        SystemProperties.serviceVersion = serviceVersion

        return serviceVersion
    }

    @Override
    void contextInitialized(final ServletContextEvent servletContextEvent) {
        readApplicationInfo(servletContextEvent.servletContext)
    }

    @Override
    void contextDestroyed(final ServletContextEvent servletContextEvent) {
    }
}