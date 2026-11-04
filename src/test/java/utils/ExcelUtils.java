package utils;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtils {

    public static Object[][] getStudents() throws IOException {
        String path = System.getProperty("user.dir") + "/src/test/resources/testdata/students.xlsx";
        try (FileInputStream file = new FileInputStream(path);
             Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Object[][] data = new Object[sheet.getLastRowNum()][12];
            for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                Row studentRow = sheet.getRow(row);
                for (int column = 0; column < 12; column++) {
                    data[row - 1][column] = formatter.formatCellValue(studentRow.getCell(column));
                }
            }
            return data;
        }
    }
}
