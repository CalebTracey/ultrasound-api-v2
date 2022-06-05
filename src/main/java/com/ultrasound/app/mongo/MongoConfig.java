package com.ultrasound.app.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:/application-${spring.profiles.active}.properties")
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final Environment env;

    @Override
    protected @NotNull String getDatabaseName() {
        return "ultrasound";
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(Objects.requireNonNull(env.getProperty("ultrasound.app.mongoUri"))));
    }

    @Override
    public @NotNull Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.ultrasound.app");
    }
}
