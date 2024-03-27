package com.codecool.fki.JiraTestBDD;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EditIssueStepDefinitions {

    private final WebDriver chromeDriver = new ChromeDriver();
    private final WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(15));
    private static final String homePage = "https://jira-auto.codecool.metastage.net/login.jsp";
    private int errorCounter = 0;
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("src/test/java/assets")
            .filename(".env")
            .load();

    private static final String PASSWORD1 = dotenv.get("PASSWORD");
    private static final String USERNAME1 = dotenv.get("USER_NAME");

    private static final String nameOfNewIssue = "fki-BDD-testIssue";

    //Background:
    @Given("I am logged in on Jira account")
    public void iAmLoggedInOnJiraAccount() {
        chromeDriver.get(homePage);
        WebElement usernameInput = chromeDriver.findElement(By.id("login-form-username"));
        WebElement passwordInput = chromeDriver.findElement(By.id("login-form-password"));
        WebElement loginButton = chromeDriver.findElement(By.id("login-form-submit"));
        usernameInput.sendKeys(USERNAME1);
        passwordInput.sendKeys(PASSWORD1);
        loginButton.click();
    }

    @And("I am on {string} issues page")
    public void iAmOnIssuesPage(String project) {
        String url = String.format("https://jira-auto.codecool.metastage.net/projects/%1$s/issues/%1$s?filter=allopenissues", project);
        chromeDriver.get(url);

    }

    @And("I create a new issue")
    public void iCreateANewIssue() {
       /* Wait<WebDriver> wait = new FluentWait<WebDriver>(chromeDriver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);*/
        WebElement createIssueBtn_inNavBar = chromeDriver.findElement(By.id("create_link"));
        createIssueBtn_inNavBar.click();
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(chromeDriver.findElement(By.id("summary"))));
        WebElement summaryInput_OfIssue_inPopUp = chromeDriver.findElement(By.id("summary"));
        summaryInput_OfIssue_inPopUp.sendKeys(nameOfNewIssue);
        WebElement createIssueBtn_inPopUp = chromeDriver.findElement(By.id("create-issue-submit"));
        createIssueBtn_inPopUp.click();
        wait.until(ExpectedConditions.invisibilityOf(chromeDriver.findElement(By.id("aui-flag-container"))));
        chromeDriver.navigate().refresh();
        //TODO:
        String xpathOfNewIssue = String.format("//li[@title='%s']", nameOfNewIssue);
        wait.until(ExpectedConditions.visibilityOf(chromeDriver.findElement(By.xpath(xpathOfNewIssue))));
        WebElement newIssue = chromeDriver.findElement(By.xpath(xpathOfNewIssue));
        newIssue.click();

    }

    @Given("I am opening the edit issue tag")
    public void iAmOpeningTheEditIssueTag() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[@id='summary-val' and contains(text(), '" + nameOfNewIssue + "')]")));
        WebElement editIssueBtn = chromeDriver.findElement(By.id("edit-issue"));
        editIssueBtn.click();
        WebElement descriptionInput_inPopup = chromeDriver.findElement(By.id("mce_0_ifr"));
        wait.until(ExpectedConditions.visibilityOf(descriptionInput_inPopup));
    }


    @And("save my changes with Update")
    public void saveMyChangesWithUpdate() {
        WebElement updateBtn_inPopup = chromeDriver.findElement(By.id("edit-issue-submit"));
        updateBtn_inPopup.click();
    }


    @When("I type {string} to description field")
    public void iTypeToDescriptionField(String descText) {
        WebElement descriptionInput_inPopup = chromeDriver.findElement(By.id("mce_0_ifr"));
        descriptionInput_inPopup.sendKeys(descText);
    }

    @Then("the description displays {string}")
    public void theDescriptionDisplays(String descText) {
        wait.until(ExpectedConditions.textToBePresentInElement(chromeDriver.findElement(By.xpath("//div[@id='description-val']/div/p")), descText));
        WebElement descriptionOfIssue = chromeDriver.findElement(By.xpath("//div[@id='description-val']/div/p"));
        assertEquals(descriptionOfIssue.getText(), descText);
    }

    @When("I change the priority to {string}")
    public void iChangeThePriorityTo(String priority) {
        WebElement priorityField = chromeDriver.findElement(By.xpath("//div[@id='priority-single-select']/span"));
        priorityField.click();
        WebElement inputPriority = chromeDriver.findElement(By.id("priority-field"));
        inputPriority.sendKeys(priority);
        //screenshot
        File screenshot = ((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE);
        String filename = String.format(".errorPicture/editIssue%d.png", errorCounter);
        try {
            FileUtils.copyFile(screenshot, new File(filename));
            errorCounter++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("the priority of issue displayed as {string}")
    public void thePriorityOfIssueDisplayedAs(String arg0) {
        // wait.until(ExpectedConditions.invisibilityOf(chromeDriver.findElement(By.id("aui-flag-container"))));
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(chromeDriver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
        WebElement anotherDynamicElement = fluentWait.until(driver -> driver.findElement(By.xpath("//*[@id= 'priority-val']/img[@title='Lowest - Trivial problem with little or no impact on progress.']")));
        WebElement priority = chromeDriver.findElement(By.id("priority-val"));
        System.out.println("138 " + priority.getText());
        System.out.println("148    " + chromeDriver.findElement(By.xpath("//*[@id=\"priority-val\"]/img")).getAttribute("title"));
    }

    @After("@aaa")
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            File screenshot = ((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE);
            String filename = String.format(".errorPicture/editIssue%d.png", errorCounter);
            try {
                FileUtils.copyFile(screenshot, new File(filename));
                errorCounter++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //delete issue if exist
        try {
            WebElement element = chromeDriver.findElement(By.xpath("//h1[@id='summary-val' and contains(text(), '" + nameOfNewIssue + "')]"));
            WebElement moreButton = chromeDriver.findElement(By.id("opsbar-operations_more"));
            moreButton.click();
            WebElement deleteIssue = chromeDriver.findElement(By.id("delete-issue"));
            deleteIssue.click();
            WebElement confirmDelete = chromeDriver.findElement(By.id("delete-issue-submit"));
            confirmDelete.click();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Element not found", e);
        }
        chromeDriver.quit();
    }
}
