package com.example.botv2.config;

import com.example.botv2.service.TelegramBot;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class ConfigBot {
    @Bean
    public TelegramBot telegramBot(ConfigBot configBot) {
        return new TelegramBot(configBot);
    }

    @Value("${bot.name}")
    String botName;

    @Value("${bot.key}")
    String token;
}