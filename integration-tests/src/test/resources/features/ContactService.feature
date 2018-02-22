Feature: Manage Contacts

  Scenario Outline: Add a Contact
    When I create a contact named <first_name> <last_name>
    Then I should be able to find the contact by id named <first_name> <last_name>

    Examples:
      | first_name | last_name |
      | John       | Doe       |
      | Jane       | Smith     |

  Scenario Outline: Delete a Contact
    Given a contact already created named <first_name> <last_name>
    When I delete the contact
    Then I should not be able to find the contact by id
    And I should not be able to find the contact named <first_name> <last_name>

    Examples:
      | first_name | last_name |
      | Peter      | Piper     |