package com.koroFoods.orderService.repository;

import com.koroFoods.orderService.enums.EstadoPedido;
import com.koroFoods.orderService.model.Pedido;
import feign.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {
	@Query("""
			SELECT p FROM Pedido p
			WHERE (:estado IS NULL OR p.estado = :estado)
			  AND p.estado <> com.koroFoods.orderService.enums.EstadoPedido.PA
			""")
	List<Pedido> findByEstadoOpcional(@Param("estado") EstadoPedido estado);

	Pedido findByIdReserva(Integer idReserva);
}
