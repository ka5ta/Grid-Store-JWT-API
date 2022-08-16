Feature: Sign up
  In order to use the site I want to create account

  Scenario: I want to sign up and I don't have an account
    Given Account do not exists in database
    When I sign up with email and password
    Then I should receive 201 status
    And Response message is "Registration was successful".

  Scenario: I want to sign up and I have an account
    Given Account already exists in database
    When I sign up with email and password
    Then I should receive 409 status
    And Response error is "Account already exists.".
