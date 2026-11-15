package tests;

import base.BaseTest;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import models.Student;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.PracticeFormPage;
import utils.ExcelUtils;

public class PracticeFormTest extends BaseTest {
    @DataProvider(name = "students")
    public Object[][] students() throws IOException {
        return studentData("POSITIVE");
    }

    private Object[][] studentData(String type) throws IOException {
        String path = System.getProperty("user.dir") + "/src/test/resources/testdata/students.xlsx";
        return ExcelUtils.getStudents(path, type);
    }

    @DataProvider(name = "emptyRequiredCase") public Object[][] emptyRequiredCase() throws IOException { return studentData("EMPTY_REQUIRED"); }
    @DataProvider(name = "missingGenderCase") public Object[][] missingGenderCase() throws IOException { return studentData("MISSING_GENDER"); }
    @DataProvider(name = "invalidEmailCase") public Object[][] invalidEmailCase() throws IOException { return studentData("INVALID_EMAIL"); }
    @DataProvider(name = "shortMobileCase") public Object[][] shortMobileCase() throws IOException { return studentData("SHORT_MOBILE"); }

    @Test(dataProvider = "students")
    public void submitPracticeForm(Student student) {
        PracticeFormPage formPage = new PracticeFormPage(driver);
        formPage.enterFirstName(student.getFirstName()); formPage.enterLastName(student.getLastName());
        formPage.enterEmail(student.getEmail()); formPage.selectGender(student.getGender());
        formPage.enterMobile(student.getMobile()); formPage.selectDateOfBirth(student.getDateOfBirth());
        formPage.enterSubject(student.getSubjects()); formPage.selectHobby(student.getHobbies());
        formPage.uploadPicture(student.getPicture()); formPage.enterAddress(student.getAddress());
        formPage.selectState(student.getState()); formPage.selectCity(student.getCity()); formPage.clickSubmit();

        Assert.assertEquals(formPage.getConfirmationTitle(), "Thanks for submitting the form");
        SoftAssert softly = new SoftAssert();
        check(softly, formPage, "Student Name", student.getFirstName() + " " + student.getLastName());
        check(softly, formPage, "Student Email", student.getEmail());
        check(softly, formPage, "Gender", student.getGender());
        check(softly, formPage, "Mobile", student.getMobile());
        String expectedDate = student.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd MMMM,yyyy", Locale.ENGLISH));
        check(softly, formPage, "Date of Birth", expectedDate);
        check(softly, formPage, "Subjects", student.getSubjects());
        check(softly, formPage, "Hobbies", student.getHobbies());
        check(softly, formPage, "Picture", student.getPicture());
        check(softly, formPage, "Address", student.getAddress());
        check(softly, formPage, "State and City", student.getState() + " " + student.getCity());
        softly.assertAll();
    }

    private void check(SoftAssert softly, PracticeFormPage page, String field, String expected) {
        softly.assertEquals(page.getSubmittedValue(field), expected, "Incorrect submitted value for " + field);
    }

    @Test(dataProvider = "emptyRequiredCase")
    public void emptyRequiredFieldsAreRejected(Student student) {
        PracticeFormPage page = new PracticeFormPage(driver);
        page.clickSubmit();
        Assert.assertFalse(page.isConfirmationDisplayed(), "Confirmation must not appear for an empty form");
        Assert.assertTrue(page.isFieldInvalid("firstName"), "First name should be invalid");
        Assert.assertTrue(page.isFieldInvalid("lastName"), "Last name should be invalid");
        Assert.assertTrue(page.isFieldInvalid("userNumber"), "Mobile should be invalid");
        Assert.assertTrue(driver.findElement(By.id("firstName")).getAttribute("validationMessage").length() > 0,
                "Browser should report native validation for the first required field");
    }

    @Test(dataProvider = "missingGenderCase")
    public void submissionWithoutGenderIsRejected(Student student) {
        PracticeFormPage page = new PracticeFormPage(driver);
        page.enterFirstName(student.getFirstName()); page.enterLastName(student.getLastName()); page.enterMobile(student.getMobile());
        page.clickSubmit();
        Assert.assertFalse(page.isConfirmationDisplayed(), "Confirmation must not appear without gender");
        Assert.assertTrue(page.isFieldInvalid("gender-radio-1"), "Gender group should be invalid");
    }

    @Test(dataProvider = "invalidEmailCase")
    public void invalidEmailIsRejected(Student student) {
        PracticeFormPage page = new PracticeFormPage(driver);
        page.enterFirstName(student.getFirstName()); page.enterLastName(student.getLastName()); page.enterEmail(student.getEmail());
        page.selectGender(student.getGender()); page.enterMobile(student.getMobile()); page.clickSubmit();
        Assert.assertFalse(page.isConfirmationDisplayed(), "Confirmation must not appear for an invalid email");
        Assert.assertTrue(page.isFieldInvalid("userEmail"), "Email should fail native browser validation");
        Assert.assertTrue(driver.findElement(By.id("userEmail")).getAttribute("validationMessage").length() > 0,
                "Browser should report native email validation");
    }

    @Test(dataProvider = "shortMobileCase")
    public void mobileWithFewerThanTenDigitsIsRejected(Student student) {
        PracticeFormPage page = new PracticeFormPage(driver);
        page.enterFirstName(student.getFirstName()); page.enterLastName(student.getLastName()); page.selectGender(student.getGender());
        page.enterMobile(student.getMobile()); page.clickSubmit();
        Assert.assertFalse(page.isConfirmationDisplayed(), "Confirmation must not appear for a short mobile number");
        Assert.assertTrue(page.isFieldInvalid("userNumber"), "Mobile should fail required/pattern validation");
    }
}
