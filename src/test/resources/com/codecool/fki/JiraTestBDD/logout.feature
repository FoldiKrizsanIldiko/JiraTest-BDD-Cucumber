Feature: Log Out
  I want to log out from my Jira account

  Scenario: Logout from current account
    Given I am logged in on Jira page
    When I press LogOut button
    Then I am redirected to new login page