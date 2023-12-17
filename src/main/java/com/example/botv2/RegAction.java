package com.example.botv2;

import com.example.botv2.repository.PlaceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
@Component
public class RegAction implements Action {
    @Autowired
    private  PlaceRepo placeRepo;
    private enum RegistrationStage {
        LOCATION, TOWN, ADDRESS, OBLAST,COMPLETED
    }
    private final List<String> oblastList = List.of("Вінницька", "Волинська", "Дніпропетровська", "Донецька", "Житомирська", "Закарпатська", "Запорізька", "Івано-Франківська", "Київська", "Кіровоградська", "Луганська", "Львівська", "Миколаївська", "Одеська", "Полтавська", "Рівненська", "Сумська", "Тернопільська", "Харківська", "Херсонська", "Хмельницька", "Черкаська", "Чернівецька", "Чернігівська");
    private RegistrationStage currentStage = RegistrationStage.LOCATION;

    @Override
    public BotApiMethod handle(Update update) {
        var msg = update.getMessage();
        var chatId = msg.getChatId().toString();
        String text;


        switch (currentStage) {
            case LOCATION:
                text = "Розпочнемо додавання нової туристичної локації. Для успішного виконання слідуйте підказкам до отримання фрази (Реєстрація завершена. Дякую!)." +
                        " Розпочнемо з назви місця. Введіть найбільш підходящю назву";
                break;
            case TOWN:
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

                break;
            default:
                text = "Дякую за увагу";
                resetState(); // Скидання стану на початковий
                break;
        }

        return new SendMessage(chatId, text);
    }
    private long chatId;
    private String placeName;
    private String town;
    private String oblast;
    private String address;
    private String name;
    @Override
    public BotApiMethod callback(Update update) {
        var msg = update.getMessage();
        var chatId = msg.getChatId().toString();
        var userInput = msg.getText();
        String text;

        switch (currentStage) {
            case LOCATION:
                placeName = userInput;
                System.out.println("Конструктор:"+"LOCATION: "+placeName);
                currentStage = RegistrationStage.TOWN;
                text = "Ви обрали назву місця: " + userInput + "\n Натисніть на /new для продовження додавання";
                break;
            case TOWN:
                town = userInput;
                currentStage = RegistrationStage.ADDRESS;
                text = "місто успішно додано: " + userInput + "\n Натисніть на /new для продовження додавання";
                break;
            case ADDRESS:
                address = userInput;
                currentStage = RegistrationStage.OBLAST;
                text = "Адреса успішно додано: " + userInput + "\n Залишилось зовсім трішечки, натисніть на /new для продовження ";
                break;
            case OBLAST:
                oblast = userInput;
                currentStage = RegistrationStage.COMPLETED;
                text = "Область успішно додано: " + userInput + "\n Ще трохи, натисніть на /new для ЗАВЕРШЕННЯ реєстрації";
                name = update.getMessage().getChat().getFirstName();

                System.out.println("Конструктор:"+"LOCATION: "+placeName+" " +
                        "TOWN: "+town+" OBLAST: "+oblast+" ADDRESS: "+address+" USERNAME: "+name);
                Place newPlace = new Place(placeName, town, oblast,address,name);
                placeRepo.save(newPlace);
                break;
            default:

                text = "Реєстрація завершена. Дякую!";
                resetState(); // Скидання стану на початковий
                break;
        }

        return new SendMessage(chatId, text);
    }
    private void resetState() {
        currentStage = RegistrationStage.LOCATION;
    }
}
