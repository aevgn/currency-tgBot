package org.example.tgbot.service;

import lombok.extern.slf4j.Slf4j;
import org.example.tgbot.model.CurrencyModel;
import org.example.tgbot.repository.CurrencyRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

@Service
@Slf4j
public class CurrencyService {
    @Value("${currency.api.url}")
    private String currencyApiUrl;
    private final CurrencyRepository currencyRepository;
    public CurrencyService(CurrencyRepository currencyRepository){
        this.currencyRepository = currencyRepository;
    }

    @Scheduled(fixedDelayString = "PT05H")
    public void saveCurrencyRate(){
        try{
            URL url = new URL(currencyApiUrl);
            Scanner scanner = new Scanner((InputStream) url.getContent());
            StringBuilder result = new StringBuilder();
            while(scanner.hasNext()){
                result.append(scanner.nextLine());
            }
            JSONObject obj = new JSONObject(result.toString());
            CurrencyModel currencyModel = new CurrencyModel();
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(obj.getString("Date"));
            currencyModel.setDate(dateTime.toLocalDate());
            currencyModel.setCurOfficialRate(obj.getDouble("Cur_OfficialRate"));
            currencyRepository.save(currencyModel);
            scanner.close();
            log.info("currency reate {} saved", currencyModel.getCurOfficialRate());
        }catch (Exception e){
            log.error("failed to load currency", e);
        }

    }

    public double getCurrencyRate(){
        CurrencyModel currencyModel = currencyRepository.findTopByOrderByIdDesc().orElseThrow(() -> new RuntimeException("currency not found"));
        return currencyModel.getCurOfficialRate();
    }
}
