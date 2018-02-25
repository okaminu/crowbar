package lt.tlistas.mobile.number.confirmation.test.acceptance.runner

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        features = arrayOf("src/test/resources/feature"),
        glue = arrayOf("lt.tlistas.test.acceptance.step"))
class AcceptanceTest
