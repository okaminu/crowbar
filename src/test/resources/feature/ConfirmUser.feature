Feature: Confirm user identity

  As a user
  I want my identity to be confirmed
  So that I could perform operations on my information

  Scenario: Receive confirmation code for existing user
    Given user exists
    When I provide confirmation address
    Then I receive confirmation code

  Scenario: Confirm existing user identity with correct code
    Given user exists
    When I provide correct confirmation code
    Then user identity is confirmed