package com.codecool.fki.JiraTestBDD;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogoutStepDefinitions {
    private final WebDriver chromeDriver = new ChromeDriver();
    private final WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(5));

    private static final String homePage = "https://jira-auto.codecool.metastage.net/login.jsp";
    private int errorCounter = 0;
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("src/test/java/assets")
            .filename(".env")
            .load();

    @Given("I am logged in on Jira page")
    public void iAmLoggedInOnJiraPage() {
        String PASSWORD = dotenv.get("PASSWORD");
        String USERNAME = dotenv.get("USER_NAME");
        chromeDriver.get(homePage);
        WebElement usernameInput = chromeDriver.findElement(By.id("login-form-username"));
        WebElement passwordInput = chromeDriver.findElement(By.id("login-form-password"));
        WebElement loginButton = chromeDriver.findElement(By.id("login-form-submit"));
        usernameInput.sendKeys(USERNAME);
        passwordInput.sendKeys(PASSWORD);
        loginButton.click();
        System.out.println("Given");
    }

    @When("I press LogOut button")
    public void iPressLogOutButton() {
        WebElement userOptions = chromeDriver.findElement(By.id("user-options"));
        WebElement logOutBtn= chromeDriver.findElement(By.id("log_out"));
        userOptions.click();
        logOutBtn.click();
        WebElement logOutSubmit= chromeDriver.findElement(By.id("confirm-logout-submit"));
        logOutSubmit.click();
        System.out.println("When");
    }

    @Then("I am redirected to new login page")
    public void iAmRedirectedToNewLoginPage() {
        WebElement contentOfPage= chromeDriver.findElement(By.id("content"));
        assertTrue(contentOfPage.getText().contains("You are now logged out."));
        System.out.println("Then");
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            File screenshot = ((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE);
            String filename = String.format(".errorPicture/logout%d.png", errorCounter);
            try {
                FileUtils.copyFile(screenshot, new File(filename));
                errorCounter++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        chromeDriver.quit();
        System.out.println("After");
    }
}
