package io.rewardsapp.report;

import io.rewardsapp.domain.RecyclableMaterial;
import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.domain.UserRecyclingActivity;
import io.rewardsapp.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

@Slf4j
public class CenterReport {
    public static final String DATE_FORMATTER = "yyyy-MM-dd hh:mm:ss";
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private final List<RecyclingCenter> centers;
    private static final String[] HEADERS = { "ID", "Center Name", "Contact", "County", "City", "Accepted Materials", "Total Activities",  "Created At" };

    public CenterReport(List<RecyclingCenter> centers) {
        this.centers = centers;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Recycling Centers");
        setHeaders();
    }

    public InputStreamResource export() {
        return generateReport();
    }

    private void setHeaders() {
        Row headerRow = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        range(0, HEADERS.length).forEach(index -> {
            Cell cell = headerRow.createCell(index);
            cell.setCellValue(HEADERS[index]);
            cell.setCellStyle(style);
        });
    }

    private InputStreamResource generateReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontHeight(10);
            style.setFont(font);
            int rowIndex = 1;
            for (RecyclingCenter center : centers) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(center.getCenterId());
                row.createCell(1).setCellValue(center.getName());
                row.createCell(2).setCellValue(center.getContact());
                row.createCell(3).setCellValue(center.getCounty());
                row.createCell(4).setCellValue(center.getCity());
                String acceptedMaterials = center.getAcceptedMaterials()
                        .stream().map(RecyclableMaterial::getName).collect(Collectors.joining(", "));
                row.createCell(5).setCellValue(acceptedMaterials);
                row.createCell(6).setCellValue(center.getRecyclingActivities().size());
                row.createCell(7).setCellValue(center.getRecyclingActivities().stream().mapToLong(UserRecyclingActivity::getAmount).sum());
                row.createCell(8).setCellValue(DateFormatUtils.format(center.getCreatedAt(), DATE_FORMATTER));
            }
            workbook.write(out);

            return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("Unable to export report file");
        }
    }
}