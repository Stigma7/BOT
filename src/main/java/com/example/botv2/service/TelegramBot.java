package com.example.botv2.service;

import com.example.botv2.config.ConfigBot;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


public class TelegramBot extends TelegramLongPollingBot {

    final ConfigBot configBot;
    public TelegramBot(ConfigBot configBot) {

        this.configBot = configBot;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start","розпочати роботу"));
        commands.add(new BotCommand("/map","мапа України"));
        commands.add(new BotCommand("/info","інформація"));
        commands.add(new BotCommand("/travel","обрати місто для туру"));
        commands.add(new BotCommand("/newplace","додати туристичне місце"));
        commands.add(new BotCommand("/help","Як користуватись ботом"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        }catch (TelegramApiException e){
            System.out.println("Error: "+ e.getMessage());
        }
    }
    @Override
    public String getBotUsername() {
        return this.configBot.getBotName();
    }

    public String getBotToken() {
        return this.configBot.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":

                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/info":
                    infoCommand(chatId, update.getMessage().getChat().getFirstName(), update.getMessage().getChat().getBio());
                    break;
                case "/help":

                    sendMessage(chatId, "É tropical samba do brasil /start\n" +
                            "Sensacional samba do brasil /info\n" +
                            "É mundial samba do brasil /travel\n" +
                            "Fenomenal samba do brasil /help");
                    break;

                default:
                    sendMessage(chatId, "Нажаль, зараз я не розумію про що Ви");
            }
        }
    }

    private void infoCommand(long chatId, String firstName, String bio) {

    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Доброго дня шановний "+name;
        System.out.println("Відповідь користувачу: " + name);

        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        }
        catch (TelegramApiException e){
            System.out.println("Error: "+ e.getMessage());
        }


    }

}