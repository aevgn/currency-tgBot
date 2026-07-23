package org.example.tgbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class UserModel {
    @Id
    private Long chatId;
    private double rentUsd;

}
