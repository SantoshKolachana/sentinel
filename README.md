# Sentinel - Selenium Automation Framework in Java

A Selenium framework that implements the [Page Object Model](http://cheezyworld.com/2010/11/09/ui-tests-not-brittle/) (POM) using [Object Oriented Programming](https://en.wikipedia.org/wiki/Object-oriented_programming) (OOP) concepts. It abstracts out most of the complexity of Selenium to allow users to focus on automating tests and not spend time dealing with the technical details of implementing an automation framework.

## Quickstart
For those wanting to go crazy without reading lots of docs, get the [sentinel.example Project](https://github.com/dougnoel/sentinel.example), and checkout the [Sentinel Javadocs](https://dougnoel.github.io/sentinel/), especially the steps package. Sentinel is intended to be included in your test automation and used as a jar file. If you are writing tests, you should **NOT** be modifying the Sentinel code itself.

People wondering where they can write @Given, @When, and @Then steps should first be asking what has been written and attempting to reuse that instead of writing their own. If you are writing your own more than 1% of the time then you are not using the framework as intended and wasting time (or working on something really weird).

# Section 1: Using Sentinel

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

## 1.0 Getting Started

### 1.1 Prerequisites

What things you need to install the software and how to install them:
 * Integrated Development Environment (Eclipse Suggested)
 * Java 8 (1.8) (Java 9 and later are NOT supported by Cucumber)
 * Maven (2.5.4 or later)

### 1.2 Installation
These instructions assume you are using Eclipse.

**On the command line:**
Clone the project from git.

```
git clone https://github.com/dougnoel/sentinel.git
```
1. Open Eclipse
2. File -> Open Projects from File system...
3. Next to Import Source click the Directory button and browse to the installed location.
4. Click the Finish button.
1. Right-Click on the project in the Project Explorer.
2. Maven -> Update Project...
3. Wait for the status bar in the lower right-hand corner to finish before continuing.

## 2.0 - 4.0
These sections have been moved to the [sentinel.example Project](https://github.com/dougnoel/sentinel.example). Please refer to that Readme for how to create and execute tests.

## 5.0 Deployment

Add additional notes about how to deploy this on a live system in Bamboo/Jenkins/etc.

### 5.1 Building a Jar File

To build a file for release, open up the pom.xml and change the version number using [Semantic Versioning](http://semver.org/) to the next release number. Alternately an X.X.X-SNAPSHOT version can be created.

Then just run:

```
mvn package
```
The jar file will be located in the target directory.

To install it locally:

```
mvn install:install-file -Dfile=sentinel-1.0.0-SNAPSHOT.jar -DgroupId=com.dougnoel -DartifactId=sentinel -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
```

If you want to do this all in one step without generating a jar, just run:

```
mvn install
```

### 5.2 Deploy to Github
Note this will only work if you have Admin rights to the Github repo.

#### 5.2.1 Generate a GitHub Personal Access Token
Note: You must have Maven installed and have an account on [github.com](https://github.com) Instructions modified from: [Create a GitHub personal access token](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line).

1. [Verify your email address](https://help.github.com/en/articles/verifying-your-email-address), if it hasn't been verified yet.
2. In the upper-right corner of any page, click your profile photo, then click **Settings**.
3. In the left sidebar, click **Developer settings**.
4. In the left sidebar, click **Personal access tokens**.
5. Click **Generate new token**.
6. Name the token **Package Read/Write**
7. Select the **write:packages** scope. This will auto-select the **read:packages** and **repo** scopes you need as well.
8. Click **Generate token**.
9. Click  to copy the token to your clipboard. For security reasons, after you navigate off the page, you will not be able to see the token again.

#### 5.2.2 Maven settings.xml file
1. Copy the settings-example.xml file to ~/.m2/settings.xml
2. Replace the USERNAME and GITHUB key with your Github username and the key generated in the previous step.

#### 5.2.3 Deploy the Package to Github
Once you're set up, all you have to do is run this command:

```
mvn deploy
```

## 6.0 Additional Documentation & Resources

### 6.1 Javadocs
Sentinel comes with Javadocs which describe its classes in great detail. This includes examples of how you can use the generic Cucumber steps that are already included. The [Sentinel Javadocs](https://dougnoel.github.io/sentinel/) are updated on github with every new version.

#### 6.1.1 Generating Javadocs
The Javadocs can also be easily generated by running the following command.

```
mvn javadoc:javadoc
```
The files are generated in the project root folder under the default path of target/site/apidocs and can be accessed by opening up the index.html file contained within that folder.

Every method should have a Javadoc comment describing what it does, its parameters, what it returns (if not void), and any exceptions it throws. We follow the [Liferay-Portal Javadoc Guidelines](https://github.com/liferay/liferay-portal/blob/master/readme/ADVANCED_JAVADOC_GUIDELINES.markdown) for writing Javadoc contents.

#### 6.1.2 Publishing Javadocs to Github
1. Make sure you have updated the version in the pomfile if necessary (pom.xml)
2. Copy the files from target/site/apidocs to docs/
3. Commit the changes and merge them with master. The files will be pulled from the docs folder and published on [https://dougnoel.github.io/sentinel/](https://dougnoel.github.io/sentinel/)

For more information, refer to [Configuring a publishing source for your GitHub Pages site](https://help.github.com/en/github/working-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site)
Additional information can be found under [About GitHub Pages and Jekyll](https://help.github.com/en/github/working-with-github-pages/about-github-pages-and-jekyll) and the [GitHub Pages site](https://pages.github.com/).

### 6.2 Built With

* [Cucumber](https://cucumber.io/) - BDD Testing Framework
* [Cucumber Extent Reporter](http://www.vimalselvam.com/cucumber-extent-reporter/) Interface between Cucumber Results and Extent Reports.
* [Commons Lang](https://commons.apache.org/proper/commons-lang/) - Apache Commons Lang 3 for common Java language options
* [Extent Reports](http://extentreports.com/) - A report framework for automation testing.
* [HttpComponents](http://hc.apache.org/) - Apache HttpComponents for API testing.
* [Jackson](https://github.com/FasterXML/jackson) - Libraries for reading YAML, JSON and other config files.
* [Log4j2](https://logging.apache.org/log4j/2.x/) - Apache logging framework.
* [PDFBox](https://pdfbox.apache.org/) - Apache library for reading PDF files.
* [Maven](https://maven.apache.org/) - Apache dependency management.
* [Maven Javadoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/) - Plugin to allow maven to generate Javadocs.
* [Saucelabs](https://saucelabs.com/) - Multi-Platform, multi-browser external testing service.
* [Selenium](https://www.seleniumhq.org/) - The automation framework workhorse.
* [Swagger Parser](https://github.com/swagger-api/swagger-parser) - Reads Swagger API files, allowing us to use them as API Objects.
* [Traprange](https://github.com/thoqbk/traprange) - Independent library developed using PDFBox to deal with tables in PDFs.
* [Unirest](http://unirest.io/java.html) - A simple API library used for the API testing functionality.

### 6.3 Web Drivers

The web drivers used by Sentinel are stored in drivers/os to make sure there is only one place to fix driver compatibility issues. Chrome auto updates, and so is the one that will go of date most often. While we could pull the driver from a path and let each implementation install the drivers, this can become problematic in CI/CD environments where we do not control the system. This also reduces the learning curve for using Sentinel.

* [Chromedriver](http://chromedriver.chromium.org/) 2.42 - Driver for automating Google Chrome.
* [Geckodriver](https://github.com/mozilla/geckodriver/releases) - Driver for automating Mozilla Firefox.
* [Safari](https://webkit.org/blog/6900/webdriver-support-in-safari-10/) - Safari driver is embedded in Safari.
* [IE Driver](http://selenium-release.storage.googleapis.com/index.html) - Driver for automating IE.

### 6.4 Saucelabs
Sentinel is setup to use [Saucelabs](https://saucelabs.com/) for remote execution. This is the recommended way to execute test in your build pipeline, because you then do not need to setup an execution server.

## 7.0 Versioning

We use [Semantic Versioning](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/dougnoel/sentinel/tags). 

## 8.0 Authors

* **Doug Noël** - *Architect* - Initial work.

## 9.0 License

This project is licensed under the Apache Commons 2.0 License - see the [LICENSE.md](LICENSE.md) file for details

## 10.0 Acknowledgments

* Some design choices inspired by [Cheezy's Page Object gem](https://github.com/cheezy/page-object).