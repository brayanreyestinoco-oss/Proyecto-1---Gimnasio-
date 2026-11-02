package cr.ac.una.est.controlmembresiasgimnasio.modelo.facturas;

import java.math.BigDecimal;
import java.time.LocalDate;

// ============================================================
// Factura.java
// QUÉ HACE:      Datos de una factura (cliente, plan, subtotal, IVA, total). No se guarda en la BD:
//                se calcula desde un pago.
// CONECTADO A:   La crea FacturaService; la usan FormatoFactura (vista previa), FacturaDocx (Word)
//                y FacturacionController.
// ============================================================
/**
 * Factura de un pago de membresía. Se genera a partir de los datos
 * que ya existen en la base (pago, membresía, socio y plan), por lo
 * que no requiere tablas adicionales.
 *
 * @param numero           consecutivo de la factura (ej. FAC-000012)
 * @param fecha            fecha de emisión (fecha del pago)
 * @param idPago           identificador del pago facturado
 * @param clienteNombre    nombre del socio
 * @param clienteCedula    cédula del socio
 * @param clienteTelefono  teléfono del socio
 * @param clienteCorreo    correo del socio
 * @param planTipo         tipo de plan (MENSUAL, ANUAL, VIP)
 * @param planNombre       nombre del plan
 * @param beneficios       beneficios incluidos en el plan
 * @param periodoInicio    inicio del período cubierto
 * @param periodoFin       fin del período cubierto
 * @param metodoPago       método con el que se pagó
 * @param tasaIva          tasa de IVA aplicada (0.13 = 13 %)
 * @param subtotal         monto antes de impuesto
 * @param iva              impuesto al valor agregado
 * @param total            monto total pagado
 */
public record Factura(
        String numero,
        LocalDate fecha,
        int idPago,
        String clienteNombre,
        String clienteCedula,
        String clienteTelefono,
        String clienteCorreo,
        String planTipo,
        String planNombre,
        String beneficios,
        LocalDate periodoInicio,
        LocalDate periodoFin,
        String metodoPago,
        double tasaIva,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total) {
}
