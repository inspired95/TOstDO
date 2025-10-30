package pl.catchex.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DateParser {

    private static final Logger logger = LoggerFactory.getLogger(DateParser.class);

    private final DateTimeFormatter dateFormatter;

    public DateParser(String dateFormat){
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public Optional<LocalDate> parse(String dateStr){
        try {
            return Optional.of(LocalDate.parse(dateStr, dateFormatter));
        }catch (DateTimeParseException ex){
            logger.warn("Date cannot be parsed [ dateStr={} ]", dateStr );
        }
        return Optional.empty();
    }
}
