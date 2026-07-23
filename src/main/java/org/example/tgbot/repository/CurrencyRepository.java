package org.example.tgbot.repository;

import org.example.tgbot.model.CurrencyModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<CurrencyModel, Long> {
    Optional<CurrencyModel> findTopByOrderByIdDesc();
}
