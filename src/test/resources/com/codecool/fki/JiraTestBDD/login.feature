Feature: Login
  I want to login to Jira page

  Background:
    Given I open the Jira home page

  Scenario: login with valid username and password
    Given I have valid username and password
    When I log in  with valid username and password
    Then I am redirected to dashboard page

