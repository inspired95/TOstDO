package pl.catchex.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.logging.Logger;

public class DateParser {

    private static final Logger logger = Logger.getLogger(DateParser.class.getName());


    private final String dateFormat;

    public DateParser(String dateFormat){
        this.dateFormat = dateFormat;
    }

    public Optional<LocalDate> parse(String dateStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        try {
            return Optional.of(LocalDate.parse(dateStr, formatter));
        }catch (DateTimeParseException ex){
            logger.warning("Date cannot be parsed [ dateStr=" + dateStr + " ]" );
        }
        return Optional.empty();
    }
}
