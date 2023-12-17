package com.example.botv2;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class RegAction implements Action {

    private PlaceRepo placeRepo;
    private enum RegistrationStage {
        EMAIL, PHONE_NUMBER, ADDRESS, OBLAST,COMPLETED
    }
    private final List<String> oblastList = List.of("Вінницька", "Волинська", "Дніпропетровська", "Донецька", "Житомирська", "Закарпатська", "Запорізька", "Івано-Франківська", "Київська", "Кіровоградська", "Луганська", "Львівська", "Миколаївська", "Одеська", "Полтавська", "Рівненська", "Сумська", "Тернопільська", "Харківська", "Херсонська", "Хмельницька", "Черкаська", "Чернівецька", "Чернігівська");
    private RegistrationStage currentStage = RegistrationStage.EMAIL;

    @Override
    public BotApiMethod handle(Update update) {
        var msg = update.getMessage();
        var chatId = msg.getChatId().toString();
        String text;


        switch (currentStage) {
            case EMAIL:
                text = "Розпочнемо додавання нової туристичної локації. Для успішного виконання слідуйте підказкам до отримання фрази (Реєстрація завершена. Дякую!)." +
                        " Розпочнемо з назви місця. Введіть найбільш підходящю назву";
                break;
            case PHONE_NUMBER:
                text = "Тепер введіть місто в якому знаходиться локація:";
                break;
            case ADDRESS:
                text = "Напишіть адресу в якій знаходиться локація:";
                break;
            case OBLAST:
                text = "Виберіть область, в якій знаходиться локація:";
                var replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setSelective(true); // Робимо клавіатуру вибору області вибірковою

                var keyboard = new ArrayList<KeyboardRow>();

                // Додаємо кнопки з областями
                for (String oblast : oblastList) {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    KeyboardButton button = new KeyboardButton();
                    button.setText(oblast);
                    keyboardRow.add(button);
                    keyboard.add(keyboardRow);
                }

                replyKeyboardMarkup.setKeyboard(keyboard);

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(text);
                message.setReplyMarkup(replyKeyboardMarkup);

                return message;

            default:
                text = "Реєстрація завершена. Дякую!";
                resetState(); // Скидання стану на початковий
                break;
        }

        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod callback(Update update) {
        var msg = update.getMessage();
        var chatId = msg.getChatId().toString();
        var userInput = msg.getText();
        String text;

        switch (currentStage) {
            case EMAIL:
                currentStage = RegistrationStage.PHONE_NUMBER;
                text = "Ви обрали назву місця: " + userInput + "\n Натисніть на /new для продовження додавання";
                break;
            case PHONE_NUMBER:
                // Збереження номеру телефону (userInput) в базі даних або деінде
                currentStage = RegistrationStage.ADDRESS;
                text = "місто успішно додано: " + userInput + "\n Натисніть на /new для продовження додавання";
                break;
            case ADDRESS:
                // Збереження адреси (userInput) в базі даних або деінде
                currentStage = RegistrationStage.OBLAST;
                text = "Адреса успішно додано: " + userInput + "\n Залишилось зовсім трішечки, натисніть на /new для продовження ";
                break;
            case OBLAST:
                // Збереження області (userInput) в базі даних або деінде
                currentStage = RegistrationStage.COMPLETED;
                text = "Область успішно додано: " + userInput + "\n Ще трохи, натисніть на /new для ЗАВЕРШЕННЯ реєстрації";
                break;
            default:
                text = "Реєстрація завершена. Дякую!";
                resetState(); // Скидання стану на початковий
                break;
        }

        return new SendMessage(chatId, text);
    }
    private void resetState() {
        currentStage = RegistrationStage.EMAIL;
    }
}
