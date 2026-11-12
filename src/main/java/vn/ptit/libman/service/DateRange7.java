package vn.ptit.libman.service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

/** Same inclusive date range is passed unchanged to aggregate and detail queries. */
public record DateRange7(LocalDate startDate, LocalDate endDate) {
    public DateRange7 {
        if (startDate==null || endDate==null || startDate.isAfter(endDate)) throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
    }
    public static DateRange7 parse(String start, String end) throws ValidationException7 {
        try { return new DateRange7(LocalDate.parse(start == null ? "" : start), LocalDate.parse(end == null ? "" : end)); }
        catch (DateTimeParseException | IllegalArgumentException ex) {
            throw new ValidationException7(Map.of("dateRange","Chọn đủ hai ngày hợp lệ; ngày bắt đầu phải trước hoặc bằng ngày kết thúc."));
        }
    }
}
