package pl.catchex.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateParser {

    private static final Logger logger = Logger.getLogger(DateParser.class.getName());

    private final DateTimeFormatter dateFormatter;

    public DateParser(String dateFormat){
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public Optional<LocalDate> parse(String dateStr){
        try {
            return Optional.of(LocalDate.parse(dateStr, dateFormatter));
        }catch (DateTimeParseException ex){
            logger.log(Level.WARNING,"Date cannot be parsed [ dateStr={0} ]", dateStr );
        }
        return Optional.empty();
    }
}
