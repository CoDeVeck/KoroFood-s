package com.koroFoods.paymentService.repository;

import com.koroFoods.paymentService.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPagoRepository extends JpaRepository<Pago, Integer> {
}
