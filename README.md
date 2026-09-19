# AI-Powered Self-Healing Test Automation Framework

A Selenium/Java test automation framework that automatically recovers from broken
element locators instead of failing outright — simulating how real UI changes
(an ID or class name changing after a release) break traditional automated tests.

## The Problem

Traditional Selenium tests use a single locator per element. When a developer
renames an `id` or restructures the DOM, the test fails immediately — even
though the element is still on the page and easy for a human to find another way.

## The Solution

`SmartElement` accepts a **prioritized list of locator strategies** for each
element. It tries them in order and returns the first one that works, logging
exactly which strategy succeeded:

- If the primary locator works → logs success normally
- If the primary locator fails → automatically falls back to the next strategy,
  logs a warning identifying which locator "healed" the test
- If every strategy fails → throws a clear error naming all attempted locators

This means a test survives minor UI changes and gives you actionable log output
telling you which locators need updating — instead of a hard failure with no
context.

## Project Structure


## Demo

`SelfHealingLoginTest` intentionally uses a **broken** primary locator for the
username field (`By.id("user_name_wrong")`) against a real login page
([the-internet.herokuapp.com/login](https://the-internet.herokuapp.com/login)),
with working fallback locators behind it. Running the test shows:

WARNING: [UsernameField] Primary locator FAILED. Self-healed using fallback #1: By.name: username
INFO: [PasswordField] Primary locator worked: By.id: password
INFO: [LoginButton] Primary locator worked: By.cssSelector: button.radius


...and the test still passes, because the framework recovered automatically.

## Tech Stack

- Java 17
- Selenium WebDriver 4.25
- TestNG
- WebDriverManager (automatic browser driver management)
- Maven

## Running It

```bash
mvn test
```

## Possible Extensions

- Persist healing events to a report (CSV/HTML) for tracking which locators
  need permanent fixes
- Add self-healing based on visual/attribute similarity, not just a fixed
  fallback list
- Integrate with CI (Jenkins/GitHub Actions) to surface healing warnings in
  build reports
