package com.control;

import static com.enums.ContextConstant.APPLICATION_SERVER;
import static com.enums.ContextConstant.CHRONE_DRIVER;
import static com.enums.ContextConstant.CURRENT_REPORT;
import static com.enums.ContextConstant.CURRENT_SC;
import static com.enums.ContextConstant.GECKO_DRIVER;
import static com.enums.ContextConstant.IE_DRIVER;
import static com.enums.ContextConstant.IS_SOFT;
import static com.enums.ContextConstant.LOG_LOCATION;
import static com.enums.ContextConstant.PARTIAL_NAME;
import static com.enums.ContextConstant.REPORT;
import static com.enums.ContextConstant.REPORT_LOCATION;
import static com.enums.ContextConstant.RESOURCES;
import static com.enums.ContextConstant.ROOT;
import static com.enums.ContextConstant.RUN_MODE;
import static com.enums.ContextConstant.TIMEOUT;
import static com.enums.ContextConstant.API_SERVER;
import static com.enums.ContextConstant.LOG_LEVEL;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.components.ComponentManager;
import com.exception.FrameworkException;
import com.reporting.IReporter;
import com.reporting.ReportAdapter;
import com.reporting.ReporterLog4J;

public class ControlCenter {

    private ThreadLocal<ComponentManager> threadLocalComponents;

    private static ControlCenter instance;

    private Map<String, String> directories;

    private Map<String, String> parameters;

    private static final String TIME_STAMP = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());

    private ReportAdapter adapter;

    /**
     * Constructor for ControlCenter
     * @throws FrameworkException : UIException
     */
    private ControlCenter() {
        directories = new HashMap<String, String>();
        parameters = new HashMap<String, String>();
        adapter = new ReportAdapter();
    }

    /**
     * Creates an instance of ControlCenter
     * @return instance: ControlCenter
     */
    public static synchronized ControlCenter getInstance() {
        if (instance == null) {
            instance = new ControlCenter();
        }
        return instance;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void initialize(Map<String, String> params) throws FrameworkException {
        this.parameters = params;

        threadLocalComponents = new ThreadLocal<ComponentManager>();
        if (!params.containsKey(APPLICATION_SERVER) && !params.containsKey(API_SERVER)) {
            throw new FrameworkException("Application url is mandatory. Please set this value and run again");
        }

        try {
            parameters.put(TIMEOUT, params.containsKey(TIMEOUT) ? params.get(TIMEOUT) : "30");
            parameters.put(IS_SOFT, params.containsKey(IS_SOFT) ? params.get(IS_SOFT) : "false");
            parameters.put(LOG_LEVEL, params.containsKey(LOG_LEVEL) ? params.get(LOG_LEVEL) : "debug");

            setDirectory(params);
            // Add default logger
            this.addReporter(new ReporterLog4J(params,
                    directories.get(LOG_LOCATION) + File.separator + directories.get(PARTIAL_NAME) + ".log"));
            setEnvironmentParameter();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    /**
     * Get a parameter value based on key
     * @param key:String
     * @return value of key: String
     */
    public String getParameter(String key) {
        if (parameters.containsKey(key))
            return parameters.get(key);
        else
            return "";
    }

    /**
     * Get directory value based on the key.
     * @param key : key in map
     * @return String : value of key
     */
    public String getDirectory(String key) {
        return directories.containsKey(key) ? directories.get(key) : null;
    }

    /**
     * @param reporter
     */
    public void addReporter(IReporter reporter) {
        adapter.addReporter(reporter);
    }

    /**
     * @return
     */
    public ReportAdapter getReporter() {
        return adapter;
    }

    /**
     * @return
     */
    public ComponentManager getComponentManager() {
        return threadLocalComponents.get();
    }

    /**
     * @return
     */
    public void setComponentManager(ComponentManager cm) {
        threadLocalComponents.set(cm);
    }

    /**
     * This function should be called at the beginning of the session execution to set up
     * the folder structure
     * @param params : Map
     * @throws IOException : IOException
     * @throws FrameworkException : UIException
     */
    private void setDirectory(Map<String, String> params) throws IOException {
        String root = "";
        try {
            root = new java.io.File(".").getCanonicalPath();
            directories.put(ROOT, root + "/");
            directories.put(RESOURCES, root + "/resources/");
            directories.put(REPORT,
                    (params.containsKey(REPORT_LOCATION) ? params.get(REPORT_LOCATION) : root) + "/reports/");

            String reportPartialName = "develop".equalsIgnoreCase(parameters.get(RUN_MODE)) ? "develop" : TIME_STAMP;
            directories.put(CURRENT_REPORT, directories.get(REPORT) + reportPartialName);
            directories.put(CURRENT_SC, directories.get(REPORT) + reportPartialName + "/sc");

            directories.put(LOG_LOCATION,
                    params.containsKey(LOG_LOCATION) ? params.get(LOG_LOCATION) : directories.get(CURRENT_REPORT));
            directories.values().stream().forEach(f -> new File(f).mkdirs());
            directories.put(PARTIAL_NAME, reportPartialName);

        }
        catch (IOException e) {
            throw e;
        }
    }

    /**
     * This function should be called at the beginning of the session execution to set up
     * the environment parameter.
     * @param params : Map
     * @throws IOException : IOException
     * @throws FrameworkException : UIException
     */
    private void setEnvironmentParameter() {
        // Initialize driver
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        String osArch = System.getProperty("os.arch");

        if (os.indexOf("win") >= 0 && (osArch.indexOf("amd64") >= 0 || osArch.indexOf("x86") >= 0)) {
            directories.put(CHRONE_DRIVER, directories.get(RESOURCES) + "/drivers/chromedriver_win.exe");
            directories.put(IE_DRIVER, directories.get(RESOURCES) + "/drivers/IEDriverServer_32.exe");
            directories.put(GECKO_DRIVER, directories.get(RESOURCES) + "/drivers/geckodriver.exe");
        }
        else {
            directories.put(CHRONE_DRIVER, directories.get(RESOURCES) + "/drivers/chromedriver");
        }
    }

}
