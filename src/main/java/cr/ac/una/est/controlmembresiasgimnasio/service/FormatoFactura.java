package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.facturas.Factura;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// ============================================================
// FormatoFactura.java
// QUÉ HACE:      Da formato a fechas, dinero y porcentajes, escribe el total en letras y arma la
//                vista previa de texto de la factura.
// CONECTADO A:   La usan FacturaDocx, FacturacionController y varias pantallas para mostrar montos
//                y fechas.
// ============================================================
/**
 * Formato de textos de una factura: montos, fechas, total en letras
 * y vista previa en texto plano.
 */
public final class FormatoFactura {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] UNIDADES = {
            "cero", "uno", "dos", "tres", "cuatro", "cinco", "seis",
            "siete", "ocho", "nueve", "diez", "once", "doce", "trece",
            "catorce", "quince", "dieciséis", "diecisiete", "dieciocho",
            "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés",
            "veinticuatro", "veinticinco", "veintiséis", "veintisiete",
            "veintiocho", "veintinueve"};

    private static final String[] DECENAS = {
            "", "", "", "treinta", "cuarenta", "cincuenta", "sesenta",
            "setenta", "ochenta", "noventa"};

    private static final String[] CENTENAS = {
            "", "ciento", "doscientos", "trescientos", "cuatrocientos",
            "quinientos", "seiscientos", "setecientos", "ochocientos",
            "novecientos"};

    private static final int ANCHO = 58;

    private FormatoFactura() {
    }

    // Fecha en formato dd/MM/yyyy.
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : FECHA.format(fecha);
    }

    /** Formatea un monto en colones, ej. ₡15,000.00 */
    public static String dinero(BigDecimal monto) {
        DecimalFormat formato = new DecimalFormat(
                "#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return "₡" + formato.format(monto);
    }

    // Porcentaje listo para mostrar.
    public static String porcentaje(double tasa) {
        return new DecimalFormat("0.##",
                DecimalFormatSymbols.getInstance(Locale.US))
                .format(tasa * 100) + "%";
    }

    /** Escribe el monto con letras, ej. "QUINCE MIL COLONES CON 00/100". */
    public static String totalEnLetras(BigDecimal monto) {
        long enteros = monto.longValue();
        int centimos = monto.remainder(BigDecimal.ONE)
                .movePointRight(2).abs().intValue();

        String moneda = enteros == 1 ? "colón" : "colones";

        return (apocope(letras(enteros)) + " " + moneda
                + String.format(" con %02d/100", centimos))
                .toUpperCase(Locale.ROOT);
    }

    private static String letras(long n) {
        if (n == 0) {
            return "cero";
        }

        long millones = n / 1_000_000;
        int miles = (int) ((n / 1000) % 1000);
        int resto = (int) (n % 1000);

        StringBuilder texto = new StringBuilder();

        if (millones > 0) {
            texto.append(millones == 1
                    ? "un millón"
                    : apocope(letras(millones)) + " millones");
        }

        if (miles > 0) {
            texto.append(texto.length() > 0 ? " " : "");
            texto.append(miles == 1
                    ? "mil"
                    : apocope(menorQueMil(miles)) + " mil");
        }

        if (resto > 0) {
            texto.append(texto.length() > 0 ? " " : "");
            texto.append(menorQueMil(resto));
        }

        return texto.toString();
    }

    private static String menorQueMil(int n) {
        if (n == 100) {
            return "cien";
        }

        StringBuilder texto = new StringBuilder();

        if (n >= 100) {
            texto.append(CENTENAS[n / 100]);
        }

        int resto = n % 100;

        if (resto > 0) {
            texto.append(texto.length() > 0 ? " " : "");

            if (resto < 30) {
                texto.append(UNIDADES[resto]);
            } else {
                texto.append(DECENAS[resto / 10]);

                if (resto % 10 > 0) {
                    texto.append(" y ").append(UNIDADES[resto % 10]);
                }
            }
        }

        return texto.toString();
    }

    /** "uno" pierde la o antes de "mil" y "millones". */
    private static String apocope(String texto) {
        if (texto.endsWith("veintiuno")) {
            return texto.substring(0, texto.length() - 9) + "veintiún";
        }

        if (texto.endsWith("uno")) {
            return texto.substring(0, texto.length() - 1);
        }

        return texto;
    }

    /** Vista previa de la factura en texto plano. */
    public static String vistaPrevia(Factura f) {
        StringBuilder t = new StringBuilder();
        String linea = "=".repeat(ANCHO);
        String guion = "-".repeat(ANCHO);

        t.append(linea).append('\n');
        t.append(centrar(FacturaService.EMISOR_NOMBRE)).append('\n');
        t.append(centrar("Céd. jurídica: "
                + FacturaService.EMISOR_CEDULA)).append('\n');
        t.append(centrar(FacturaService.EMISOR_DIRECCION)).append('\n');
        t.append(centrar("Tel: " + FacturaService.EMISOR_TELEFONO
                + "  |  " + FacturaService.EMISOR_CORREO)).append('\n');
        t.append(linea).append('\n');
        t.append(centrar("FACTURA  N° " + f.numero())).append('\n');
        t.append(linea).append('\n');
        t.append("Fecha de emisión : ").append(fecha(f.fecha())).append('\n');
        t.append("Condición venta  : Contado\n");
        t.append("Medio de pago    : ").append(f.metodoPago()).append('\n');
        t.append(guion).append('\n');
        t.append("CLIENTE\n");
        t.append("Nombre   : ").append(f.clienteNombre()).append('\n');
        t.append("Cédula   : ").append(f.clienteCedula()).append('\n');
        t.append("Teléfono : ").append(f.clienteTelefono()).append('\n');
        t.append("Correo   : ").append(f.clienteCorreo()).append('\n');
        t.append(guion).append('\n');
        t.append("DETALLE\n");
        t.append("1 x Membresía ").append(f.planNombre())
                .append(" (").append(f.planTipo()).append(")\n");
        t.append("    Período: ").append(fecha(f.periodoInicio()))
                .append(" al ").append(fecha(f.periodoFin())).append('\n');
        t.append("    Incluye: ").append(f.beneficios()).append('\n');
        t.append(guion).append('\n');
        t.append(derecha("Subtotal", dinero(f.subtotal())));
        t.append(derecha("IVA " + porcentaje(f.tasaIva()), dinero(f.iva())));
        t.append(derecha("TOTAL", dinero(f.total())));
        t.append(guion).append('\n');
        t.append("Son: ").append(totalEnLetras(f.total())).append('\n');
        t.append(linea).append('\n');
        t.append(centrar("¡Gracias por su preferencia!")).append('\n');

        return t.toString();
    }

    private static String centrar(String texto) {
        int espacios = Math.max(0, (ANCHO - texto.length()) / 2);
        return " ".repeat(espacios) + texto;
    }

    private static String derecha(String etiqueta, String valor) {
        int relleno = Math.max(1, ANCHO - etiqueta.length() - valor.length());
        return etiqueta + " ".repeat(relleno) + valor + "\n";
    }
}
