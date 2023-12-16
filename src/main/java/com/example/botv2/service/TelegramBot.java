package com.example.botv2.service;

import com.example.botv2.config.ConfigBot;
import com.example.botv2.model.Place;
import com.example.botv2.repository.PlaceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


public class TelegramBot extends TelegramLongPollingBot {
@Autowired
    private PlaceRepo placeRepo;
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
                    sendMessage(chatId, "Нажаль, ви олень який ніц не розуміє");
                    break;
                case "/newplace":
                    createPlace(chatId, update.getMessage(), update.getMessage().getChat().getFirstName());
                    break;
                case "/help":

                    sendMessage(chatId, "É tropical samba do brasil /start\n");
                    break;


            }
        }
    }
    private int currentStep = 0;
    private long chatId;
    private String placeName;
    private String town;
    private String oblast;
    private String address;
    private String name;

    private void processUserInput(long chatId, String messageText, String name) {
        switch (currentStep) {
            case 0:
                placeName = messageText;
                currentStep++;
                sendMessage(chatId, "Введіть назву міста, де воно знаходиться");
                break;
            case 1:
                town = messageText;
                currentStep++;
                sendMessage(chatId, "Введіть назву області");
                break;
            case 2:
                oblast = messageText;
                currentStep++;
                sendMessage(chatId, "Введіть адресу місця");
                break;
            case 3:
                address = messageText;
                // Збереження інформації про місце у базі даних
                Place newPlace = new Place(placeName, town, oblast, address, name);
                placeRepo.save(newPlace);
                currentStep = 0; // Скидання стану після завершення
                sendMessage(chatId, "Дані збережено!");
                break;
            default:
                // Непередбачуваний крок, можна обробити тут
                break;
        }
    }


    private void createPlace(long chatId, Message message, String name) {
        this.chatId = chatId;

        // Виклик handleNextStep() для кожного кроку
        processUserInput(chatId, String.valueOf(message), name);
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