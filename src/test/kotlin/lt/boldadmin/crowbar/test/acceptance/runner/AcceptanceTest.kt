package lt.boldadmin.crowbar.test.acceptance.runner

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["progress"],
    features = ["src/test/resources/feature"],
    glue = ["lt.boldadmin.crowbar.test.acceptance.step"]
)
class AcceptanceTest
