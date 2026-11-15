package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import models.Student;

public class ExcelUtils {
    private static final String[] REQUIRED_HEADERS = {"FirstName", "LastName", "Email", "Gender", "Mobile",
            "DateOfBirth", "Subject", "Hobby", "Picture", "Address", "State", "City"};
    private static final String[] OPTIONAL_HEADERS = {"Subjects", "Hobbies"};
    private static final String TEST_TYPE_HEADER = "TestType";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    public static Object[][] getStudents(String path, String requestedType) throws IOException {
        try (FileInputStream file = new FileInputStream(path); Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) throw new IllegalArgumentException("Student workbook is missing its header row");
            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < header.getLastCellNum(); i++) columns.put(formatter.formatCellValue(header.getCell(i)).trim(), i);
            for (String required : REQUIRED_HEADERS) {
                if (!columns.containsKey(required)) throw new IllegalArgumentException("Missing required Excel column: " + required);
            }
            List<Student> students = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || row.getPhysicalNumberOfCells() == 0) continue;
                String testType = getOptionalValue(row, columns, TEST_TYPE_HEADER, "POSITIVE", formatter);
                if (!testType.equalsIgnoreCase(requestedType)) continue;
                String[] values = new String[REQUIRED_HEADERS.length];
                boolean empty = true;
                for (int i = 0; i < REQUIRED_HEADERS.length; i++) {
                    values[i] = formatter.formatCellValue(row.getCell(columns.get(REQUIRED_HEADERS[i]))).trim();
                    if (!values[i].isEmpty()) empty = false;
                }
                if (empty && testType.equalsIgnoreCase("POSITIVE")) continue;
                if (testType.equalsIgnoreCase("POSITIVE")) {
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].isEmpty()) throw new IllegalArgumentException("Missing value for " + REQUIRED_HEADERS[i] + " in Excel row " + (rowIndex + 1));
                    }
                }
                String subjects = getOptionalValue(row, columns, OPTIONAL_HEADERS[0], values[6], formatter);
                String hobbies = getOptionalValue(row, columns, OPTIONAL_HEADERS[1], values[7], formatter);
                try {
                    LocalDate dateOfBirth = values[5].isEmpty() ? null : LocalDate.parse(values[5], DATE_FORMAT);
                    students.add(new Student(values[0], values[1], values[2], values[3], values[4],
                            dateOfBirth, values[6], values[7], values[8], values[9], values[10], values[11], subjects, hobbies));
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid DateOfBirth in Excel row " + (rowIndex + 1) + ": " + values[5], e);
                }
            }
            if (students.isEmpty()) throw new IllegalArgumentException("No " + requestedType + " student rows found in workbook");
            Object[][] data = new Object[students.size()][1];
            for (int i = 0; i < students.size(); i++) data[i][0] = students.get(i);
            return data;
        }
    }

    private static String getOptionalValue(Row row, Map<String, Integer> columns, String header,
            String fallback, DataFormatter formatter) {
        Integer column = columns.get(header);
        if (column == null) return fallback;
        String value = formatter.formatCellValue(row.getCell(column)).trim();
        return value.isEmpty() ? fallback : value;
    }
}
