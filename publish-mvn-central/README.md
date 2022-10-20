## Publish to Maven Central

In order to publish artifacts to Maven Central make sure that artifacts contains the following files in https://repo.enonic.com

- `$sourceArtifactId-${sourceVersion}.pom`
- `$sourceArtifactId-${sourceVersion}.jar`
- `$sourceArtifactId-${sourceVersion}-javadoc.jar`
- `$sourceArtifactId-${sourceVersion}-sources.jar`
- `$sourceArtifactId-${sourceVersion}.module`

Where properties must be declared in the `gradle.properties` file or provided as arguments

```properties
sourceGroupId=
sourceArtifactId=
sourceVersion=
nexusUsername=
nexusUserPassword=
signing.keyId=<The last 8 symbols of the keyId>
signing.password=<The passphrase used to protect your private key>
signing.secretKeyRingFile=<The absolute path to the secret key ring file containing your private key> (gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg)
```

Step 1: Download artifacts from https://repo.enonic.com/public
```
 ./gradlew clean downloadArtifacts -P sourceGroupId="<groupdId>" -P sourceArtifactId="<artifactId>" -P sourceVersion="<version>"
```

Step 2: Publish to Nexus staging repository

```
./gradlew publish -P sourceGroupId="<groupdId>" -P sourceArtifactId="<artifactId>" -P sourceVersion="<version>" -P signing.keyId="<last 8 characters of publicKey>" -P signing.password="<pwd>" -P signing.secretKeyRingFile="<path to secring.gpg>" -P nexusUsername="<username>" -P nexusUserPassword="<nexus user password>"
```

Step 3: Close and release to Maven Central

```
./gradlew closeAndReleaseNexusStagingRepository -P nexusUsername="<username>" -P nexusUserPassword="<nexus user password>"
```


