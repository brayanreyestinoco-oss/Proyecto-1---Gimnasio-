package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.facturas.Factura;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Pago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera las facturas de los clientes a partir de los pagos
 * registrados en la base de datos.
 */
public class FacturaService {

    /*
     * Datos del emisor que aparecen en la factura.
     * Cámbialos por los datos reales del gimnasio.
     */
    public static final String EMISOR_NOMBRE = "Gimnasio Control de Membresías";
    public static final String EMISOR_CEDULA = "3-101-000000";
    public static final String EMISOR_DIRECCION = "Heredia, Costa Rica";
    public static final String EMISOR_TELEFONO = "0000-0000";
    public static final String EMISOR_CORREO = "facturacion@gimnasio.cr";

    /*
     * IVA incluido en el monto pagado (13 %). El monto del pago es el
     * total; el subtotal se obtiene dividiéndolo entre (1 + tasa).
     * Use 0 si los pagos no llevan impuesto.
     */
    public static final double TASA_IVA = 0.13;

    private final PagoService pagoService;

    public FacturaService(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    /** Todas las facturas, de la más reciente a la más antigua. */
    public List<Factura> listar() {
        List<Factura> facturas = new ArrayList<>();

        for (Pago pago : pagoService.getPagos()) {
            facturas.add(desdePago(pago));
        }

        return facturas;
    }

    /** Construye la factura correspondiente a un pago. */
    public Factura desdePago(Pago pago) {
        Membresia membresia = pago.getMembresia();

        BigDecimal total = BigDecimal.valueOf(pago.getMonto())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal subtotal = total.divide(
                BigDecimal.valueOf(1 + TASA_IVA), 2, RoundingMode.HALF_UP);

        BigDecimal iva = total.subtract(subtotal);

        int meses = membresia.getPlan().getDuracionEnMeses();

        return new Factura(
                String.format("FAC-%06d", pago.getIdPago()),
                pago.getFechaPago(),
                pago.getIdPago(),
                membresia.getSocio().getNombre(),
                membresia.getSocio().getCedula(),
                membresia.getSocio().getTelefono(),
                membresia.getSocio().getCorreo(),
                membresia.getPlan().getTipo(),
                membresia.getPlan().getNombre(),
                membresia.getPlan().getBeneficios(),
                pago.getFechaPago(),
                pago.getFechaPago().plusMonths(meses),
                pago.getMetodoPago(),
                TASA_IVA,
                subtotal,
                iva,
                total);
    }
}
