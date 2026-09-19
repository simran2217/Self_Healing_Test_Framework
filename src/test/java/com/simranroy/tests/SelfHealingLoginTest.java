package com.simranroy.tests;

import com.simranroy.framework.SmartElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.testng.Assert.assertTrue;

public class SelfHealingLoginTest {

    private WebDriver driver;
    private SmartElement smartElement;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        smartElement = new SmartElement(driver);
    }

    @Test
    public void loginFieldHealsWhenPrimaryLocatorIsBroken() {
        driver.get("https://the-internet.herokuapp.com/login");

        WebElement usernameField = smartElement.find("UsernameField", Arrays.asList(
                By.id("user_name_wrong"),
                By.name("username"),
                By.cssSelector("#username")
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

        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions
                        .presenceOfElementLocated(By.cssSelector(".flash.success")));

        assertTrue(driver.getPageSource().contains("You logged into a secure area"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
