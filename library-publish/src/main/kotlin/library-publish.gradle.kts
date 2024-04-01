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
val libVersionName =
    versionProperties.getProperty("library") as String + if (isSnapshot) snapshotIdentifier else ""
val artifactName = "library"

// Maven Central properties
val mavenCentralUrl = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
val mavenSnapshotUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

val group = mavenProperties.getProperty("GROUP") as String
println(group)

val projectUrl = mavenProperties.getProperty("POM_URL") as String

val licenseName = mavenProperties.getProperty("LICENCE_NAME") as String
val licenseUrl = mavenProperties.getProperty("LICENCE_URL") as String

val developerId = mavenProperties.getProperty("DEVELOPER_ID") as String
val developerName = mavenProperties.getProperty("DEVELOPER_NAME") as String

val scmConnection = mavenProperties.getProperty("SCM_CONNECTION") as String
val scmDevConnection = mavenProperties.getProperty("SCM_DEV_CONNECTION") as String

// Load the repository credentials from the secret properties
val repositoryUsername = secretProperties.getProperty("mavenCentralUsername") ?: System.getenv("USERNAME") as String
val repositoryPassword = secretProperties.getProperty("mavenCentralPassword") ?: System.getenv("TOKEN") as String

val singingKey = secretProperties.getProperty("signing.keyId")
val singingSecretKeyRingFile = secretProperties.getProperty("signing.secretKeyRingFile")
val singingPassword = secretProperties.getProperty("signing.password")


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
    // Use in-memory PGP keys for signing
    // The keys are retrieved from the secret properties file
    if (singingKey != null && singingSecretKeyRingFile != null && singingPassword != null) {
        useInMemoryPgpKeys(
            singingKey, // The key ID
            singingSecretKeyRingFile, // The path to the secret key ring file
            singingPassword // The password for the key
        )
    }
    // Sign the "release" publication
    sign(publishing.publications["release"].name)
}
