package com.dougnoel.sentinel.elements;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.dougnoel.sentinel.configurations.TimeoutManager;
import com.dougnoel.sentinel.enums.SelectorType;
import com.dougnoel.sentinel.exceptions.ElementNotFoundException;
import com.dougnoel.sentinel.exceptions.ElementNotVisibleException;
import com.dougnoel.sentinel.exceptions.NoSuchElementException;
import com.dougnoel.sentinel.exceptions.NoSuchSelectorException;
import com.dougnoel.sentinel.exceptions.SentinelException;
import com.dougnoel.sentinel.strings.SentinelStringUtils;
import com.dougnoel.sentinel.webdrivers.WebDriverFactory;

/**
 * Base element class that handles creation of element types and allows late
 * binding. It takes a selector type, a selector value and a driver, and uses
 * that to exercise Selenium WebDriver functionality. Classes that extend this
 * are wrappers to allow clearer coding conventions. For example a Button
 * element just allows the specific declaration of a button on a web page for
 * functionality.
 * <p>
 * Selenium already has a WebElement class, but using it requires you to be able
 * to see the elements on the page as soon as you declare the page object. This
 * doesn't work, since we want to define the page before run time. The solution
 * to the problem is late binding, or declaring the objects as they are used.
 * Every time we use an element on the page, we look for it at the time we are
 * using it.
 * <p>
 * <b>Note:</b> Renamed from WebElement to PageElement to avoid name space
 * conflicts with selenium's WebElement object when Selenium's WebElement type
 * was needed as a return type for the element() function.
 * <p>
 * <b>To Do:</b>
 * <ul>
 * <li>Remove the need to pass in a driver class instance for the creation of
 * every element and delay that binding to a singleton class that can be
 * changed, allowing the use of multiple drivers through a testing session.</li>
 * <li>Look into creating a Selenium WebElement member variable to store the
 * element after the first time it is used. This could increase the speed of
 * calling objects that get called often.</li>
 * </ul>
 */
public class PageElement {
	private static final Logger log = LogManager.getLogger(PageElement.class.getName()); // Create a logger.

	protected SelectorType selectorType;
	protected String selectorValue;

	protected WebDriver driver;

	/**
	 * The constructor for a WebElement to initialize how an element is going to be
	 * found when it is worked on by the WebDriver class. Takes a reference to the
	 * WebDriver class that will be exercising its functionality.
	 * 
	 * @param selectorType
	 *            SelectorType
	 * @param selectorValue
	 *            String
	 */
	public PageElement(SelectorType selectorType, String selectorValue) {
		this.selectorType = selectorType;
		this.selectorValue = selectorValue;
		this.driver = WebDriverFactory.getWebDriver();
	}
	
	/**
	 * Wrapper for dealing with fluent waits when getting an element.
	 * 
	 * @param locator org.openqa.selenium.By
	 * @return org.openqa.selenium.WebElement
	 */
	private WebElement getElementWithWait(final By locator) {
		Duration timeout =  Duration.ofSeconds(TimeoutManager.getDefaultTimeout());
		Duration interval =  Duration.ofMillis(10);
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
			       .withTimeout(timeout)
			       .pollingEvery(interval)
			       .ignoring(NoSuchElementException.class);

		return wait.until(d -> driver.findElement(locator));
		
	}
	
	/**
	 * Returns the Selenium WebElement if it can be found on the current page.
	 * Provides late binding for elements so that the driver does not look for them
	 * until they are called, at which point the driver should be on the correct
	 * page.
	 * 
	 * @return org.openqa.selenium.WebElement the Selenium WebElement object type that can be acted upon
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	protected WebElement element() throws ElementNotFoundException  {
		WebElement element = null;

		try {
			switch (selectorType) {
			case CLASS:
				element = getElementWithWait(By.className(selectorValue));
				break;
			case CSS:
				element = getElementWithWait(By.cssSelector(selectorValue));
				break;
			case ID:
				element = getElementWithWait(By.id(selectorValue));
				break;
			case NAME:
				element = getElementWithWait(By.name(selectorValue));
				break;
			case PARTIALTEXT:
				element = getElementWithWait(By.partialLinkText(selectorValue));
				break;
			case TEXT:
				element = getElementWithWait(By.linkText(selectorValue));
				break;
			case XPATH:
				element = getElementWithWait(By.xpath(selectorValue));
				break;
			default:
				// This is here in case a new type is added to SelectorType and has not been
				// implemented yet here.
				String errorMessage = SentinelStringUtils.format(
						"Unhandled selector type \"{}\" passed to Page Element base class. Could not resolve the reference. Refer to the Javadoc for valid options.",
						selectorType);
				throw new NoSuchSelectorException(errorMessage);
			}
		} catch (org.openqa.selenium.NoSuchElementException e) {
			String errorMessage = SentinelStringUtils.format(
					"{} element does not exist or is not visible using the {} value \"{}\". Assure you are on the page you think you are on, and that the element identifier you are using is correct.",
					this.getClass().getSimpleName(), selectorType, selectorValue);
			throw new NoSuchElementException(errorMessage, e);
		}
		if (element == null) {
			String errorMessage = SentinelStringUtils.format(
					"{} element does not exist or is not visible using the {} value \"{}\". Assure you are on the page you think you are on, and that the element identifier you are using is correct.",
					this.getClass().getSimpleName(), selectorType, selectorValue);
			throw new NoSuchElementException(errorMessage);
		}
		return element;
	}	

	/**
	 * Type text into a PageElement.
	 * <p>
	 * <b>Aliases:</b>
	 * <ul>
	 * <li>Textbox.type(text)</li>
	 * </ul>
	 * 
	 * @param text
	 *            String (text to type)
	 * @return PageElement (for chaining)
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public PageElement sendKeys(String text) throws ElementNotFoundException {
		element().click();
		element().clear();
		element().sendKeys(text);
		return this;
	}

	public PageElement javaScriptSendKeys(String text) throws SentinelException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].value='" + text + "';", element());

		return this;
	}

	/**
	 * Press keys with focus on a PageElement. This is useful when type() or
	 * sendKeys isn't working due to a mask or hidden field being employed to grab
	 * key press events and operate on each one.
	 * 
	 * @param text
	 *            String (keys to type)
	 * @return PageElement (for chaining)
	 * @throws AWTException if the key cannot be pressed.
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public PageElement pressKeys(String text) throws AWTException, ElementNotFoundException {
		// Ensure that the element has focus.
		if ("input".equals(element().getTagName())) {
			element().sendKeys("");
		} else {
			new Actions(driver).moveToElement(element()).perform();
		}

		// Iterate through the string and press every key
		Robot robot = new Robot();
		robot.delay(1000);
		char[] chars = text.toCharArray();

		for (char c : chars) {
			log.debug(c);
			robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
			robot.delay(1000);
			robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
			robot.delay(1000);
		}

		return this;
	}

	/**
	 * Click a PageElement.
	 * <p>
	 * This function waits up to 10 seconds in 500 millisecond increments to see if
	 * the element is visible. This wait ensures that context-switching, such as
	 * bringing up a pop-up, AJAX calls, etc. will not fail a test.
	 * <p>
	 * <b>Aliases:</b>
	 * <ul>
	 * <li>Checkbox.check()</li>
	 * <li>Radiobutton.select()</li>
	 * </ul>
	 * 
	 * @return PageElement (for chaining)
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public PageElement click() throws ElementNotFoundException  {
		long waitTime = TimeoutManager.getDefaultTimeout();
		try {
			new WebDriverWait(driver, waitTime).until(ExpectedConditions.elementToBeClickable(element())).click();
		} catch (WebDriverException e) {
			try {
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", element());
			} catch (Exception e2) {
				String message = SentinelStringUtils.format(
						"{} element is not visible using the {} value \"{}\" and cannot be clicked. Make sure the element is visible on the page when you attempt to click it. Clicking was attempted once with a mouse click and once with the Return key. The total wait time was {} seconds.",
						this.getClass().getSimpleName(), selectorType, selectorValue, waitTime);
				log.error(message);
				throw new ElementNotVisibleException(message, e2);
			}
		}
		return this;
	}

	/**
	 * Clear a PageElement. Clears text in a text box. Un-checks check boxes. Clears
	 * radio button choices.
	 * <p>
	 * <b>Aliases:</b>
	 * <ul>
	 * <li>Checkbox.uncheck()</li>
	 * </ul>
	 * 
	 * @return PageElement (for chaining)
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public PageElement clear() throws ElementNotFoundException {
		element().clear();
		return this;
	}

	/**
	 * Returns true if the element is enabled within 10 seconds; otherwise returns
	 * false.
	 * 
	 * @return boolean true if the element is enabled within 10 seconds; otherwise
	 *         returns false.
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public boolean isEnabled() throws ElementNotFoundException {
		return isEnabled(10);
	}

	/**
	 * Returns true if the element is enabled within the number of seconds
	 * indicated; otherwise returns false.
	 * <p>
	 * This function waits a number of seconds in 500 millisecond increments to see
	 * if the element is visible. This wait ensures that context-switching, such as
	 * bringing up a pop-up, AJAX calls, etc. will not fail a test.
	 * <p>
	 * A StaleElementReferenceException can be thrown when testing a Bootstrap
	 * website that uses divs as popups. We resolve this by catching the exception
	 * and retrying it 5 times. If it still fails, we catch the exception and return
	 * a failure indicating the element wasn't found instead of throwing an
	 * exception.
	 * 
	 * @param seconds
	 *            int the number of seconds to wait before returning failure.
	 * @return boolean true if the element is enabled within the number of seconds
	 *         indicated; otherwise returns false.
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public boolean isEnabled(int seconds) throws ElementNotFoundException {
		int retries = 0;
		while (true) {
			try {
				return new WebDriverWait(driver, seconds).until(ExpectedConditions.elementToBeClickable(element()))
						.isEnabled();
			} catch (StaleElementReferenceException e) {
				if (retries < 5) {
					retries++;
				} else {
					return false;
				}
			} catch (TimeoutException e) {
				return false;
			}
		}
	}

	/**
	 * Validates whether or not the element is selected.
	 * @return boolean true if the element is selected, false if it is not
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public boolean isSelected() throws ElementNotFoundException {
		return element().isSelected();
	}

	/**
	 * Returns true if the element is displayed within 10 seconds; otherwise returns
	 * false.
	 * 
	 * @return boolean true if the element is displayed within 10 seconds; otherwise
	 *         returns false.
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public boolean isDisplayed() throws ElementNotFoundException {
		return isDisplayed(10);
	}

	/**
	 * Returns true if the element is displayed within the number of seconds
	 * indicated; otherwise returns false.
	 * <p>
	 * This function waits a number of seconds in 500 millisecond increments to see
	 * if the element is visible. This wait ensures that context-switching, such as
	 * bringing up a pop-up, AJAX calls, etc. will not fail a test.
	 * <p>
	 * A StaleElementReferenceException can be thrown when testing a Boostrap
	 * website that uses divs as popups. We resolve this by catching the exception
	 * and retrying it 5 times. If it still fails, we catch the exception and return
	 * a failure indicating the element wasn't found instead of throwing an
	 * exception.
	 * 
	 * @param seconds
	 *            int the number of seconds to wait before returning failure.
	 * @return boolean true if the element is displayed within the number of seconds
	 *         indicated; otherwise returns false.
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public boolean isDisplayed(int seconds) throws ElementNotFoundException  {
		int retries = 0;
		while (true) {
			try {
				return new WebDriverWait(driver, seconds).until(ExpectedConditions.visibilityOf(element()))
						.isDisplayed();
			} catch (StaleElementReferenceException e) {
				if (retries < 5) {
					retries++;
				} else {
					return false;
				}
			} catch (TimeoutException e) {
				return false;
			}
		}
	}

	/**
	 * Determines with 250 milliseconds (1/4 of a second) if an element is not present.
	 * This should be used when you expect an element to not be present and do not want
	 * to slow down your tests waiting for the normal timeout time to expire.
	 * @return boolean true if the element cannot be found, false if it is found
	 * @throws NoSuchSelectorException if the selector type passed is invalid
	 */
	public boolean doesNotExist() throws NoSuchSelectorException {
		By locator = null;
		boolean flag = false;

		switch (selectorType) {
		case CSS:
			locator = By.cssSelector(selectorValue);
			break;
		case ID:
			locator = By.id(selectorValue);
			break;
		case NAME:
			locator = By.name(selectorValue);
			break;
		case PARTIALTEXT:
			locator = By.partialLinkText(selectorValue);
			break;
		case TEXT:
			locator = By.linkText(selectorValue);
			break;
		case XPATH:
			locator = By.xpath(selectorValue);
			break;
		default:
			// This is here in case a new type is added to SelectorType and has not been
			// implemented yet here.
			String errorMessage = SentinelStringUtils.format(
					"Unhandled selector type \"{}\" passed to Page Element base class. Could not resolve the reference. Refer to the Javadoc for valid options.",
					selectorType);
			throw new NoSuchSelectorException(errorMessage);
		}
		flag = new WebDriverWait(driver, 1).until(ExpectedConditions.invisibilityOfElementLocated(locator));
		log.trace("Return result: {}", flag);
		return flag;
	}

	/**
	 * Returns the text of the page element as a String.
	 * 
	 * @return String The text value stored in the element.
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public String getText() throws ElementNotFoundException {
		return element().getText();
	}

	/**
	 * Returns the WebElement wrapped inside the PageElement so that it can be acted
	 * upon inside of step definitions.
	 * @return org.openqa.selenium.WebElement
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public WebElement toWebElement() throws ElementNotFoundException  {
		return element();
	}

	/**
	 * Verifies if the element has a class value.
	 * <p>
	 * <b>Examples:</b>
	 * <ul>
	 * <li>Determine if an element is highlighted because class="active"</li>
	 * </ul>
	 * 
	 * @param text String the class to verify
	 * @return boolean
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public boolean hasClass(String text) throws ElementNotFoundException {
		String classes = element().getAttribute("class");
		log.debug("Classes found on element {}: {}", this.getClass().getName(), classes);
		for (String c : classes.split(" ")) {
			if (c.equals(text)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if the element as an attribute equal to the value passed;
	 * otherwise returns false.
	 * <p>
	 * <b>Examples:</b>
	 * <ul>
	 * <li>Determine if an element has a style="display:none" attribute set.
	 * </ul>
	 * 
	 * @param attribute String the attribute to look for
	 * @param value String the value to which attribute should be set
	 * @return boolean true if the element as an attribute equal to the value passed; otherwise returns false
	 * @throws ElementNotFoundException if the element cannot be found
	 */
	public boolean attributeEquals(String attribute, String value) throws ElementNotFoundException {
		String values = element().getAttribute(attribute);
		log.debug("Values found for attribute {} on element {}: {}", attribute, this.getClass().getName(),
				values);
		if (values.equals(value)) {
			return true;
		} else {
			for (String c : values.split(" ")) {
				if (c.equals(value)) {
					return true;
				}
			}
		}

		return false;
	}

}
