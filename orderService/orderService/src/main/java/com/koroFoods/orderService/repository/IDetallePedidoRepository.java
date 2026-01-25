package com.koroFoods.orderService.repository;

import com.koroFoods.orderService.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
}
