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
import java.util.function.Function;


public class EditIssueStepDefinitions {

    private final WebDriver chromeDriver = new ChromeDriver();
    private final WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(25));
    private static final String homePage = "https://jira-auto.codecool.metastage.net/login.jsp";
    private int errorCounter = 0;
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("src/test/java/assets")
            .filename(".env")
            .load();

    private static final String PASSWORD1 = dotenv.get("PASSWORD");
   private static final String USERNAME1 = dotenv.get("USER_NAME");

  private static final String nameOfNewIssue="fki-BDD-testIssue";

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
        Wait<WebDriver> wait = new FluentWait<WebDriver>(chromeDriver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);
        WebElement createIssueBtn_inNavBar=chromeDriver.findElement(By.id("create_link"));
        createIssueBtn_inNavBar.click();
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(chromeDriver.findElement(By.id("summary"))));
        WebElement summaryInput_OfIssue_inPopUp= chromeDriver.findElement(By.id("summary"));
       summaryInput_OfIssue_inPopUp.sendKeys(nameOfNewIssue);
        WebElement createIssueBtn_inPopUp=chromeDriver.findElement(By.id("create-issue-submit"));
        createIssueBtn_inPopUp.click();
        wait.until(ExpectedConditions.alertIsPresent());
        //nem történik meg a refresh!!!!
        chromeDriver.navigate().refresh();
//itt nem lássa az alert miatt, de hiába nyomom ki manuálisan akkor se megy tovább...
        //a refresh nem történik meg
        wait.until(ExpectedConditions.textToBePresentInElement(chromeDriver.findElement(By.id("main")),nameOfNewIssue ));
       // WebElement main=chromeDriver.findElement(By.id("main"));
        System.out.println("74");
        String xpathExpression = String.format("//ol[@class='issue-list']/li/a[contains(text(), '%s')]", nameOfNewIssue);
        WebElement newIssue=chromeDriver.findElement(By.xpath(xpathExpression));
        newIssue.click();

    }

    @Given("I am opening the edit issue tag")
    public void iAmOpeningTheEditIssueTag() {
        WebElement title_OfSelectedIssue=chromeDriver.findElement(By.id("summary-val"));
      wait.until(ExpectedConditions.textToBePresentInElement(title_OfSelectedIssue, nameOfNewIssue));
        WebElement editIssueBtn=chromeDriver.findElement(By.id("edit-issue"));
        editIssueBtn.click();
        WebElement descriptionInput_inPopup=chromeDriver.findElement(By.id("mce_0_ifr"));
        wait.until(ExpectedConditions.visibilityOf(descriptionInput_inPopup));
    }

    @When("I change the type of issue to {string}")
    public void iChangeTheTypeOfIssueTo(String arg0) {
        System.out.println("83 "+arg0);
    }

    @And("save my changes with Update")
    public void saveMyChangesWithUpdate() {
        WebElement updateBtn_inPopup=chromeDriver.findElement(By.id("edit-issue-submit"));
        updateBtn_inPopup.click();
    }

    @Then("the type of issue has changed to {string}")
    public void theTypeOfIssueHasChangedTo(String arg0) {
        System.out.println("93 "+arg0);
    }

    @When("I type {string} to description field")
    public void iTypeToDescriptionField(String descText) {
        WebElement descriptionInput_inPopup=chromeDriver.findElement(By.id("mce_0_ifr"));
            descriptionInput_inPopup.sendKeys(descText);
    }

    @Then("the description displays {string}")
    public void theDescriptionDisplays(String descText) {
        WebElement descriptionOfIssue=chromeDriver.findElement(By.id("description-val"));
        wait.until(ExpectedConditions.textToBePresentInElement(descriptionOfIssue, descText));
        System.out.println(descriptionOfIssue.getText());
    }

    @When("I change the priority to {string}")
    public void iChangeThePriorityTo(String arg0) {
        System.out.println("priority " + arg0);
    }

    @Then("the priority of issue displayed as {string}")
    public void thePriorityOfIssueDisplayedAs(String arg0) {
        System.out.println("priority then " + arg0);
    }

    @After
    public void tearDown(Scenario scenario){
        if (scenario.isFailed()){
            File screenshot = ((TakesScreenshot) chromeDriver).getScreenshotAs(OutputType.FILE);
            String filename= String.format(".errorPicture/editIssue%d.png", errorCounter);
            try{
                FileUtils.copyFile(screenshot, new File(filename));
                errorCounter++;
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }
        //delete issue if exist
        // create Exception for no such of issue
        chromeDriver.quit();
    }
}
