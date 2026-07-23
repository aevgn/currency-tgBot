package org.example.tgbot.bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tgbot.BotState;
import org.example.tgbot.config.BotConfig;
import org.example.tgbot.service.CurrencyService;
import org.example.tgbot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CurrencyService currencyService;
    private final UserService userService;

    private final Map<Long, BotState> userStates = new ConcurrentHashMap<>();

    @Override
    public String getBotUsername(){
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken(){
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update){

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            BotState currentState = userStates.getOrDefault(chatId, BotState.IDLE);

            if(currentState == BotState.WAITING_FOR_UPDATE_RENT){
                try{
                    userService.updateUser(update.getMessage().getChatId(), Double.parseDouble(update.getMessage().getText()));
                    sendMessage(chatId, "amount have been added successfully");
                    userStates.put(chatId, BotState.IDLE);
                }catch (NumberFormatException e){
                    sendMessage(chatId, "invalid number format");
                }
                return;
            }

            if (currentState == BotState.WAITING_FOR_RENT){
                try{
                    userService.addUser(update.getMessage().getChatId(), Double.parseDouble(update.getMessage().getText()));
                    sendMessage(chatId, "user have been added successfully");
                    userStates.put(chatId, BotState.IDLE);
                }catch (NumberFormatException e){
                    sendMessage(chatId, "invalid number format");
                }
                return;
            }

            switch (messageText) {
                case "/start" -> {
                    startCommandReceived(chatId);
                    return;
                }
                case "/currency" -> currencyCommandReceived(chatId);
                case "/set" -> {
                    if (userService.isUserExists(chatId)) {
                        setCommandReceived(chatId);
                        return;
                    } else {
                        sendMessage(chatId, "please choose start and register");
                    }
                }
                case "/price" -> {
                    if (userService.isUserExists(chatId)) {
                        priceCommandReceived(chatId);
                    } else {
                        sendMessage(chatId, "please choose start and register");
                    }
                }
                default -> sendMessage(chatId, "please enter valid command");
            }
        }
    }

    public void registerBotCommands(){
        List<BotCommand> listOfCommands = new ArrayList<>();

        listOfCommands.add(new BotCommand("start", "создание нового пользователя"));
        listOfCommands.add(new BotCommand("currency", "текущий курс"));
        listOfCommands.add(new BotCommand("set", "задать цену"));
        listOfCommands.add(new BotCommand("price", "цена в рублях по курсу"));

        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
            log.info("commands registered");
        } catch (TelegramApiException e){
            log.error("failed to load commands", e);
        }
    }

    private void priceCommandReceived(Long chatId){
        sendMessage(chatId, "the amount is: " + String.valueOf(Math.round(currencyService.getCurrencyRate()*userService.getPrice(chatId))) + " BYN");
    }

    private void startCommandReceived(Long chatId) {
        if (!userService.isUserExists(chatId)){
            sendMessage(chatId, "please enter rent amount");
            userStates.put(chatId, BotState.WAITING_FOR_RENT);
        }else {
            sendMessage(chatId, "user already exists");
        }
    }

    private void setCommandReceived(Long chatId){
        sendMessage(chatId, "enter new rent amount");
        userStates.put(chatId, BotState.WAITING_FOR_UPDATE_RENT);
    }

    private void currencyCommandReceived(Long chatId){
        sendMessage(chatId, String.valueOf(currencyService.getCurrencyRate()));
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try{
            execute(sendMessage);
        }catch (TelegramApiException e){
            log.error("failed to load message to chat {}", chatId, e);
        }
    }
}
