package com.koroFoods.orderService.repository;

import com.koroFoods.orderService.dto.response.DetalleCantidadPedidos;
import com.koroFoods.orderService.dto.response.DetalleEstadoCount;
import com.koroFoods.orderService.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {


    @Query(value = """
            SELECT * 
            FROM tb_detalle_pedido
            WHERE id_pedido = :idPedido
            ORDER BY
              CASE estado
                WHEN 'PED' THEN 1
                WHEN 'ENT' THEN 2
                WHEN 'CAN' THEN 3
              END
            """,nativeQuery = true)
    List<DetallePedido> findByIdPedidoDescEstado(Integer idPedido);

    @Query(value = """
            select
            SUM(CASE WHEN estado = 'PED' then 1 else 0 END) as pedidos,
            SUM(CASE WHEN estado = 'ENT' then 1 else 0 END) as entregados,
            SUM(CASE WHEN estado = 'CAN' then 1 else 0 END) as cancelados
            from tb_detalle_pedido
            where id_pedido = :idPedido
            """,nativeQuery = true)
    DetalleEstadoCount findByIdPedido(Integer idPedido);


}
