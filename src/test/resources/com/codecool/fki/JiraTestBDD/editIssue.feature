Feature: Edit issue
  In created issue I can make changes

  Background:
    Given I am logged in on Jira account
    And I am on "TOUCAN" issues page
    And I create a new issue

  #Scenario: I can change the issue type
   # Given I am opening the edit issue tag
    #When I change the type of issue to "Bug"
  #  And save my changes with Update
   # Then the type of issue has changed to "Bug"

  Scenario: I can change the description of issue
    Given I am opening the edit issue tag
    When I type "Changed" to description field
    And save my changes with Update
    Then the description displays "Changed"

  Scenario: I can change the priority of issue
    Given I am opening the edit issue tag
    When I change the priority to "Lowest"
    And save my changes with Update
    Then the priority of issue displayed as "Lowest"