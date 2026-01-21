package com.koroFoods.orderService.repository;

import com.koroFoods.orderService.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {
}
