package base;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demoqa.com/automation-practice-form");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null) {
            try {
                if (result.getStatus() == ITestResult.FAILURE) {
                    try {
                        Files.createDirectories(Paths.get("screenshots"));
                        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                        String filename = result.getMethod().getMethodName() + "_" + System.currentTimeMillis() + ".png";
                        Files.write(Paths.get("screenshots", filename), screenshot);
                    } catch (Exception e) {
                        System.err.println("Warning: could not save failure screenshot: " + e.getMessage());
                    }
                }
            } finally {
                try {
                    driver.quit();
                } catch (Exception e) {
                    System.err.println("Warning: could not close WebDriver: " + e.getMessage());
                }
            }
        }
    }
}
