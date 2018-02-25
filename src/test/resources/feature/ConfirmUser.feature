Feature: Confirm User

As a User
I want to authenticate my identity
So that I could start logging my work time

Scenario: Receive confirmation code for existing User
  Given user exists
  When I provide confirmation address
  Then I receive confirmation code

Scenario: Confirm existing User with correct code
  Given user exists
  When I provide correct confirmation code
  Then user is confirmed