# Android Maven Publisher Template

The Android Maven Publisher Template is a project template designed to assist Android developers in publishing their libraries to Maven repositories. It simplifies the configuration of Gradle projects for publishing Android libraries, making it easier for developers to share their work with the community.

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Core](https://img.shields.io/maven-central/v/com.ferhatozcelik/example.svg)](https://search.maven.org/artifact/com.ferhatozcelik/example)

This is a template project demonstrating the use of the android-maven-publisher plugin to publish Android libraries to Maven repositories.

## GPG Key Create Linux

Install GPG:
First, you need to install the GPG package on your Linux distribution. You can typically do this using your package manager. For Debian/Ubuntu-based distributions, you can use:

```bash
sudo apt-get install gnupg

For Red Hat/CentOS-based distributions, you can use:

sudo yum install gnupg

```
Generate a GPG Key:
```bash

Open a terminal and run the following command to generate a GPG key:
```
```bash

gpg --full-generate-key

```
This command will guide you through a series of questions. In most cases, selecting the default options is suitable.

Export the Key:
To export the key, use the following command:

```bash

gpg --armor --export GPG_ID > mykey.asc

```

Replace GPG_ID with the ID of the key you generated, and mykey.asc with the desired filename for the exported key.

Retrieve the GPG ID:
To retrieve the GPG ID of your key, open a terminal and run the following command:

```bash

gpg --list-secret-keys --keyid-format LONG

```
This command will list the secret keys on your system along with their GPG IDs. The ID is the long string next to the "sec" line.

## GPG Key Create Windows

Download and Install Kleopatra:
First, download and install Kleopatra from the following link: Kleopatra Download.

Launch Kleopatra:
After installing Kleopatra, launch the application.

Generate a New Key Pair:

Click on the "File" menu.
Select "New Certificate" or "New Key Pair" option.
Choose "Create a personal OpenPGP key pair" and click "Next".
Enter your name, email address, and an optional comment.
Click "Next" to proceed.
Set Key Parameters:

Choose the key type and key length. For most purposes, the default options are sufficient.
Set the expiration date for the key, or leave it blank for no expiration.
Click "Next" to continue.
Review and Confirm:
Review the details you've entered and click "Create key" to generate the key pair.

Export the Key:

Once the key pair is generated, find it in the Kleopatra interface.
Right-click on the key pair and select "Export Secret Keys".
Choose a location to save the exported key file and click "Save".


```bash

gpg --armor --export GPG_ID > mykey.asc

```

## Kotlin DSL Publish Module
```
module_name-publish/
│
├── .gitignore
├── build.gradle.kts     // Gradle build script for the module
└── src/
    └── main/
        └── kotlin/        // Source code directory for the module
             └── library-publish.gradle.kts // Gradle script for configuring library publishing
```

In this structure:

- .gitignore: This file specifies patterns of files and directories that should be ignored by version control systems like Git. It helps in keeping your repository clean by excluding unnecessary files such as build artifacts and IDE configurations.

- build.gradle.kts: This is the Gradle build script for the module. It defines dependencies, tasks, and configurations related to building and packaging the module. You would typically configure the publishing settings within this file, such as specifying the Maven repository URL, group ID, artifact ID, version, etc.

- src/main/kotlin/: This directory is the source code directory for your module. You would place your Kotlin source files here.

- src/main/kotlin/library-publish.gradle.kts: This file seems to be intended for configuring library publishing settings specific to your module. While it's unconventional to place a Gradle script directly inside the src/ directory, it's possible that you've organized it this way for your project's specific structure. This script might contain configurations related to publishing the module to a Maven repository or other publishing-related tasks.

- Overall, this structure seems suitable for managing a Gradle-based module in your project. Make sure to populate the src/main/kotlin/ directory with your Kotlin source files and configure the build.gradle.kts script with the necessary dependencies and build settings. Additionally, ensure that the .gitignore file includes patterns to ignore build artifacts and any other files that shouldn't be committed to version control.

#### settings.gradle.kts

```kotlin 

includeBuild("module_name-publish")

```

#### build.gradle.kts

```kotlin 

plugins {
    kotlin("jvm") version "1.9.0"
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
}

```

#### module_name-publish.gradle.kts

```kotlin 

import java.util.Properties

plugins {
    `maven-publish`
    signing
}

// Snapshot versioning is enabled by default
val isSnapshot = false
val snapshotIdentifier = "-SNAPSHOT"

// Load maven properties
val mavenPropertiesFile = File(rootProject.projectDir, "maven.properties")
val mavenProperties = Properties()
if (mavenPropertiesFile.exists()) {
    mavenProperties.apply {
        load(mavenPropertiesFile.inputStream())
    }
    System.getProperties().putAll(mavenProperties)
}

// Load version properties
val versionPropertiesFile = File(rootProject.projectDir, "version.properties")
val versionProperties = Properties()
if (versionPropertiesFile.exists()) {
    versionProperties.apply {
        load(versionPropertiesFile.inputStream())
    }
    System.getProperties().putAll(versionProperties)
}

// Load secret properties
val secretPropertiesFile = File(rootProject.projectDir, "secret.properties")
val secretProperties = Properties()
if (secretPropertiesFile.exists()) {
    secretProperties.apply {
        load(secretPropertiesFile.inputStream())
    }
    System.getProperties().putAll(secretProperties)
}

// Define properties
val pomName = "Library Example"
val pomDescription = "Maven plugin for publishing Android libraries"
val libVersionName = versionProperties.getProperty("example") as String + if (isSnapshot) snapshotIdentifier else ""
val artifactName = "example"

// Maven Central properties
val mavenCentralUrl = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
val mavenSnapshotUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

val group = mavenProperties.getProperty("GROUP") as String

val projectUrl = mavenProperties.getProperty("POM_URL") as String

val licenseName = mavenProperties.getProperty("LICENCE_NAME") as String
val licenseUrl = mavenProperties.getProperty("LICENCE_URL") as String

val developerId = mavenProperties.getProperty("DEVELOPER_ID") as String
val developerName = mavenProperties.getProperty("DEVELOPER_NAME") as String

val scmConnection = mavenProperties.getProperty("SCM_CONNECTION") as String
val scmDevConnection = mavenProperties.getProperty("SCM_DEV_CONNECTION") as String

// Load the repository credentials from the secret properties
val repositoryUsername = System.getenv("MAVEN_CENTRAL_USERNAME") as String
val repositoryPassword = System.getenv("MAVEN_CENTRAL_PASSWORD") as String

// Configure the publishing tasks
publishing {
    // Configure the publications
    publications {
        // Register a new publication named "release" of type MavenPublication
        register<MavenPublication>("release") {
            // Set the group ID, artifact ID, and version of the library
            groupId = group
            artifactId = artifactName
            version = libVersionName

            // Specify that the release build variant of the library should be published
            afterEvaluate {
                from(components["release"])
            }

            // Configure the POM (Project Object Model) file
            pom {
                // Set the project's name, description, and URL
                name.set(pomName)
                description.set(pomDescription)
                url.set(projectUrl)

                // Set the project's license information
                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }

                // Set the project's developer information
                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerName)
                    }
                }

                // Set the project's source control management information
                scm {
                    connection.set(scmConnection)
                    developerConnection.set(scmDevConnection)
                    url.set(projectUrl)
                }
            }
        }

        // Configure the repositories where the library will be published
        repositories {
            // Configure the Maven repository
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
                // Set the credentials for the Maven repository
                credentials {
                    username = repositoryUsername
                    password = repositoryPassword
                }

                // Set the URL of the Maven repository
                url = when {
                    // If the library version ends with "-SNAPSHOT", publish to the snapshot repository
                    libVersionName.endsWith(snapshotIdentifier) -> {
                        mavenSnapshotUrl
                    }
                    // Otherwise, publish to the release repository
                    else -> {
                        mavenCentralUrl
                    }
                }
            }
        }
    }
}

// Configure the signing tasks
signing {
    // Sign the "release" publication
    sign(publishing.publications["release"].name)
}


```


#### maven.properties
```bash

# Maven repository group id
GROUP=com.ferhatozcelik

# Maven repository pom url
POM_URL=https://github.com/ferhatozcelik/ferhatozcelik-core

# Maven repository scm connection
SCM_CONNECTION=scm:git@github.com:ferhatozcelik/ferhatozcelik-core.git

# Maven repository scm developer connection
SCM_DEV_CONNECTION=scm:git@github.com:ferhatozcelik/ferhatozcelik-core.git

# License information
LICENCE_NAME=The Apache Software License, Version 2.0
LICENCE_URL=https://www.apache.org/licenses/LICENSE-2.0.txt

# Developer information
DEVELOPER_ID=ferhatozcelik
DEVELOPER_NAME=Ferhat OZCELIK

```

#### version.properties

```bash

example=1.0.0

```


## GITHUB Actions publish

main.yml
```yml

name: Publish to Maven Central

# create a new release on GitHub to trigger this workflow
on:
  release:
    types: [ created ]

jobs:
  build:
    runs-on: ubuntu-latest
    env:
      MAVEN_CENTRAL_USERNAME: ${{ secrets.MAVEN_CENTRAL_USERNAME }} # Maven Central username
      MAVEN_CENTRAL_PASSWORD: ${{ secrets.MAVEN_CENTRAL_PASSWORD }} # Maven Central password
      GPG_PRIVATE_KEY: ${{ secrets.GPG_PRIVATE_KEY }} # GPG private key  -----BEGIN PGP PRIVATE KEY BLOCK----- XXXXXXX  -----END PGP PRIVATE KEY BLOCK-----
      GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }} # GPG passphrase

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      # Get the release title from the GitHub release
      - name: Get release title
        id: release_title
        run: echo "::set-output name=title::${{ github.event.release.name }}"

      - name: Version properties file update
        uses: dschanoeh/change-property@v1
        with:
          file: version.properties
          property: example
          value: ${{ steps.release_title.outputs.title }}

      - name: Version properties file read
        run: |
          echo "Version: $(cat version.properties)"
      

      # Java 11 is required to build the project
      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          gpg-private-key: ${{ secrets.GPG_PRIVATE_KEY }}
          gpg-passphrase: ${{ secrets.GPG_PASSPHRASE }}

      - name: Build with Gradle
        run: gradle build

      # Publish the project to Maven Central
      - name: Publish to GitHub Packages
        run: gradle publish
        env:
          USERNAME: ${{ secrets.MAVEN_CENTRAL_USERNAME }}
          PASSWORD: ${{ secrets.MAVEN_CENTRAL_PASSWORD }}

```
### Environment Variables

Before building or publishing your library, make sure to set the following environment variables:

- `GPG_PASSPHRASE`: The passphrase for your GPG private key.
- `GPG_PRIVATE_KEY`: The GPG private key used for signing artifacts.
- `MAVEN_CENTRAL_USERNAME`: Your Maven Central username for publishing artifacts.
- `MAVEN_CENTRAL_PASSWORD`: Your Maven Central password for publishing artifacts.

Ensure that these environment variables are securely stored and accessed only by authorized users or automated processes. Avoid exposing sensitive information in your repository or public documentation.


## Example Maven Repository
- https://mvnrepository.com/artifact/com.ferhatozcelik/iot
- https://mvnrepository.com/artifact/com.ferhatozcelik/example

## Author
👤 Ferhat OZCELIK

Github: @ferhatozcelik
LinkedIn:https://www.linkedin.com/in/ferhatozcelik/
Show your support
Give a ⭐️ if this project helped you!
