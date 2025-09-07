package com.Concesionaria.payments_service.service;

import com.Concesionaria.payments_service.DTO.*;
import com.Concesionaria.payments_service.model.Pagos;
import com.Concesionaria.payments_service.repository.PagosRepository;
import com.Concesionaria.payments_service.util.EstadoPagos;
import com.Concesionaria.payments_service.util.FrecuenciaPago;
import com.Concesionaria.payments_service.util.MetodoPago;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagosService implements IPagosService {

    private final PagosRepository repo;
    private final PagosMapper mapper;

    @Override
    public List<PagosGetDTO> generarPagos(GenerarPagosRequestDTO request) {


        List<Pagos> pagosGenerados = new ArrayList<>();

        if (request.getFrecuenciaPago() == FrecuenciaPago.UNICO) {
            pagosGenerados.add(crearPagoUnico(request));
        } else {
            pagosGenerados = generarPagosConCuotas(request);
        }

        return pagosGenerados.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }


    private Pagos crearPagoUnico(GenerarPagosRequestDTO request) {
        Pagos pagoUnico = new Pagos();
        pagoUnico.setVentaId(request.getVentaId());
        pagoUnico.setFechaPago(LocalDate.now());
        pagoUnico.setMonto(request.getTotalVenta());
        pagoUnico.setEstado(EstadoPagos.PENDIENTE);
        pagoUnico.setMetodoPago(MetodoPago.PENDIENTE);
        pagoUnico.setActivo(true);

        return repo.save(pagoUnico);
    }

    private List<Pagos> generarPagosConCuotas(GenerarPagosRequestDTO request) {
        List<Pagos> pagos = new ArrayList<>();
        LocalDate fechaBase = LocalDate.now();

        BigDecimal montoEntrega = BigDecimal.valueOf(request.getEntrega() != null ? request.getEntrega() : 0.0);
        BigDecimal montoRestante = request.getTotalVenta().subtract(montoEntrega);
        int cantidadCuotas = (request.getCuotas() != null && request.getCuotas() > 0) ? request.getCuotas() : 1;
        BigDecimal montoPorCuota = montoRestante.divide(BigDecimal.valueOf(cantidadCuotas), 2, RoundingMode.HALF_UP);

        if (montoEntrega.compareTo(BigDecimal.ZERO) > 0) {
            pagos.add(crearPagoInicial(request, fechaBase, montoEntrega));
        }

        BigDecimal sumaCuotas = BigDecimal.ZERO;
        for (int i = 0; i < cantidadCuotas; i++) {
            Pagos cuota = crearCuota(request, fechaBase, i, cantidadCuotas, montoRestante, montoPorCuota, sumaCuotas);
            pagos.add(repo.save(cuota));

            if (i != cantidadCuotas - 1) {
                sumaCuotas = sumaCuotas.add(montoPorCuota);
            }
        }

        return pagos;
    }

    private Pagos crearPagoInicial(GenerarPagosRequestDTO request, LocalDate fechaBase, BigDecimal montoEntrega) {
        Pagos pagoInicial = new Pagos();
        pagoInicial.setVentaId(request.getVentaId());
        pagoInicial.setFechaPago(fechaBase);
        pagoInicial.setMonto(montoEntrega);
        pagoInicial.setEstado(EstadoPagos.PENDIENTE);
        pagoInicial.setMetodoPago(MetodoPago.PENDIENTE);
        pagoInicial.setActivo(true);
        return repo.save(pagoInicial);
    }

    private Pagos crearCuota(GenerarPagosRequestDTO request, LocalDate fechaBase, int index,
                             int totalCuotas, BigDecimal montoRestante,
                             BigDecimal montoPorCuota, BigDecimal sumaCuotas) {
        Pagos cuota = new Pagos();
        cuota.setVentaId(request.getVentaId());
        cuota.setEstado(EstadoPagos.PENDIENTE);
        cuota.setMetodoPago(MetodoPago.PENDIENTE);
        cuota.setFechaPago(fechaBase.plusMonths(index + 1));
        cuota.setActivo(true);

        if (index == totalCuotas - 1) {
            cuota.setMonto(montoRestante.subtract(sumaCuotas));
        } else {
            cuota.setMonto(montoPorCuota);
        }

        return cuota;
    }

    @Override
    public PagosGetDTO findById(Integer id) {
        Pagos pago = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        return mapper.toDTO(pago);
    }


    @Override
    public List<PagosGetDTO> findAll() {
        return repo.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagosGetDTO> findByVentaId(Integer ventaId) {
        return repo.findByVentaId(ventaId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagosGetDTO> getPagosPorVentas(List<Integer> ventaIds) {
        return repo.findByVentaIdIn(ventaIds).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public PagosGetDTO confirmarPago(Integer id, MetodoPago metodoPago) {
        Pagos pago = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));

        pago.setEstado(EstadoPagos.PAGADO);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDate.now());
        pago.setActivo(Boolean.FALSE);
        Pagos saved = repo.save(pago);


        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PagosGetDTO update(Integer id, PagosPutDTO putDTO) {
        Pagos pago = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));

        pago = mapper.update(pago, putDTO);
        Pagos saved = repo.save(pago);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PagosGetDTO delete(Integer id) {
        Pagos pago = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        pago.setFechaPago(null);
        pago.setEstado(EstadoPagos.PENDIENTE);
        pago.setMetodoPago(MetodoPago.PENDIENTE);
        pago.setActivo(Boolean.TRUE);
        Pagos saved = repo.save(pago);
        return mapper.toDTO(saved);
    }

    // ========== MÉTODOS AUXILIARES ==========
    @Override
    public BigDecimal getTotalPagado(Integer ventaId) {
        return repo.findByVentaIdAndEstado(ventaId, EstadoPagos.PAGADO).stream()
                .map(Pagos::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Integer getCantidadPagos(Integer ventaId) {
        return repo.countByVentaIdAndActivoTrue(ventaId);
    }

    @Override
    public Optional<PagosGetDTO> getUltimoPago(Integer ventaId) {
        return repo.findFirstByVentaIdOrderByFechaPagoDesc(ventaId)
                .map(mapper::toDTO);
    }
}