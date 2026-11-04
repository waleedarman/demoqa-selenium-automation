package tests;

import base.BaseTest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.PracticeFormPage;
import utils.ExcelUtils;

public class PracticeFormTest extends BaseTest {

    @DataProvider(name = "students")
    public Object[][] students() throws IOException {
        return ExcelUtils.getStudents();
    }

    @Test(dataProvider = "students")
    public void submitPracticeForm(String firstName, String lastName, String email,
            String gender, String mobile, String dateOfBirth, String subject,
            String hobby, String picture, String address, String state, String city) {
        PracticeFormPage formPage = new PracticeFormPage(driver);
        formPage.enterFirstName(firstName);
        formPage.enterLastName(lastName);
        formPage.enterEmail(email);
        formPage.selectGender(gender);
        formPage.enterMobile(mobile);
        formPage.selectDateOfBirth(dateOfBirth);
        formPage.enterSubject(subject);
        formPage.selectHobby(hobby);
        formPage.uploadPicture(picture);
        formPage.enterAddress(address);
        formPage.selectState(state);
        formPage.selectCity(city);
        formPage.clickSubmit();

        Assert.assertEquals(formPage.getConfirmationTitle(), "Thanks for submitting the form");
        Assert.assertEquals(formPage.getSubmittedValue("Student Name"), firstName + " " + lastName);
        Assert.assertEquals(formPage.getSubmittedValue("Student Email"), email);
        Assert.assertEquals(formPage.getSubmittedValue("Gender"), gender);
        Assert.assertEquals(formPage.getSubmittedValue("Mobile"), mobile);
        DateTimeFormatter excelFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        DateTimeFormatter popupFormat = DateTimeFormatter.ofPattern("dd MMMM,yyyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(dateOfBirth, excelFormat);
        String expectedDate = date.format(popupFormat);
        Assert.assertEquals(formPage.getSubmittedValue("Date of Birth"), expectedDate);
        Assert.assertEquals(formPage.getSubmittedValue("Subjects"), subject);
        Assert.assertEquals(formPage.getSubmittedValue("Hobbies"), hobby);
        Assert.assertEquals(formPage.getSubmittedValue("Picture"), picture);
        Assert.assertEquals(formPage.getSubmittedValue("Address"), address);
        Assert.assertEquals(formPage.getSubmittedValue("State and City"), state + " " + city);
    }

    @Test
    public void emptyFormMarksNameAndMobileInvalid() {
        PracticeFormPage formPage = new PracticeFormPage(driver);
        formPage.clickSubmit();
        Assert.assertTrue(formPage.isFieldInvalid("firstName"));
        Assert.assertTrue(formPage.isFieldInvalid("lastName"));
        Assert.assertTrue(formPage.isFieldInvalid("userNumber"));
    }
}
