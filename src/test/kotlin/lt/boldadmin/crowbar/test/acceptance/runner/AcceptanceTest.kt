package lt.boldadmin.crowbar.test.acceptance.runner

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    format = arrayOf("progress"),
    features = arrayOf("src/test/resources/feature"),
    glue = arrayOf("lt.boldadmin.crowbar.test.acceptance.step")
)
class AcceptanceTest
