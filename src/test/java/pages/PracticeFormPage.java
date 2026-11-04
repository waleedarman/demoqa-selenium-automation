package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class PracticeFormPage extends BasePage {

    public PracticeFormPage(WebDriver driver) {
        super(driver);
    }

    public void enterFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName"))).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        driver.findElement(By.id("lastName")).sendKeys(lastName);
    }

    public void enterEmail(String email) {
        driver.findElement(By.id("userEmail")).sendKeys(email);
    }

    public void selectGender(String gender) {
        if (gender.equalsIgnoreCase("Male")) {
            driver.findElement(By.cssSelector("label[for='gender-radio-1']")).click();
        } else if (gender.equalsIgnoreCase("Female")) {
            driver.findElement(By.cssSelector("label[for='gender-radio-2']")).click();
        } else if (gender.equalsIgnoreCase("Other")) {
            driver.findElement(By.cssSelector("label[for='gender-radio-3']")).click();
        }
    }

    public void enterMobile(String mobile) {
        driver.findElement(By.id("userNumber")).sendKeys(mobile);
    }

    public void selectDateOfBirth(String dateOfBirth) {
        String[] date = dateOfBirth.split(" ");
        String day = String.valueOf(Integer.parseInt(date[0]));
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        driver.findElement(By.id("dateOfBirthInput")).click();
        WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".react-datepicker__month-select")));
        Select month = new Select(monthDropdown);
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(date[1])) {
                month.selectByIndex(i);
                break;
            }
        }
        WebElement yearDropdown = driver.findElement(By.cssSelector(".react-datepicker__year-select"));
        Select year = new Select(yearDropdown);
        year.selectByVisibleText(date[2]);
        driver.findElement(By.xpath("//div[contains(@class,'react-datepicker__day--')"
                + " and not(contains(@class,'outside-month')) and text()='" + day + "']")).click();
    }

    public void enterSubject(String subject) {
        WebElement input = driver.findElement(By.id("subjectsInput"));
        input.sendKeys(subject);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".subjects-auto-complete__option")));
        input.sendKeys(Keys.ENTER);
    }

    public void selectHobby(String hobby) {
        if (hobby.equalsIgnoreCase("Sports")) {
            driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']")).click();
        } else if (hobby.equalsIgnoreCase("Reading")) {
            driver.findElement(By.cssSelector("label[for='hobbies-checkbox-2']")).click();
        } else if (hobby.equalsIgnoreCase("Music")) {
            driver.findElement(By.cssSelector("label[for='hobbies-checkbox-3']")).click();
        }
    }

    public void uploadPicture(String picture) {
        String path = System.getProperty("user.dir") + "/src/test/resources/testdata/" + picture;
        driver.findElement(By.id("uploadPicture")).sendKeys(path);
    }

    public void enterAddress(String address) {
        driver.findElement(By.id("currentAddress")).sendKeys(address);
    }

    public void selectState(String state) {
        WebElement input = driver.findElement(By.id("react-select-3-input"));
        input.sendKeys(state);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#state [id*='-option-']")));
        input.sendKeys(Keys.ENTER);
    }

    public void selectCity(String city) {
        WebElement input = driver.findElement(By.id("react-select-4-input"));
        input.sendKeys(city);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#city [id*='-option-']")));
        input.sendKeys(Keys.ENTER);
    }

    public void clickSubmit() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
        try {
            button.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", button);
        }
    }

    public String getConfirmationTitle() {
        WebElement popupTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("example-modal-sizes-title-lg")));
        return popupTitle.getText();
    }

    public String getSubmittedValue(String label) {
        By valueLocator = By.xpath("//tr[td[1]='" + label + "']/td[2]");
        WebElement submittedValue = wait.until(ExpectedConditions.visibilityOfElementLocated(valueLocator));
        return submittedValue.getText();
    }

    public boolean isFieldInvalid(String fieldId) {
        return !driver.findElements(By.cssSelector("#" + fieldId + ":invalid")).isEmpty();
    }

    public int getInvalidRequiredFieldCount() {
        return driver.findElements(By.cssSelector(
                "#firstName:invalid, #lastName:invalid, input[name='gender']:invalid, #userNumber:invalid")).size();
    }

    public boolean isConfirmationDisplayed() {
        return !driver.findElements(By.id("example-modal-sizes-title-lg")).isEmpty();
    }
}
