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


### Usage
* Implement *RequestRepository* and *ConfirmationRepository* with your own choice of database.

Example with Spring Data MongoDB:
```
interface ConfirmationMongoRepository : ConfirmationRepository, MongoRepository<Confirmation, String> {

    override fun save(authentication: Confirmation)

    override fun existsByToken(token: String): Boolean

    override fun findByToken(token: String): Confirmation
}
```
* Implement *ConfirmationMessageGateway* from Crowbar API with a preferred message delivery provider

Example:
```
class YourGatewayAdapter() : ConfirmationMessageGateway {

    override fun send(message: String, mobileNumber: String) {
        //implementation
    }
```
*  Classes which implement *repositories* and *ConfirmationMessageGateway* are provided during runtime, favorite DI tool should be used.
* Request confirmation code by providing an address, where confirmation code should be sent and user ID:
```
requestService.sendConfirmation(address, userId)
```
* Authenticate user by providing the confirmation code. It returns a token which identifies a unique user:
```
val token = tokenService.confirmCode(confirmationCode)
```
### Exception handling

*ConfirmationCodeNotFoundException* – runtime exception, thrown when a confirmation code, provided by the user, is not found in the request repository.

### License

This library is licensed under MIT. Full license text is available in [LICENSE](https://github.com/tlistas/Crowbar/blob/TLIST-466-mobile-confirmation/LICENSE.txt).