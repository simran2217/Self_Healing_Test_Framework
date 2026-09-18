package com.simranroy.framework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;
import java.util.logging.Logger;

public class SmartElement {

    private static final Logger logger = Logger.getLogger(SmartElement.class.getName());
    private final WebDriver driver;

    public SmartElement(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement find(String elementName, List<By> locatorStrategies) {
        for (int i = 0; i < locatorStrategies.size(); i++) {
            By locator = locatorStrategies.get(i);
            try {
                WebElement element = driver.findElement(locator);
                if (i == 0) {
                    logger.info("[" + elementName + "] Primary locator worked: " + locator);
                } else {
                    logger.warning("[" + elementName + "] Primary locator FAILED. "
                            + "Self-healed using fallback #" + i + ": " + locator);
                }
                return element;
            } catch (NoSuchElementException e) {
                logger.info("[" + elementName + "] Locator failed: " + locator + " — trying next strategy...");
            }
        }
        throw new NoSuchElementException(
                "[" + elementName + "] All " + locatorStrategies.size() + " locator strategies failed.");
    }
}