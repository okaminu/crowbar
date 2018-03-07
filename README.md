![Alt text](logo.jpg?raw=true)
# Crowbar 

Crowbar is an extensible library, which helps to authenticate user via preferred provider and method of your choice (email, SMS, etc.).  

* Crowbar AWS SNS plugin for sending confirmation code via SMS is provided as [a separate project](https://github.com/tlistas/Crowbar_AWS_SNS_Plugin).
* Crowbar API is also provided as [a separate project](https://github.com/tlistas/Crowbar_API).

### Workflow
#####  Requesting confirmation code
* User requests confirmation code by providing his mobile number, email or any other address, unique to the user together with the user ID.
* Unique 6 digit Confirmation code is generated and stored together with the user ID in a repository.
* Confirmation code is sent to the user via specified provider.
##### Authenticating the user
* Confirmation code is received
* If confirmation code exists in Confirmation repository, the database entry is removed.
* Unique 36 symbol length Authentication token is generated and stored together with user ID.
* Authentication token is returned.


### Key features
* Written in Kotlin and is compatible with other JVM languages including Java, Scala, Groovy.
* Extensible API, no need to modify current code. 
* Use your own confirmation code provider and repository by implementing Crowbar API


### Download


### Usage 
* Implement *AuthenticationRepository* and *ConfirmationRepository* with you own choice of database.

Example with Spring Data MongoDB:
```
interface AuthenticationMongoRepository : AuthenticationRepository, MongoRepository<Authentication, String> {

    override fun save(authentication: Authentication)

    override fun existsByToken(token: String): Boolean

    override fun findByToken(token: String): Authentication
}
```
* Implement *ConfirmationMessageGateway* from Crowbar with a preferred message delivery provider

Example from [Crowbar AWS SNS plugin](https://github.com/tlistas/Crowbar_AWS_SNS_Plugin):
```
class AwsSmsGatewayAdapter(private val snsClientBuilder: SnsClientBuilder) : ConfirmationMessageGateway {

    override fun send(message: String, mobileNumber: String) {
        try {
            snsClientBuilder.build().publish(PublishRequest()
                    .withMessage(message)
                    .withPhoneNumber(mobileNumber))
        } catch (e: InternalErrorException) {
            throw ConfirmationMessageGatewayException("Api exception ${e.message}")
        } catch (e: InvalidParameterException) {
            throw InvalidAddressException("Address $address is not valid")
        }
    }
```
*  Classes which implement *repositories* and *ConfirmationMessageGateway* are provided during runtime, favorite DI tool should be used. 
* Request confirmation code by providing an address, where confirmation code should be sent and user ID: 
```
confirmationService.sendConfirmation(address, userId)
```
* Authenticate user by providing the confirmation code. It returns a token which identifies a unique user:
```
val token = authenticationService.getAuthenticationToken(confirmationCode)
```
### Exception handling
There are several runtime exceptions defined in Crowbar API:

*ConfirmationMessageGatewayException* – thrown when an error occurs in the message delivery provider.

*InvalidAddressException* – thrown when provided user address cannot be processed by the message delivery provider, e.g., mobile number is in incorrect format. 

*ConfirmationCodeNotFound* – thrown when a confirmation code, provided by the user is not found in the Confirmation repository.

### License
