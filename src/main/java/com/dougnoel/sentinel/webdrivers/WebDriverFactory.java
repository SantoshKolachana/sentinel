package com.dougnoel.sentinel.webdrivers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.safari.SafariDriver;

import com.dougnoel.sentinel.configurations.ConfigurationManager;
import com.dougnoel.sentinel.exceptions.ConfigurationNotFoundException;
import com.dougnoel.sentinel.exceptions.WebDriverNotExecutableException;
import com.dougnoel.sentinel.exceptions.WebDriverException;
import com.dougnoel.sentinel.filemanagers.DownloadManager;
import com.dougnoel.sentinel.strings.SentinelStringUtils;

/**
 * This object factory is used to keep up with driver versions for all browsers.
 * All the browser drivers can be found in the root of the project under the
 * drivers/[os]/ paths.
 * <p>
 * The purpose of this class is to use the
 * <a href="https://en.wikipedia.org/wiki/Factory_method_pattern">Factory Design
 * Pattern</a> to encapsulate the creation of drivers for various browsers and
 * share that functionality across projects. All drivers are stored as part of
 * the project and updated from one central repository. Pulling the latest code
 * from git will ensure you have the latest browser drivers and that you do not
 * need to spend time keeping them up to date. All drivers are assumed to be
 * 64-bit. Chromedriver and Internet Explorer drivers for Windows are 32-bit.
 * <p>
 * <b>Supported Browsers:</b>
 * <ul>
 * <li>Chrome
 * (<a href="http://chromedriver.chromium.org/downloads">Chromedriver</a>
 * 2.3.8)</li>
 * <li>Firefox
 * (<a href="https://github.com/mozilla/geckodriver/releases">Geckodriver</a>
 * 0.20.1)</li>
 * <li>Internet Explorer
 * (<a href="https://www.seleniumhq.org/download/">Internet Explorer Driver
 * Server</a> 3.12.0)</li>
 * <li>Safari (Safaridriver 10) (Comes installed on OS X)
 * </ul>
 * <p>
 * <b>Supported Operating Systems:</b>
 * <ul>
 * <li>Linux (Chrome/Firefox)</li>
 * <li>OS X (Chrome/Firefox/Safari)</li>
 * <li>Windows (Chrome/Firefox/Internet Explorer</li>
 * </ul>
 */
public class WebDriverFactory {
    private static final Logger log = LogManager.getLogger(WebDriverFactory.class); // Create a logger.
    private static WebDriver driver = null;

    private static WebDriverFactory instance = null;
    
    private static final String LINUX = "linux";
    private static final String MAC = "mac";
    private static final String WINDOWS = "windows";
    protected static final String DRIVERNOTFOUNDERRORMESSAGEPATTERN = "The driver does not have execute permissions or cannot be found. Make sure it is in the correct location. On linux/mac run chmod +x on the driver. If you passed in a location using the -Ddriver= command, ensure the path is correct and the driver is executable.\n{}";

    private WebDriverFactory() {
        // Exists only to defeat instantiation.
    }
    
    //TODO: Add all of the valid browser options and make them match saucelabs options
    /**
     * Creates and returns a useable WebDriver.
     * We use this factory method to handle keeping up with driver versions for all
     * browsers. All the browser drivers can be found in the root of the project
     * under the drivers/[os]/ paths. The browser can be set in the config file or a system
     * variable. See the README for more information.
     * @return WebDriver An initialized <a href="https://www.seleniumhq.org/">Selenium
     *         WebDriver</a> object for the specified browser and operating system
     *         combination.
     * @throws WebDriverException if error thrown while creating WebDriver instance
     * @throws ConfigurationNotFoundException if a needed configuration value cannot be found
     */
    public static WebDriver instantiateWebDriver() throws WebDriverException, ConfigurationNotFoundException {
        // Ensure we only have one instance of this class, so that we always return the
        // same driver.
        if (instance == null) {
            instance = new WebDriverFactory();
        }
        
        //Saucelabs Driver setup
        String saucelabsUserName = ConfigurationManager.getOptionalProperty("saucelabsUserName");
        if (saucelabsUserName != null) {
        	return driver = SauceLabsDriverFactory.createSaucelabsDriver(); //NOTE: Returning the driver here so that we do not need an extra else statement.
        }

        // Set a Download Directory if one was specified on the command line
        String downloadDirectory = ConfigurationManager.getOptionalProperty("download");
        if (downloadDirectory != null)
            DownloadManager.setDownloadDirectory(downloadDirectory);

        String browser = getBrowserName();

        // Initialize the driver object based on the browser and operating system (OS).
        // Throw an error if the value isn't found.   	
    	switch (browser) {
        case "chrome":
        	driver = ChromeDriverFactory.createChromeDriver();
            break;
        case "firefox":
        	driver = FirefoxDriverFactory.createFirefoxDriver();
            break;
        case "internetexplorer":
        	driver = createInternetExplorerDriver();
            break;
        case "safari":
        	driver = createSafariDriver();
            break;
        default:
            throw new WebDriverException(SentinelStringUtils.format("Invalid browser type '{}' passed to WebDriverFactory. Could not resolve the reference. Check your spelling. Refer to the Javadoc for valid options.", browser));
        }

        return driver;
    }

    /**
     * Returns the WebDriver instance. This will silently log an error if the WebDriver has not been instantiated yet.
     * 
     * @return WebDriver the created Selenium WebDriver
     */
    public static WebDriver getWebDriver()  {
        if (instance == null) {
        	String errorMessage = "WebDriver has not been created. Call WebDriver.instantiateWebDriver() before calling WebDriver.getWebDriver";
        	log.error(errorMessage);
        }
        return driver;
    }
    
    /**
     * Returns a sanitized version of the operating system set in the config file or on the command line.
     * @return String a sanitized string containing the operating system
     * @throws ConfigurationNotFoundException if the configuration data cannot be read
     */
    protected static String getOperatingSystem() throws ConfigurationNotFoundException {
    	//TODO: Add auto detection
    	//TODO Make this useable by Saucelabs driver
    	String operatingSystem = ConfigurationManager.getProperty("os");
        operatingSystem = operatingSystem.replaceAll("\\s+", "").toLowerCase();
        if (operatingSystem.equals("macintosh") || operatingSystem.equals("osx"))
            operatingSystem = MAC;
        else if (operatingSystem.equals("win"))
            operatingSystem = WINDOWS;
        
        return operatingSystem;
    }
    
    /**
     * Returns an error message string 
     * @return String error message
     * @throws ConfigurationNotFoundException if the configuration data cannot be read
     */
    protected static String getMissingOSConfigurationErrorMessage() throws ConfigurationNotFoundException {
    	String operatingSystem = ConfigurationManager.getProperty("os");
    	return SentinelStringUtils.format("Invalid operating system '{}' passed to WebDriverFactory. Could not resolve the reference. Check your spelling. Refer to the Javadocs for valid options.", operatingSystem);
        
    }
    
    /**
     * Returns an error message string 
     * @return String error message
     * @throws ConfigurationNotFoundException if the configuration data cannot be read
     */
    private static String getOSNotCompatibleWithBrowserErrorMessage() throws ConfigurationNotFoundException {
    	String operatingSystem = ConfigurationManager.getProperty("os");
    	String browser = ConfigurationManager.getProperty("browser");
    	return SentinelStringUtils.format("Invalid operating system '{}' passed to WebDriverFactory for the {} driver. Refer to the Javadocs for valid options.", operatingSystem, browser);
    	
    }
    
    /**
     * Returns a sanitized version of the browser set in the config file or on the command line.
     * @return String a sanitized string containing the browser
     * @throws ConfigurationNotFoundException if the configuration data cannot be read
     */
    private static String getBrowserName() throws ConfigurationNotFoundException {
    	//TODO: Add auto detection
    	//TODO Make this useable by Saucelabs driver
    	String browser;
		browser = ConfigurationManager.getProperty("browser");
        // Make sure whatever string we are passed is all lower case and all spaces are removed.
        browser = browser.replaceAll("\\s+", "").toLowerCase();
        if (browser.equals("ie"))
            browser = "internetexplorer";
        return browser;
    }
    
    /**
     * Returns the driver path if it exists, otherwise null.
     * @return String the driver path if it exists, otherwise null
     */
    protected static String getDriverPath() {
    	return ConfigurationManager.getOptionalProperty("driver");
    }
    
    /**
     * Creates an IE WebDriver and returns it.
     * @return WebDriver an IE WebDriver object
     * @throws WebDriverException if the WebDriver creation fails
     * @throws ConfigurationNotFoundException if the configuration data cannot be read
     */
    private static WebDriver createInternetExplorerDriver() throws WebDriverException, ConfigurationNotFoundException {
    	String driverPath = getDriverPath();
    	String errorMessage;
        switch (getOperatingSystem()) {
        case LINUX:
        case MAC:
        	errorMessage = getOSNotCompatibleWithBrowserErrorMessage();
        	log.error(errorMessage);
            throw new WebDriverException(errorMessage);
        case WINDOWS:
        	if (driverPath == null) {
        		driverPath = "src\\main\\resources\\drivers\\windows\\IEDriverServer.exe";
        	}
            break;
        default:
        	errorMessage = getMissingOSConfigurationErrorMessage();
        	log.error(errorMessage);
            throw new WebDriverException(errorMessage);
    	}
        System.setProperty("webdriver.ie.driver", driverPath);
    	InternetExplorerOptions options = new InternetExplorerOptions();
    	options.ignoreZoomSettings();
    	try {
    		return new InternetExplorerDriver(options);
    	}
		catch (IllegalStateException e) {
			errorMessage = SentinelStringUtils.format(DRIVERNOTFOUNDERRORMESSAGEPATTERN, e.getMessage());
			log.error(errorMessage);
			throw new WebDriverNotExecutableException(errorMessage, e);
		}
        catch (org.openqa.selenium.WebDriverException e) {
        	log.error(e.getMessage());
        	throw new WebDriverException(e);
        }
    }

    /**
     * Creates a Safari WebDriver and returns it.
     * @return WebDriver a Safari WebDriver object
     * @throws WebDriverException if the WebDriver creation fails
     * @throws ConfigurationNotFoundException if the configuration data cannot be read
     */
    private static WebDriver createSafariDriver() throws WebDriverException, ConfigurationNotFoundException {
        switch (getOperatingSystem()) {
        case LINUX:
        case WINDOWS:
            throw new WebDriverException(getOSNotCompatibleWithBrowserErrorMessage());
        case MAC:
            // Nothing to do here, as Apple has already set this up on macs.
            break;
        default:
            throw new WebDriverException(getMissingOSConfigurationErrorMessage());
        }
        return new SafariDriver();
    }
}