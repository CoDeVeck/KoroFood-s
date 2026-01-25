package com.koroFoods.reservationService.repository;

import com.koroFoods.reservationService.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReservaRepository extends JpaRepository<Reserva, Integer> {
}
