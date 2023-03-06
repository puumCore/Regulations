package com.r_n_m.kws.Regulations._custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.r_n_m.kws.Regulations._enum.Miezi;
import com.r_n_m.kws.Regulations._util.DateUtils;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 19/07/2022
 */

public abstract class Assistant {

    public static final Integer BIRTH_YEAR = 2023;
    public static final ConcurrentHashMap<String, LocalDateTime> ONLINE_USERS = new ConcurrentHashMap<>();
    public final Clock clock = Clock.system(ZoneId.of("Africa/Nairobi"));
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
    private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Africa/Nairobi")));


    @Deprecated
    private String point_to_previous_month(String timestamp) {
        val year = timestamp.split("-")[0];
        val month = timestamp.split("-")[1];
        var targetMonth = Arrays.stream(Miezi.values())
                .filter(miezi -> miezi.getShortForm().equalsIgnoreCase(month) || miezi.getLongForm().equalsIgnoreCase(month))
                .findFirst()
                .orElse(null);
        if (targetMonth != null) {
            if (targetMonth == Miezi.JAN) {
                return String.format("%d-%s", (Integer.parseInt(year) - 1), Miezi.DEC.getLongForm());
            } else {
                targetMonth = Arrays.stream(Miezi.values())
                        .sorted(Comparator.comparingInt(Miezi::getIndex))
                        .toList()
                        .get(targetMonth.getIndex() - 1);
                return timestamp.replace(month, targetMonth.getLongForm());
            }
        }
        return timestamp;
    }

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

    protected final List<Integer> get_years_active() {
        List<Integer> years = new ArrayList<>();
        var currentYear = Integer.parseInt(get_year());
        val yearsPast = currentYear - BIRTH_YEAR;
        if (yearsPast >= 1) {
            do {
                years.add(currentYear);
                --currentYear;
            } while (currentYear != BIRTH_YEAR);
        }
        years.add(BIRTH_YEAR);
        Collections.reverse(years);
        return years;
    }

    @Deprecated
    public final int get_last_date_of_the_month() {
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public String time_stamp() {
        return String.format("%s %s", get_date(), get_time());
    }

    public final String get_time() {
        return new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());
    }

    public final String get_date() {
        return new SimpleDateFormat("yyyy-MMM-dd").format(calendar.getTime());
    }

    @Deprecated
    protected final void generate_file_from_stream(InputStream inputStream, File targetFile) throws IOException {
        OutputStream outputStream = new FileOutputStream(targetFile);
        inputStream.transferTo(outputStream);
    }

    protected final int[] get_previous_months(LocalDate current, @SuppressWarnings("SameParameterValue") final int limit) {
        val foundMonth = Arrays.stream(Miezi.values())
                .filter(miezi -> (miezi.getIndex() + 1) == current.getMonthValue())
                .findFirst()
                .orElse(null);
        if (foundMonth == null) {
            return null;
        }
        int[] previousMonths = new int[limit];
        IntStream.iterate(2, index -> index >= 0, index -> index - 1).forEach(index -> {
            var previousMonth = foundMonth.getIndex() - index;
            previousMonths[index] = previousMonth;
        });
        return previousMonths;
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

    @Deprecated
    protected final String get_next_month_n_year() {
        return new SimpleDateFormat("MMM-yyyy").format(DateUtils.asDate(DateUtils.getStartOfNextMonth()));
    }

    protected final String extract_numbers(String text) {
        final String numericText = text.replaceAll("\\D+", "").trim();
        return numericText.isEmpty() ? null : numericText;
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

    /**
     * Calculates the no of days to the provided dates.
     *
     * @param targetDate Date to end at
     * @return A -ve if the days are past the start date otherwise the days yet to reach start date
     */
    protected final long calculate_days_left_from_today(String targetDate) {
        return calculate_days_between_given_dates(LocalDate.now(clock), LocalDate.parse(targetDate, dateTimeFormatter));
    }

    /**
     * Calculates the no of days between the provided dates.
     *
     * @param start Date to start from
     * @param end   Date to end at
     * @return A -ve if the days are past the start date otherwise the days yet to reach start date
     */
    protected final long calculate_days_between_given_dates(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
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

    protected final String format_timestamp(Timestamp timestamp) {
        return DateFormatUtils.format(DateUtils.asDate(ZonedDateTime.ofInstant(timestamp.toInstant(), clock.getZone()).toLocalDateTime()), "yyyy-MMM-dd HH:mm:ss");
    }


    protected final String date() {
        return new SimpleDateFormat("dd").format(calendar.getTime());
    }

    protected String ordinal_date_suffix(int date) {
        int j = date % 10, k = date % 100;
        if (j == 1 && k != 11) {
            return date + "st";
        }
        if (j == 2 && k != 12) {
            return date + "nd";
        }
        if (j == 3 && k != 13) {
            return date + "rd";
        }
        return date + "th";
    }

    @SafeVarargs
    protected final <K> List<K> clear_null_values_from_list(K... k) {
        return Arrays.stream(k).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public final Long count_occurrences(String providedString, Character target) {
        return providedString.chars().filter(value -> value == target).count();
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean email_is_in_correct_format(String param) {
        return Pattern.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", param);
    }

    public final boolean not_a_number(String param) {
        return !Pattern.matches("[+]?[0-9]+", param);
    }

}
