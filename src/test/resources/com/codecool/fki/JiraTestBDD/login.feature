Feature: Login
  I want to login to Jira page

  Background:
    Given I open the Jira home page

  Scenario: login with valid username and password
    Given I have valid username and password
    When I log in  with valid username and password
    Then I am redirected to dashboard page

  Scenario Outline: login attempt with invalid username and password
    When I try to log in with "<username>" and "<password>"
    Then warning message appears

    Examples:
      | username   | password   |
      | micimacko  | malacka    |
      | 123        | abcde      |
      | {username} | {password} |

    Scenario: try to to login with valid username and invalid password 3 times in a row
      Given I have a valid username
      When I try to login with valid username and "1234" password
      And I try to login with valid username and "123" password
      And I try to login with valid username and "12" password
      Then captcha become visible


