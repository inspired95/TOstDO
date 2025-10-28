package pl.catchex.common;

import pl.catchex.config.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateParser {
    private final Configuration configuration;

    public DateParser(Configuration configuration){
        this.configuration = configuration;
    }

    public Optional<LocalDate> parse(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(configuration.getDateFormat());
        try {
            return Optional.of(LocalDate.parse(date, formatter));
        }catch (DateTimeParseException ex){
            //TODO log
        }
        return Optional.empty();
    }
}
