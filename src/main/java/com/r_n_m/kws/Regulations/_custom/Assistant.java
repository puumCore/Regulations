package com.r_n_m.kws.Regulations._custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.r_n_m.kws.Regulations._util.DateUtils;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 19/07/2022
 */

public abstract class Assistant {
    public final Clock clock = Clock.system(ZoneId.of("Africa/Nairobi"));
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
    private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Africa/Nairobi")));


    public final void protect_excel_from_unauthorised_modification(XSSFWorkbook xssfWorkbook) {
        val password = RandomStringUtils.randomAlphanumeric(8, 16);
        xssfWorkbook.setWorkbookPassword(password, HashAlgorithm.sha512);
        IntStream.range(0, xssfWorkbook.getNumberOfSheets())
                .mapToObj(xssfWorkbook::getSheetAt)
                .forEach(xssfSheet -> {
                    xssfSheet.lockDeleteColumns(true);
                    xssfSheet.lockDeleteRows(true);
                    xssfSheet.lockFormatCells(true);
                    xssfSheet.lockFormatColumns(true);
                    xssfSheet.lockFormatRows(true);
                    xssfSheet.lockInsertColumns(true);
                    xssfSheet.lockInsertRows(true);
                    xssfSheet.getCTWorksheet().getSheetProtection().setPassword(password.getBytes(StandardCharsets.UTF_8));
                    xssfSheet.enableLocking();
                });
        xssfWorkbook.lockStructure();
    }

    @Deprecated
    public final int get_last_date_of_the_month() {
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public final String get_date() {
        return new SimpleDateFormat("yyyy-MMM-dd").format(calendar.getTime());
    }

    @Deprecated
    protected final void generate_file_from_stream(InputStream inputStream, File targetFile) throws IOException {
        OutputStream outputStream = new FileOutputStream(targetFile);
        inputStream.transferTo(outputStream);
    }

    protected final String get_year() {
        return new SimpleDateFormat("yyyy").format(calendar.getTime());
    }

    protected final String get_month() {
        return new SimpleDateFormat("MMM").format(calendar.getTime());
    }

    protected final String get_month_n_year() {
        return "%s-%s".formatted(get_month(), get_year());
    }

    @SuppressWarnings("SameParameterValue")
    protected final JsonElement read_json_from_file(String filePath) throws Exception {
        var inputStreamReader = new InputStreamReader(get_file_from_resource(filePath), StandardCharsets.UTF_8);
        var bufferedReader = new BufferedReader(inputStreamReader);
        var jsonElement = JsonParser.parseReader(bufferedReader);
        bufferedReader.close();
        return jsonElement;
    }

    protected final InputStream get_file_from_resource(String filePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("File (" + filePath + ") could not be found");
        }
        return inputStream;
    }

    protected final long calculate_days_between_given_dates(Date start, Date end, ChronoUnit chronoUnit) {
        return chronoUnit.between(DateUtils.asLocalDateTime(start), DateUtils.asLocalDateTime(end));
    }

    @Deprecated
    protected final String get_unique_random_name(Set<String> knownNames) {
        val randomAlphabetic = get_unique_random_name();
        val anyMatch = knownNames
                .stream()
                .anyMatch(randomAlphabetic::equals);
        return anyMatch ? get_unique_random_name(knownNames) : randomAlphabetic;
    }

    protected final String get_unique_random_name() {
        return RandomStringUtils.randomAlphabetic(8, 45);
    }

    protected final boolean the_date_format_is_NOT_acceptable(String param) {
        return !Pattern.matches("[0-9]{4}-[a-zA-Z]{3,4}-\\d{1,2}", param);
    }

    @Deprecated
    protected final boolean the_month_format_is_acceptable(String param) {
        return Pattern.matches("[0-9]{4}-[a-zA-Z]{3,4}", param);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean phoneNumber_is_in_correct_format(String param) {
        return Pattern.matches("^\\d{10}$", param) ||
                Pattern.matches("^(\\d{3}[- .]?){2}\\d{4}$", param) ||
                Pattern.matches("^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", param) ||
                Pattern.matches("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", param);
    }

}
