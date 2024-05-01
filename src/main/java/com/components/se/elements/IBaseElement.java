package com.components.se.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import com.components.se.enums.LocatorType;
import com.exception.FrameworkException;

public interface IBaseElement {

    /**
     * Find all elements within the current context using the given mechanism. When using
     * xpath be aware that webdriver follows standard conventions: a search prefixed with
     * "//" will search the entire document, not just the children of this current node.
     * Use ".//" to limit your search to the children of this WebElement. This method is
     * affected by the 'implicit wait' times in force at the time of execution. When
     * implicitly waiting, this method will return as soon as there are more than 0 items
     * in the found collection, or will return an empty list if the timeout is reached.
     * @param by The locating mechanism to use
     * @return A list of all {@link IBaseElement}s, or an empty list if nothing matches.
     * @throws FrameworkException throw a UIException
     * @see org.openqa.selenium.By
     * @see org.openqa.selenium.WebDriver.Timeouts
     */
    public List<IBaseElement> findElements(By by) throws FrameworkException;

    /**
     * Find all elements within the current context using the given mechanism. When using
     * xpath be aware that webdriver follows standard conventions: a search prefixed with
     * "//" will search the entire document, not just the children of this current node.
     * Use ".//" to limit your search to the children of this WebElement. This method is
     * affected by the 'implicit wait' times in force at the time of execution. When
     * implicitly waiting, this method will return as soon as there are more than 0 items
     * in the found collection, or will return an empty list if the timeout is reached.
     * @param locator
     * @param type The locating mechanism to use
     * @return A list of all {@link IBaseElement}s, or an empty list if nothing matches.
     * @throws FrameworkException throw a UIException
     * @see org.openqa.selenium.By
     * @see org.openqa.selenium.WebDriver.Timeouts
     */
    public List<IBaseElement> findElements(String locator, LocatorType type) throws FrameworkException;

    /**
     * Find the first {@link IBaseElement} using the given method. See the note in
     * {@link #findElements(By)} about finding via XPath. This method is affected by the
     * 'implicit wait' times in force at the time of execution. The findElement(..)
     * invocation will return a matching row, or try again repeatedly until the configured
     * timeout is reached.
     *
     * findElement should not be used to look for non-present elements, use
     * {@link #findElements(By)} and assert zero length response instead.
     * @param by The locating mechanism
     * @return The first matching element on the current context.
     * @throws FrameworkException throw a UIException
     * @see org.openqa.selenium.By
     * @see org.openqa.selenium.WebDriver.Timeouts
     */
    public IBaseElement findElement(By by) throws FrameworkException;

    // state checking
    /**
     * Determine whether or not this element is detached from DOM or not.
     * @return boolean
     */
    public boolean isPresent();

    /**
     * Determine whether or not this element is visible from DOM or not.
     * @return boolean
     */
    public boolean isDisplayed() throws FrameworkException;

    /**
     * Determine whether or not this element is selected or not. This operation only
     * applies to input elements such as checkboxes, options in a select and radio
     * buttons.
     * @return boolean
     */
    public boolean isEnable() throws FrameworkException;

    /**
     * Is this element selected
     * @return boolean
     */
    public boolean isSelected();

    /**
     * To click check box. If given value is true it will check. If given value is false
     * it will uncheck.
     * @param check boolean
     * @throws FrameworkException throw a UIException
     */
    public void setCheck(boolean check) throws FrameworkException;

    // wait for state to change
    /**
     * wait for the invoking element to be enabled.
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void waitForEnable() throws FrameworkException;

    /**
     * wait for the invoking element to be clickable.
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void waitForClickable() throws FrameworkException;

    /**
     * wait for the invoking element to be disabled.
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void waitForDisable() throws FrameworkException;

    /**
     * wait for the invoking element to be visible.
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void waitForVisible() throws FrameworkException;

    /**
     * wait for the invoking element to be invisible.
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void waitForInvisible() throws FrameworkException;

    /**
     * wait for the invoking element to be detached from DOM.
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void waitForNotPresent() throws FrameworkException;

    // click

    /**
     * Click this element. If this causes a new page to load, you should discard all
     * references to this element and any further operations performed on this element
     * will throw a StaleElementReferenceException.
     *
     * Note that if click() is done by sending a native event (which is the default on
     * most browsers/platforms) then the method will _not_ wait for the next page to load
     * and the caller should verify that themselves.
     *
     *
     * There are some preconditions for an element to be clicked. The element must be
     * visible and it must have a height and width greater then 0.
     * @throws FrameworkException throw a UIException
     */
    public void click() throws FrameworkException;

    /**
     * Click an element using JavascriptExecutor. Use when normal click doesn't
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void clickAsJScript() throws FrameworkException;

    /**
     * perform double click on the invoking element
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void doubleClick() throws FrameworkException;

    /**
     * Performs a context-click at middle of the given element. First performs a mouseMove
     * to the location of the element.
     * @throws FrameworkException throw a UIException
     */
    public void rightClick() throws FrameworkException;

    // text input

    /**
     * Get the visible (i.e. not hidden by CSS) innerText of this element, including
     * sub-elements, without any leading or trailing whitespace.
     * @return The innerText of this element.
     */
    public String getText() throws FrameworkException;

    /**
     * Set text to the element
     * @param text sequence of characters
     * @throws FrameworkException Any exception will be wrapped in UIExcepton
     */
    public void setText(CharSequence... text) throws FrameworkException;

    /**
     * Use this method to append text into an element, which may set its value.
     * @param keysToSend character sequence to append to the element
     * @throws FrameworkException throw a UIException
     *
     */
    public void appendText(CharSequence... text) throws FrameworkException;

    /**
     * If this element is a text entry element, this will clear the value. Has no effect
     * on other elements. Text entry elements are INPUT and TEXTAREA elements.
     *
     * Note that the events fired by this event may not be as you'd expect. In particular,
     * we don't fire any keyboard or mouse events. If you want to
     */
    public void clear() throws FrameworkException;

    // getters
    /**
     * Where on the page is the top left-hand corner of the rendered element?
     * @return A point, containing the location of the top left-hand corner of the element
     */
    public Point getLocation() throws FrameworkException;

    /**
     * What is the width and height of the rendered element?
     * @return The size of the element on the page.
     */
    public Dimension getSize() throws FrameworkException;

    /**
     * @return The location and size of the rendered element
     */
    public Rectangle getRect() throws FrameworkException;

    /**
     * Get the value of a given CSS property. Color values should be returned as rgba
     * strings, so, for example if the "background-color" property is set as "green" in
     * the HTML source, the returned value will be "rgba(0, 255, 0, 1)".
     *
     * Note that shorthand CSS properties (e.g. background, font, border, border-top,
     * margin, margin-top, padding, padding-top, list-style, outline, pause, cue) are not
     * returned, in accordance with the
     * <a href= "http://www.w3.org/TR/DOM-Level-2-Style/css.html#CSS-CSSStyleDeclaration">
     * DOM CSS2 specification</a> - you should directly access the longhand properties
     * (e.g. background-color) to access the desired values.
     * @param propertyName the css property name of the element
     * @return The current, computed value of the property.
     */
    public String getCssValue(String propertyName) throws FrameworkException;

    /**
     * Get the value of a the given attribute of the element. Will return the current
     * value, even if this has been modified after the page has been loaded. More exactly,
     * this method will return the value of the given attribute, unless that attribute is
     * not present, in which case the value of the property with the same name is returned
     * (for example for the "value" property of a textarea element). If neither value is
     * set, null is returned. The "style" attribute is converted as best can be to a text
     * representation with a trailing semi-colon. The following are deemed to be "boolean"
     * attributes, and will return either "true" or null:
     *
     * async, autofocus, autoplay, checked, compact, complete, controls, declare,
     * defaultchecked, defaultselected, defer, disabled, draggable, ended, formnovalidate,
     * hidden, indeterminate, iscontenteditable, ismap, itemscope, loop, multiple, muted,
     * nohref, noresize, noshade, novalidate, nowrap, open, paused, pubdate, readonly,
     * required, reversed, scoped, seamless, seeking, selected, spellcheck, truespeed,
     * willvalidate
     *
     * Finally, the following commonly mis-capitalized attribute/property names are
     * evaluated as expected:
     *
     * <ul>
     * <li>"class"
     * <li>"readonly"
     * </ul>
     * @param name The name of the attribute.
     * @return The attribute/property's current value or null if the value is not set.
     */
    public String getAttribute(String name) throws FrameworkException;

    /**
     * Get the tag name of this element. <b>Not</b> the value of the name attribute: will
     * return <code>"input"</code> for the element
     * <code>&lt;input name="foo" /&gt;</code>.
     * @return The tag name of this element.
     */
    public String getTagName();

    // others
    /**
     * Scrolls the element on which it's called into the visible area of the browser
     * window.
     * @param isAlignTop Is a Boolean value: If true, the top of the element will be
     * aligned to the top of the visible area of the scrollable ancestor. Corresponds to
     * scrollIntoViewOptions: {block: "start", inline: "nearest"}. This is the default
     * value. If false, the bottom of the element will be aligned to the bottom of the
     * visible area of the scrollable ancestor. Corresponds to scrollIntoViewOptions:
     * {block: "end", inline: "nearest"}.
     * @throws FrameworkException throw a UIException
     */
    public void scrollIntoView(boolean isAlignTop) throws FrameworkException;

    /**
     * Scrolls the element on which it's called into the visible area of the browser
     * window and align the element to the top of the browser
     * @throws FrameworkException throw a UIException
     */
    public void scrollIntoView() throws FrameworkException;

    /**
     * Execute the javascript code on an element using JavascriptExecutor with given
     * parameters
     * @param javascriptToExecute Java script to execute
     * @return Return value of executed java script code with given parameters.
     * @throws FrameworkException throw a UIException
     */
    public Object executeScript(String javascriptToExecute, Object... parameters) throws FrameworkException;

    /**
     * Moves the mouse to the middle of the element. The element is scrolled into view and
     * its location is calculated using getBoundingClientRect.
     * @throws FrameworkException throw a UIException
     */
    public void hover() throws FrameworkException;

    /**
     * Moves the mouse to an offset from the top-left corner of the element. The element
     * is scrolled into view and its location is calculated using getBoundingClientRect.
     * @param xOffset Offset from the top-left corner. A negative value means coordinates
     * left from the element.
     * @param yOffset Offset from the top-left corner. A negative value means coordinates
     * above the element.
     * @throws FrameworkException throw a UIException
     */
    public void hover(int xOffset, int yOffset) throws FrameworkException;

    /**
     * Drag invoking element to the target element
     * @throws FrameworkException throw a UIException
     */
    public void dragTo(IBaseElement target) throws FrameworkException;

    /**
     * Drag invoking element to the target element
     * @throws FrameworkException throw a UIException
     */
    public void dragTo(Point target) throws FrameworkException;

    /**
     * @throws FrameworkException throw a UIException
     */
    public void highlight() throws FrameworkException;

    /**
     * @throws FrameworkException throw a UIException
     */
    public WebElement getWrappedElement();

    /**
     * Select all options that have a value matching the argument. That is, when given
     * "foo" this would select an option like:
     *
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     * @param value The value to match against
     * @throws FrameworkException If no matching option elements are found
     */
    public void selectByValue(String value) throws FrameworkException;

    /**
     * Select the option at the given index. This is done by examining the "index"
     * attribute of an element, and not merely by counting.
     * @param index The option at this index will be selected
     * @throws FrameworkException If no matching option elements are found
     */
    public void selectByIndex(int index) throws FrameworkException;

    /**
     * Select all options that display text matching the argument. That is, when given
     * "Bar" this would select an option like:
     *
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     * @param text The visible text to match against
     * @throws FrameworkException If no matching option elements are found
     */
    public void selectByText(String text) throws FrameworkException;

    /**
     * Deselect all options that have a value matching the argument. That is, when given
     * "foo" this would deselect an option like:
     *
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     * @param value The value to match against
     * @throws FrameworkException If no matching option elements are found
     */
    public void deselectByValue(String value) throws FrameworkException;

    /**
     * Deselect the option at the given index. This is done by examining the "index"
     * attribute of an element, and not merely by counting.
     * @param index The option at this index will be deselected
     * @throws FrameworkException If no matching option elements are found
     */
    public void deselectByIndex(int index) throws FrameworkException;

    /**
     * Deselect all options that display text matching the argument. That is, when given
     * "Bar" this would deselect an option like:
     *
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     * @param text The visible text to match against
     * @throws FrameworkException If no matching option elements are found
     */
    public void deselectByText(String text) throws FrameworkException;

    /**
     * @return The first selected option in this select tag (or the currently selected
     * option in a normal select)
     * @throws NoSuchElementException If no option is selected
     */
    public String getSelectedItemValue() throws FrameworkException;

    /**
     * @return The first selected option in this select tag (or the currently selected
     * option in a normal select) as IBaseElement
     * @throws NoSuchElementException If no option is selected
     */
    public IBaseElement getSelectedItem() throws FrameworkException;

    /**
     * @return The all selected options in this select tag (or the currently selected
     * option in a normal select) as List of IBaseElement
     * @throws NoSuchElementException If no option is selected
     */
    public List<IBaseElement> getSelectedItems() throws FrameworkException;

}
