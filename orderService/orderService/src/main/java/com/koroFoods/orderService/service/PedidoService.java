package com.koroFoods.orderService.service;

import com.koroFoods.orderService.dto.DetallePedidoRequestDTO;
import com.koroFoods.orderService.dto.PedidoResumenDto;
import com.koroFoods.orderService.dto.PedidoRequestDTO;
import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.dto.request.DetallePedidoRequest;
import com.koroFoods.orderService.dto.response.DetalleEstadoCount;
import com.koroFoods.orderService.dto.response.DetallePedidoResponse;
import com.koroFoods.orderService.dto.response.DetallePedidoUsuarioResponse;
import com.koroFoods.orderService.enums.EstadoDetallePedido;
import com.koroFoods.orderService.enums.EstadoPedido;
import com.koroFoods.orderService.feign.*;
import com.koroFoods.orderService.model.DetallePedido;
import com.koroFoods.orderService.model.Pedido;
import com.koroFoods.orderService.repository.IDetallePedidoRepository;
import com.koroFoods.orderService.repository.IPedidoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detallePedidoRepository;
    private final MesaFeignClient mesaFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;
    private final PlatoFeignClient platoFeignClient;
    private final ReservaFeignClient reservaFeignClient;

    public ResultadoResponse<List<PedidoResumenDto>> listarPedidos(EstadoPedido estado) {
        List<Pedido> pedidos = pedidoRepository.findByEstadoOpcional(estado);

        List<PedidoResumenDto> dtos = pedidos.stream().map(pedido -> {
            PedidoResumenDto dto = new PedidoResumenDto();
            dto.setIdPedido(pedido.getIdPedido());
            dto.setIdMesa(pedido.getIdMesa());
            dto.setFechaHora(pedido.getFechaHora());
            dto.setEstado(pedido.getEstado());
            dto.setTotal(pedido.getTotal());
            return dto;
        }).toList();

        return ResultadoResponse.success("Listado encontrado", dtos);
    }

    public ResultadoResponse<PedidoResumenDto> obtenerPedidoPorReserva(Integer idReserva) {
        Pedido pedido = pedidoRepository.findByIdReserva(idReserva);

        if (pedido == null) {
            return ResultadoResponse.success("No existe pedido para esta reserva", null);
        }

        PedidoResumenDto dto = new PedidoResumenDto();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaHora(pedido.getFechaHora());
        dto.setTotal(pedido.getTotal());
        dto.setIdMesa(pedido.getIdMesa());
        dto.setEstado(pedido.getEstado());

        return ResultadoResponse.success("Pedido encontrado", dto);
    }

    @Transactional
    public ResultadoResponse<Pedido> crearPedido(PedidoRequestDTO dto) {
        var mesaResp = mesaFeignClient.getTableById(dto.getIdMesa());
        if (!mesaResp.isValor() || mesaResp.getData() == null) {
            throw new RuntimeException("La mesa no existe");
        }

        var usuarioResp = usuarioFeignClient.getUsuarioById(dto.getIdUsuario());
        if (!usuarioResp.isValor() || usuarioResp.getData() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        Pedido pedido = new Pedido();
        pedido.setIdMesa(dto.getIdMesa());
        pedido.setIdUsuario(dto.getIdUsuario());
        pedido.setIdReserva(dto.getIdReserva());
        pedido.setFechaHora(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.EP);
        pedido.setSubTotal(BigDecimal.ZERO);
        pedido.setTotal(BigDecimal.ZERO);

        pedido = pedidoRepository.save(pedido);

        BigDecimal subtotalPedido = BigDecimal.ZERO;

        for (DetallePedidoRequestDTO d : dto.getDetalles()) {
            var platoResp = platoFeignClient.getDishById(d.getIdPlato());
            if (!platoResp.isValor() || platoResp.getData() == null) {
                throw new RuntimeException("El plato con ID " + d.getIdPlato() + " no existe.");
            }

            BigDecimal precioUnit = platoResp.getData().getPrecio();
            BigDecimal subtotal = precioUnit.multiply(BigDecimal.valueOf(d.getCantidad()));

            DetallePedido detalle = new DetallePedido();
            detalle.setIdPedido(pedido.getIdPedido());
            detalle.setIdPlato(d.getIdPlato());
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(precioUnit);
            detalle.setSubtotal(subtotal);
            detalle.setEstado(EstadoDetallePedido.PED);

            detallePedidoRepository.save(detalle);
            platoFeignClient.substractStockOrder(d.getIdPlato(), d.getCantidad());

            subtotalPedido = subtotalPedido.add(subtotal);
        }

        pedido.setSubTotal(subtotalPedido);
        pedido.setTotal(subtotalPedido);
        pedidoRepository.save(pedido);

        return ResultadoResponse.success("El pedido fue generado satisfactoriamente.", pedido);
    }

    public ResultadoResponse<List<Pedido>> obtenerPedidoDelCliente(Integer idCliente){
        validarId(idCliente);

        //Obtener las reservas del cliente
        ResultadoResponse<List<ReservaFeign>> reservasResponse = reservaFeignClient.obtenerListaDeReservasPorCliente(idCliente);
        var reservaData = reservasResponse.getData();


        //Sacar de las  reservas los IDS y poder filtrar despues
        List<Integer> idsReservas = reservaData.stream()
                .map(ReservaFeign::getIdReserva)
                .toList();

        if (idsReservas.isEmpty()) {
            return ResultadoResponse.error("El cliente no tiene reservas", null);
        }

        List<Pedido> pedidos = pedidoRepository.findByIdReservaIn(idsReservas);

        if (pedidos.isEmpty()){
            return ResultadoResponse.error("No se encontro pedidos",null);
        }

        return ResultadoResponse.success("Pedidos encontrados: " +  pedidos.size() ,pedidos);


    }

    private void validarId(Integer request){
        if (request == null ||request <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }
    }
}
