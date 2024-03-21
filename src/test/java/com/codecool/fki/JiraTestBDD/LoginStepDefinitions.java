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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class LoginStepDefinitions {
    private final WebDriver chromeDriver = new ChromeDriver();
    private final WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(5));

    private static final String homePage = "https://jira-auto.codecool.metastage.net/login.jsp";
    private int errorCounter = 0;
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("src/test/java/assets")
            .filename(".env")
            .load();

    private static String PASSWORD;
    private static String USERNAME;
    private WebElement usernameInput;
    private WebElement passwordInput;
    private WebElement loginButton;

    @Given("I open the Jira home page")
    public void iOpenTheJiraHomePage() {
        chromeDriver.get(homePage);
    }

    @Given("I have valid username and password")
    public void iHaveValidUsernameAndPassword() {
        PASSWORD = dotenv.get("PASSWORD");
        USERNAME = dotenv.get("USER_NAME");
    }

    @When("I log in  with valid username and password")
    public void iLogInWithValidUsernameAndPassword() {
        usernameInput = chromeDriver.findElement(By.id("login-form-username"));
        passwordInput = chromeDriver.findElement(By.id("login-form-password"));
        loginButton = chromeDriver.findElement(By.id("login-form-submit"));
        usernameInput.sendKeys(USERNAME);
        passwordInput.sendKeys(PASSWORD);
        loginButton.click();
    }

    @Then("I am redirected to dashboard page")
    public void iAmRedirectedToDashboardPage() {
        wait.until(ExpectedConditions.urlContains("Dashboard"));
    }

    @When("I try to log in with {string} and {string}")
    public void iTryToLogInWithAnd(String username, String password) {
        usernameInput = chromeDriver.findElement(By.id("login-form-username"));
        passwordInput = chromeDriver.findElement(By.id("login-form-password"));
        loginButton = chromeDriver.findElement(By.id("login-form-submit"));
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();
    }

    @Then("warning message appears")
    public void warningMessageAppears() {
        WebElement invalidCredentialsNotification = chromeDriver.findElement(By.xpath("//p[contains(text(), 'Sorry')]"));
        invalidCredentialsNotification.isDisplayed();
    }


    @Given("I have a valid username")
    public void iHaveAValidUsername() {
        USERNAME = dotenv.get("USER_NAME");
    }

    @When("I try to login with valid username and {string} password")
    public void iTryToLoginWithValidUsernameAndInvalidPassword(String password) {
        usernameInput = chromeDriver.findElement(By.id("login-form-username"));
        passwordInput = chromeDriver.findElement(By.id("login-form-password"));
        loginButton = chromeDriver.findElement(By.id("login-form-submit"));
        usernameInput.sendKeys(USERNAME);
        passwordInput.sendKeys(password);
        loginButton.click();
    }

    @Then("captcha become visible")
    public void captchaBecomeVisible() {
        WebElement captchaField = chromeDriver.findElement(By.id("captcha"));
        captchaField.isDisplayed();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            File screenshot = ((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE);
            String filename = String.format(".errorPicture/login%d.png", errorCounter);
            try {
                FileUtils.copyFile(screenshot, new File(filename));
                errorCounter++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        chromeDriver.quit();
    }
}
