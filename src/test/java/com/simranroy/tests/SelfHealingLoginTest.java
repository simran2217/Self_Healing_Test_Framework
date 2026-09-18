package com.simranroy.tests;

import com.simranroy.framework.SmartElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.testng.Assert.assertTrue;

public class SelfHealingLoginTest {

    private WebDriver driver;
    private SmartElement smartElement;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        smartElement = new SmartElement(driver);
    }

    @Test
    public void loginFieldHealsWhenPrimaryLocatorIsBroken() {
        driver.get("https://the-internet.herokuapp.com/login");

        // Simulating a UI change: the real id is "username", but our
        // primary locator below is deliberately wrong — as if a developer
        // renamed the field. Watch the console: it should "self-heal"
        // using the fallback locator instead of failing.
        WebElement usernameField = smartElement.find("UsernameField", Arrays.asList(
                By.id("user_name_wrong"),      // primary - intentionally broken
                By.name("username"),           // fallback 1 - this will work
                By.cssSelector("#username")    // fallback 2 - backup
        ));
        usernameField.sendKeys("tomsmith");

        WebElement passwordField = smartElement.find("PasswordField", Arrays.asList(
                By.id("password"),
                By.name("password")
        ));
        passwordField.sendKeys("SuperSecretPassword!");

        WebElement loginButton = smartElement.find("LoginButton", Arrays.asList(
                By.cssSelector("button.radius")
        ));
        loginButton.click();

        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions
                        .presenceOfElementLocated(By.cssSelector(".flash.success")));

        assertTrue(Objects.requireNonNull(driver.getPageSource()).contains("You logged into a secure area"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}