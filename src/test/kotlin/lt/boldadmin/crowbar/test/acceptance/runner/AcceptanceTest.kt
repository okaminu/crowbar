package lt.boldadmin.crowbar.test.acceptance.runner

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["progress"],
    features = ["src/test/resources/feature"],
    glue = ["lt.boldadmin.crowbar.test.acceptance.step"]
)
class AcceptanceTest
