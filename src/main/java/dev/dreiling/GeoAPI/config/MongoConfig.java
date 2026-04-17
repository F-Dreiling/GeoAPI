package dev.dreiling.GeoAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new Converter<LocalDate, Date>() {
                    @Override
                    public Date convert(LocalDate source) {
                        return Date.from(source.atStartOfDay(ZoneOffset.UTC).toInstant());
                    }
                },
                new Converter<Date, LocalDate>() {
                    @Override
                    public LocalDate convert(Date source) {
                        return source.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
                    }
                }
        ));
    }
}