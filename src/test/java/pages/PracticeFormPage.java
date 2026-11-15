package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PracticeFormPage extends BasePage {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);

    public PracticeFormPage(WebDriver driver) {
        super(driver);
    }

    public void enterFirstName(String firstName) {
        type(By.id("firstName"), firstName);
    }

    public void enterLastName(String lastName) {
        type(By.id("lastName"), lastName);
    }

    public void enterEmail(String email) {
        type(By.id("userEmail"), email);
    }

    public void selectGender(String gender) {
        if (gender == null) throw new IllegalArgumentException("Gender must be Male, Female, or Other");
        if (gender.equalsIgnoreCase("Male")) {
            click(By.cssSelector("label[for='gender-radio-1']"));
        } else if (gender.equalsIgnoreCase("Female")) {
            click(By.cssSelector("label[for='gender-radio-2']"));
        } else if (gender.equalsIgnoreCase("Other")) {
            click(By.cssSelector("label[for='gender-radio-3']"));
        } else {
            throw new IllegalArgumentException("Unsupported gender: " + gender);
        }
    }

    public void enterMobile(String mobile) {
        type(By.id("userNumber"), mobile);
    }

    public void selectDateOfBirth(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date of birth must not be null");
        try {
            click(By.id("dateOfBirthInput"));
            WebElement monthDropdown = waitForVisible(By.cssSelector(".react-datepicker__month-select"));
            Select month = new Select(monthDropdown);
            String monthName = date.format(MONTH_FORMAT);
            month.selectByVisibleText(monthName);
            WebElement yearDropdown = waitForVisible(By.cssSelector(".react-datepicker__year-select"));
            Select year = new Select(yearDropdown);
            year.selectByVisibleText(String.valueOf(date.getYear()));
            By day = By.xpath("//div[contains(@class,'react-datepicker__day--') and not(contains(@class,'outside-month')) and text()='" + date.getDayOfMonth() + "']");
            click(day);
        } catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.TimeoutException e) {
            throw new IllegalArgumentException("Date of birth " + date + " is unavailable in the date picker", e);
        }
    }

    public void enterSubject(String subject) {
        for (String value : subject.split(",")) {
            String item = value.trim();
            if (item.isEmpty()) throw new IllegalArgumentException("Subject list contains an empty value");
            type(By.id("subjectsInput"), item);
            waitForVisible(By.cssSelector(".subjects-auto-complete__option"));
            waitForVisible(By.id("subjectsInput")).sendKeys(Keys.ENTER);
        }
    }

    public void selectHobby(String hobby) {
        if (hobby == null || hobby.trim().isEmpty()) throw new IllegalArgumentException("Hobby must be Sports, Reading, or Music");
        for (String value : hobby.split(",")) {
            String item = value.trim();
            if (item.equalsIgnoreCase("Sports")) {
                click(By.cssSelector("label[for='hobbies-checkbox-1']"));
            } else if (item.equalsIgnoreCase("Reading")) {
                click(By.cssSelector("label[for='hobbies-checkbox-2']"));
            } else if (item.equalsIgnoreCase("Music")) {
                click(By.cssSelector("label[for='hobbies-checkbox-3']"));
            } else {
                throw new IllegalArgumentException("Unsupported hobby: " + item);
            }
        }
    }

    public void uploadPicture(String picture) {
        String path = System.getProperty("user.dir") + "/src/test/resources/testdata/" + picture;
        uploadFile(By.id("uploadPicture"), path);
    }

    public void enterAddress(String address) {
        type(By.id("currentAddress"), address);
    }

    public void selectState(String state) {
        type(By.cssSelector("#state input"), state);
        waitForVisible(By.cssSelector("#state [id*='-option-']"));
        waitForVisible(By.cssSelector("#state input")).sendKeys(Keys.ENTER);
    }

    public void selectCity(String city) {
        type(By.cssSelector("#city input"), city);
        waitForVisible(By.cssSelector("#city [id*='-option-']"));
        waitForVisible(By.cssSelector("#city input")).sendKeys(Keys.ENTER);
    }

    public void clickSubmit() {
        WebElement button = waitForClickable(By.id("submit"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
        try {
            button.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", button);
        }
    }

    public String getConfirmationTitle() {
        WebElement popupTitle = waitForVisible(By.id("example-modal-sizes-title-lg"));
        return popupTitle.getText();
    }

    public String getSubmittedValue(String label) {
        By valueLocator = By.xpath("//tr[td[1]='" + label + "']/td[2]");
        WebElement submittedValue = waitForVisible(valueLocator);
        return submittedValue.getText();
    }

    public boolean isFieldInvalid(String fieldId) {
        return !driver.findElements(By.cssSelector("#" + fieldId + ":invalid")).isEmpty();
    }

    public boolean isConfirmationDisplayed() {
        return !driver.findElements(By.id("example-modal-sizes-title-lg")).isEmpty();
    }
}
