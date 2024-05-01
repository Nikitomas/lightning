package com.components.se.factory;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.control.ControlCenter;
import com.enums.ContextConstant;
import com.exception.FrameworkException;

public class WebDriverFactory {

    static final Logger logger = LogManager.getLogger(WebDriverFactory.class);
    static int closeAllIEInstance = 0;

    /**
     * Initializes browser. IE , CHROME and Firefox(default) For Selenium grid Platform
     * default Platform set is WINDOWS The options available are - WINDOWS, Windows 8,
     * Windows 10, Linux, Unix, MAC, Vista
     * @param context:ITestContext
     * @return WebDriver
     * @throws FrameworkException
     * @throws IOException
     * @throws Exception throw a Exception
     */
    public static WebDriver initWebDriver(Map<String, String> params) throws FrameworkException, IOException {

        String platform = params.get("PLATFORM");
        boolean runWithSeleniumGrid = params.containsKey("runWithSeleniumGrid")
                && params.get("runWithSeleniumGrid").equalsIgnoreCase("True");
        String hubUrl = params.get("HUB_URL");
        Platform env = null;

        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        if (platform == null) {
            if (os.indexOf("mac") >= 0)
                env = Platform.MAC;
            else if (os.indexOf("win") >= 0)
                env = Platform.WINDOWS;
            else if (os.indexOf("nux") >= 0)
                env = Platform.LINUX;
        }
        else {
            if (platform != null && platform.equalsIgnoreCase("Windows")) {
                env = Platform.WINDOWS;
            }
            else if (platform != null && platform.equalsIgnoreCase("Windows 8")) {
                env = Platform.WIN8;
            }
            else if (platform != null && platform.equalsIgnoreCase("Windows 10")) {
                env = Platform.WIN10;
            }
            else if (platform != null && platform.equalsIgnoreCase("MAC")) {
                env = Platform.MAC;
            }
            else if (platform != null && platform.equalsIgnoreCase("LINUX")) {
                env = Platform.LINUX;
            }
            else if (platform != null && platform.equalsIgnoreCase("UNIX")) {
                env = Platform.UNIX;
            }
            else if (platform != null && platform.equalsIgnoreCase("Vista")) {
                env = Platform.VISTA;
            }
        }

        if (params.containsKey("browser")) {
            if (params.get("browser").equalsIgnoreCase("chrome")) {
                try {
                    System.setProperty("webdriver.chrome.driver",
                            ControlCenter.getInstance().getDirectory(ContextConstant.CHRONE_DRIVER));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new FrameworkException(e.getMessage(), e);
                }

                ChromeOptions options = new ChromeOptions();
                Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.default_content_settings.popups", 0);
                options.setExperimentalOption("prefs", prefs);

                options.addArguments("test-type");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("disable-infobars");
                options.addArguments("--allow-cross-origin-auth-prompt");

                logger.info("Browser parameter is Chrome");

                if (runWithSeleniumGrid) {
                    return new RemoteWebDriver(new URL(hubUrl), options);
                }
                else {
                    return new ChromeDriver(options);
                }
            }
            else if (params.get("browser").equalsIgnoreCase("safari")) {
                SafariOptions options = new SafariOptions();
                if (runWithSeleniumGrid) {
                    return new RemoteWebDriver(new URL(hubUrl), options);
                }
                else {
                    return new SafariDriver(options);
                }
            }
            else {
                logger.info("Browser parameter is Firefox");
                logger.info("Cannot find browser parameter, firefox will be used as default browser");
                System.setProperty("webdriver.gecko.driver",
                        ControlCenter.getInstance().getDirectory(ContextConstant.GECKO_DRIVER));
                FirefoxOptions option = new FirefoxOptions();
                FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("dom.disable_open_during_load", false);
                if (runWithSeleniumGrid) {
                    return new RemoteWebDriver(new URL(hubUrl), option);
                }
                else {
                    return new FirefoxDriver(option);
                }
            }
        }
        else {
            try {
                logger.info("Cannot find browser parameter, firefox will be used as default browser");
                System.setProperty("webdriver.gecko.driver",
                        ControlCenter.getInstance().getDirectory(ContextConstant.GECKO_DRIVER));
                // URI
                // driver=getDriver(ControlCenter.getInstance().getDirectory(ContextConstant.GECKO_DRIVER));
                // System.setProperty("webdriver.gecko.driver",new
                // File(driver).getAbsolutePath());
                FirefoxOptions option = new FirefoxOptions();
                // FirefoxProfile profile = new FirefoxProfile();
                // profile.setPreference("dom.disable_open_during_load", false);
                // option.setCapability(FirefoxDriver.PROFILE, profile);
                if (runWithSeleniumGrid) {
                    return new RemoteWebDriver(new URL(hubUrl), option);
                }
                else {
                    return new FirefoxDriver(option);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    /**
     * Get the driver from the jar file. IE , CHROME and Firefox(default)
     * @param driverPath:String
     * @return URI
     * @throws URISyntaxException throw an URISyntaxException
     * @throws ZipException throw a ZipException
     * @throws IOException throw an IOException
     */
    public static URI getDriver(String driverPath) throws URISyntaxException, ZipException, IOException {
        final URI uri;
        final URI exe;

        uri = getJarURI();
        exe = getFile(uri, driverPath);
        return exe;
    }

    /**
     * Get the jar URI
     * @return URI
     * @throws URISyntaxException throw an URISyntaxException
     */
    private static URI getJarURI() throws URISyntaxException {
        final ProtectionDomain domain;
        final CodeSource source;
        final URL url;
        final URI uri;

        domain = WebDriverFactory.class.getProtectionDomain();
        source = domain.getCodeSource();
        url = source.getLocation();
        uri = url.toURI();

        return (uri);
    }

    /**
     * Get the driver from the jar file
     * @param where:URI
     * @param fileName: String
     * @return URI
     * @throws ZipException throw a ZipException
     * @throws IOException throw an IOException
     */
    private static URI getFile(final URI where, final String fileName) throws ZipException, IOException {
        final File location;
        final URI fileURI;

        location = new File(where);

        // not in a JAR, just return the path on disk
        if (location.isDirectory()) {
            fileURI = URI.create(where.toString() + fileName);
        }
        else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);
            try {
                fileURI = extract(zipFile, fileName);
            }
            finally {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    /**
     * extract the driver exe from the jar file
     * @param ZipFile:ZipFile
     * @param fileName: String
     * @return URI
     * @throws IOException throw an IOException
     */
    private static URI extract(final ZipFile zipFile, final String fileName) throws IOException {
        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;

        tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if (entry == null) {
            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
        }

        zipStream = zipFile.getInputStream(entry);
        fileStream = null;

        try {
            final byte[] buf;
            int i;

            fileStream = new FileOutputStream(tempFile);
            buf = new byte[1024];
            i = 0;

            while ((i = zipStream.read(buf)) != -1) {
                fileStream.write(buf, 0, i);
            }
        }
        finally {
            close(zipStream);
            close(fileStream);
        }

        tempFile.setExecutable(true);
        /*
         * String os = System.getProperty("os.name",
         * "generic").toLowerCase(Locale.ENGLISH); if (os.indexOf("mac") >= 0)
         * Runtime.getRuntime().exec("chmod +x " + tempFile);
         */

        return (tempFile.toURI());
    }

    /**
     * close the stream
     * @param stream:Closeable
     */
    private static void close(final Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            }
            catch (final IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

}