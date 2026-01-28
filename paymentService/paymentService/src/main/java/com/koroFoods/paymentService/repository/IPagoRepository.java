package com.koroFoods.paymentService.repository;

import com.koroFoods.paymentService.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPagoRepository extends JpaRepository<Pago, Integer> {
}
