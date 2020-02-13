package com.dougnoel.sentinel.elements;

import com.dougnoel.sentinel.enums.SelectorType;
import com.dougnoel.sentinel.exceptions.ConfigurationNotFoundException;
import com.dougnoel.sentinel.exceptions.ElementNotVisibleException;
import com.dougnoel.sentinel.exceptions.NoSuchElementException;
import com.dougnoel.sentinel.exceptions.NoSuchSelectorException;

/**
 * Check box implementation of a PageElement.
 */
public class Checkbox extends PageElement {

	/**
	 * 
	 * @param selectorType (CSS, ID, NAME, TEXT, XPATH)
	 * @param selectorValue the value of the selector we are using to access the checkbox
	 */
	public Checkbox(SelectorType selectorType, String selectorValue){
		super(selectorType, selectorValue);
	}
	
	/**
	 * Check a Checkbox PageElement. Created as an alias for click.
	 * <p>
	 * <b>Alias For:</b> PageElement.click()
	 * @return PageElement (for chaining)
	 * @throws NoSuchSelectorException if sentinel cannot find the selector used to identify the element
	 * @throws NoSuchElementException if sentinel cannot find the element it is trying to use
	 * @throws ElementNotVisibleException if element is not visible or disabled
	 * @throws ConfigurationNotFoundException if the requested configuration property has not been set
	 */
	public PageElement check() throws NoSuchSelectorException, NoSuchElementException, ElementNotVisibleException, ConfigurationNotFoundException {
		return this.click();
	}
		
	/**
	 * Un-check a Checkbox PageElement. Created as an alias for clear.
	 * <p>
	 * <b>Alias For:</b> PageElement.clear()
	 * @return PageElement (for chaining)
	 * @throws NoSuchSelectorException if sentinel cannot find the selector used to identify the element
	 * @throws NoSuchElementException if sentinel cannot find the element it is trying to use
	 */
	public PageElement uncheck() throws NoSuchSelectorException, NoSuchElementException{
		return this.clear();
	}

}
