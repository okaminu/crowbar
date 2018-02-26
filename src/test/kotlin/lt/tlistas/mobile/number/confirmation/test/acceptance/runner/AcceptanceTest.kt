package lt.tlistas.mobile.number.confirmation.test.acceptance.runner

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        format = arrayOf("progress"),
        features = arrayOf("src/test/resources/feature"),
        glue = arrayOf("lt.tlistas.mobile.number.confirmation.test.acceptance.step"))
class AcceptanceTest
