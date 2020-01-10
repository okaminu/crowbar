![Alt text](logo.jpg?raw=true)
# Crowbar 

Crowbar is an extensible library, which helps to authenticate user via preferred provider and method of your choice (email, SMS, etc.).  


### Workflow
#####  Requesting confirmation code
* User requests confirmation code by providing his mobile number, email or any other address, unique to the user together with the user ID.
* Unique 6 digit Confirmation code is generated and stored together with the user ID in a repository.
* Confirmation code is sent to the user via specified provider.
##### Confirming the user
* Confirmation code is received
* If confirmation code exists in Confirmation repository, the database entry is removed.
* Unique 36 symbol length Authentication token is generated and stored together with user ID.
* Authentication token is returned.


### Key features
* Written in Kotlin and is compatible with other JVM languages including Java, Scala, Groovy.
* Extensible API, no need to modify current code.
* Use your own confirmation code provider and repository by implementing Crowbar API


### Download
* Gradle
```
repositories {
    repositories {
        maven {
            url 'http://repository.boldadmin.com.s3.amazonaws.com/releases/'
        }
    }
}

dependencies {
    compile "lt.boldadmin.crowbar:crowbar:2.7.1"
}
```
* Maven
```
<repositories>
    <repository>
        <id>boldadmin-release-repo</id>
        <name>BoldAdmin Release Repository</name>
        <url>http://repository.boldadmin.com.s3.amazonaws.com/releases/</url>
    </repository>
</repositories>


<dependencies>
    <dependency>
        <groupId>lt.boldadmin.crowbar</groupId>
        <artifactId>crowbar</artifactId>
        <version>2.7.1</version>
    </dependency>
</dependencies>
```

### Usage
* Implement *UserTokenRepository* and *UserConfirmationCodeRepository* with your own choice of database.

Example with Spring Data MongoDB:
```
interface UserConfirmationCodeMongoRepository
    : UserConfirmationCodeRepository, MongoRepository<UserConfirmationCode, String> {

    override fun save(code: UserConfirmationCode)

    override fun deleteByCode(id: String)

    override fun existsByCode(code: String): Boolean

    override fun findByCode(code: String): UserConfirmationCode
}
```
* Implement *ConfirmationMessageGateway* from Crowbar API with a preferred message delivery provider. [Plugin 
example](https://github.com/boldadmin-com/Crowbar_AWS_SNS_Plugin)

Example:
```
class YourGatewayAdapter() : ConfirmationMessageGateway {

    override fun send(message: String, address: String) {
        //implementation
    }
```
*  Classes which implement *repositories* and *ConfirmationMessageGateway* are provided during runtime, favorite DI tool should be used.
* Request confirmation code by providing an address, where confirmation code should be sent and user ID:
```
identityConfirmation.sendConfirmationCode(userId, address)
```
* Confirm user by providing the correct confirmation code. It returns a token which identifies a unique user:
```
identityConfirmation.confirmCode(userConfirmationCode)
```
* Retrieve authentication token by providing user`s ID
```
val token = identityConfirmation.getTokenById(userId: String)
```
### License

This library is licensed under MIT. Full license text is available in [LICENSE](https://github.com/boldadmin-com/Crowbar/blob/dev/LICENSE.txt).