package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.facturas.Factura;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Exporta una factura a un archivo Word (.docx).
 *
 * Un .docx es un ZIP con XML, así que se genera directamente con
 * java.util.zip y no se necesita ninguna librería externa.
 */
public final class FacturaDocx {

    private static final String AZUL_OSCURO = "1F2937";
    private static final String GRIS_CLARO = "F3F4F6";
    private static final String GRIS_TEXTO = "6B7280";

    /* Ancho útil de la página en twips (Carta con márgenes de 0.75"). */
    private static final int ANCHO = 10080;

    private FacturaDocx() {
    }

    /** Escribe la factura en el archivo indicado. */
    public static void exportar(Factura factura, Path destino)
            throws IOException {

        try (OutputStream salida = Files.newOutputStream(destino);
             ZipOutputStream zip = new ZipOutputStream(
                     salida, StandardCharsets.UTF_8)) {

            agregar(zip, "[Content_Types].xml", CONTENT_TYPES);
            agregar(zip, "_rels/.rels", RELS);
            agregar(zip, "word/document.xml", documento(factura));
        }
    }

    private static void agregar(ZipOutputStream zip, String nombre,
                                String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String documento(Factura f) {
        StringBuilder x = new StringBuilder();

        x.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        x.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/"
                + "wordprocessingml/2006/main\"><w:body>");

        /* Encabezado del emisor. */
        x.append(parrafo(texto(FacturaService.EMISOR_NOMBRE, true, 36,
                AZUL_OSCURO), "center", 40));
        x.append(parrafo(texto("Cédula jurídica: "
                        + FacturaService.EMISOR_CEDULA, false, 20, GRIS_TEXTO),
                "center", 0));
        x.append(parrafo(texto(FacturaService.EMISOR_DIRECCION,
                false, 20, GRIS_TEXTO), "center", 0));
        x.append(parrafo(texto("Tel: " + FacturaService.EMISOR_TELEFONO
                        + "   |   " + FacturaService.EMISOR_CORREO,
                false, 20, GRIS_TEXTO), "center", 200));

        /* Título y datos generales. */
        x.append(tabla(new int[]{ANCHO / 2, ANCHO / 2}, new String[][]{{
                celda(ANCHO / 2, AZUL_OSCURO,
                        parrafo(texto("FACTURA", true, 40, "FFFFFF"),
                                "left", 0)),
                celda(ANCHO / 2, AZUL_OSCURO,
                        parrafo(texto("N° " + f.numero(), true, 32, "FFFFFF"),
                                "right", 0))
        }}));
        x.append(parrafo("", "left", 60));

        x.append(tabla(new int[]{ANCHO / 2, ANCHO / 2}, new String[][]{{
                celda(ANCHO / 2, null,
                        etiquetaValor("Fecha de emisión: ",
                                FormatoFactura.fecha(f.fecha()))
                                + etiquetaValor("Condición de venta: ",
                                "Contado")),
                celda(ANCHO / 2, null,
                        etiquetaValor("Medio de pago: ", f.metodoPago())
                                + etiquetaValor("Moneda: ",
                                "Colones costarricenses (CRC)"))
        }}));
        x.append(parrafo("", "left", 120));

        /* Datos del cliente. */
        x.append(parrafo(texto("DATOS DEL CLIENTE", true, 22, AZUL_OSCURO),
                "left", 60));
        x.append(tabla(new int[]{ANCHO / 2, ANCHO / 2}, new String[][]{{
                celda(ANCHO / 2, GRIS_CLARO,
                        etiquetaValor("Nombre: ", f.clienteNombre())
                                + etiquetaValor("Cédula: ",
                                f.clienteCedula())),
                celda(ANCHO / 2, GRIS_CLARO,
                        etiquetaValor("Teléfono: ", f.clienteTelefono())
                                + etiquetaValor("Correo: ",
                                f.clienteCorreo()))
        }}));
        x.append(parrafo("", "left", 120));

        /* Detalle. */
        int[] anchos = {900, 5280, 1950, 1950};

        x.append(parrafo(texto("DETALLE", true, 22, AZUL_OSCURO),
                "left", 60));
        x.append(tabla(anchos, new String[][]{
                {
                        celdaEncabezado(anchos[0], "Cant.", "center"),
                        celdaEncabezado(anchos[1], "Descripción", "left"),
                        celdaEncabezado(anchos[2], "Precio unitario",
                                "right"),
                        celdaEncabezado(anchos[3], "Total", "right")
                },
                {
                        celda(anchos[0], null, parrafo(
                                texto("1", false, 20, null), "center", 0)),
                        celda(anchos[1], null,
                                parrafo(texto("Membresía " + f.planNombre()
                                                + " (" + f.planTipo() + ")",
                                        true, 20, null), "left", 20)
                                        + parrafo(texto("Período: "
                                                + FormatoFactura.fecha(
                                                f.periodoInicio()) + " al "
                                                + FormatoFactura.fecha(
                                                f.periodoFin()),
                                        false, 18, GRIS_TEXTO), "left", 20)
                                        + parrafo(texto("Incluye: "
                                                + f.beneficios(),
                                        false, 18, GRIS_TEXTO), "left", 0)),
                        celda(anchos[2], null, parrafo(texto(
                                FormatoFactura.dinero(f.subtotal()),
                                false, 20, null), "right", 0)),
                        celda(anchos[3], null, parrafo(texto(
                                FormatoFactura.dinero(f.subtotal()),
                                false, 20, null), "right", 0))
                }}));
        x.append(parrafo("", "left", 120));

        /* Totales alineados a la derecha. */
        int[] totales = {ANCHO - 3900, 1950, 1950};

        x.append(tabla(totales, new String[][]{
                {
                        celdaVacia(totales[0]),
                        celda(totales[1], null, parrafo(texto("Subtotal",
                                false, 20, null), "right", 0)),
                        celda(totales[2], null, parrafo(texto(
                                FormatoFactura.dinero(f.subtotal()),
                                false, 20, null), "right", 0))
                },
                {
                        celdaVacia(totales[0]),
                        celda(totales[1], null, parrafo(texto("IVA "
                                        + FormatoFactura.porcentaje(f.tasaIva()),
                                false, 20, null), "right", 0)),
                        celda(totales[2], null, parrafo(texto(
                                FormatoFactura.dinero(f.iva()),
                                false, 20, null), "right", 0))
                },
                {
                        celdaVacia(totales[0]),
                        celda(totales[1], AZUL_OSCURO, parrafo(texto("TOTAL",
                                true, 22, "FFFFFF"), "right", 0)),
                        celda(totales[2], AZUL_OSCURO, parrafo(texto(
                                FormatoFactura.dinero(f.total()),
                                true, 22, "FFFFFF"), "right", 0))
                }}, false));
        x.append(parrafo("", "left", 120));

        x.append(parrafo(texto("Son: ", true, 20, null)
                + texto(FormatoFactura.totalEnLetras(f.total()),
                false, 20, null), "left", 240));

        /* Pie de página. */
        x.append(parrafo(texto("¡Gracias por su preferencia!", true, 22,
                AZUL_OSCURO), "center", 40));
        x.append(parrafo(texto("Documento generado por el Sistema de "
                        + "Control de Membresías del gimnasio.", false, 16,
                GRIS_TEXTO), "center", 0));

        x.append("<w:sectPr><w:pgSz w:w=\"12240\" w:h=\"15840\"/>"
                + "<w:pgMar w:top=\"1080\" w:right=\"1080\" "
                + "w:bottom=\"1080\" w:left=\"1080\" w:header=\"720\" "
                + "w:footer=\"720\" w:gutter=\"0\"/></w:sectPr>");
        x.append("</w:body></w:document>");

        return x.toString();
    }

    /* ---------- Piezas de WordprocessingML ---------- */

    private static String texto(String contenido, boolean negrita,
                                int medioPunto, String color) {
        return "<w:r><w:rPr><w:rFonts w:ascii=\"Calibri\" "
                + "w:hAnsi=\"Calibri\" w:cs=\"Calibri\"/>"
                + (negrita ? "<w:b/>" : "")
                + (color != null ? "<w:color w:val=\"" + color + "\"/>" : "")
                + "<w:sz w:val=\"" + medioPunto + "\"/>"
                + "<w:szCs w:val=\"" + medioPunto + "\"/></w:rPr>"
                + "<w:t xml:space=\"preserve\">" + escapar(contenido)
                + "</w:t></w:r>";
    }

    private static String parrafo(String runs, String alineacion,
                                  int despues) {
        return "<w:p><w:pPr><w:spacing w:before=\"0\" w:after=\"" + despues
                + "\"/><w:jc w:val=\"" + alineacion + "\"/></w:pPr>"
                + runs + "</w:p>";
    }

    private static String etiquetaValor(String etiqueta, String valor) {
        return parrafo(texto(etiqueta, true, 20, null)
                + texto(valor, false, 20, null), "left", 40);
    }

    private static String celda(int ancho, String relleno,
                                String contenido) {
        return "<w:tc><w:tcPr><w:tcW w:w=\"" + ancho
                + "\" w:type=\"dxa\"/>"
                + (relleno != null
                ? "<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\""
                + relleno + "\"/>" : "")
                + "</w:tcPr>" + contenido + "</w:tc>";
    }

    private static String celdaVacia(int ancho) {
        return celda(ancho, null, parrafo("", "left", 0));
    }

    private static String celdaEncabezado(int ancho, String titulo,
                                          String alineacion) {
        return celda(ancho, AZUL_OSCURO, parrafo(
                texto(titulo, true, 20, "FFFFFF"), alineacion, 0));
    }

    private static String tabla(int[] anchos, String[][] filas) {
        return tabla(anchos, filas, true);
    }

    private static String tabla(int[] anchos, String[][] filas,
                                boolean conBordes) {
        String borde = conBordes
                ? "<w:top w:val=\"single\" w:sz=\"4\" w:color=\"BFBFBF\"/>"
                + "<w:left w:val=\"single\" w:sz=\"4\" w:color=\"BFBFBF\"/>"
                + "<w:bottom w:val=\"single\" w:sz=\"4\" w:color=\"BFBFBF\"/>"
                + "<w:right w:val=\"single\" w:sz=\"4\" w:color=\"BFBFBF\"/>"
                + "<w:insideH w:val=\"single\" w:sz=\"4\" w:color=\"BFBFBF\"/>"
                + "<w:insideV w:val=\"single\" w:sz=\"4\" w:color=\"BFBFBF\"/>"
                : "";

        StringBuilder t = new StringBuilder();
        int suma = 0;

        for (int a : anchos) {
            suma += a;
        }

        t.append("<w:tbl><w:tblPr><w:tblW w:w=\"").append(suma)
                .append("\" w:type=\"dxa\"/><w:tblBorders>").append(borde)
                .append("</w:tblBorders><w:tblLayout w:type=\"fixed\"/>"
                        + "<w:tblCellMar><w:top w:w=\"60\" w:type=\"dxa\"/>"
                        + "<w:left w:w=\"110\" w:type=\"dxa\"/>"
                        + "<w:bottom w:w=\"60\" w:type=\"dxa\"/>"
                        + "<w:right w:w=\"110\" w:type=\"dxa\"/>"
                        + "</w:tblCellMar></w:tblPr><w:tblGrid>");

        for (int a : anchos) {
            t.append("<w:gridCol w:w=\"").append(a).append("\"/>");
        }

        t.append("</w:tblGrid>");

        for (String[] fila : filas) {
            t.append("<w:tr>");

            for (String celda : fila) {
                t.append(celda);
            }

            t.append("</w:tr>");
        }

        return t.append("</w:tbl>").toString();
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static final String CONTENT_TYPES =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<Types xmlns=\"http://schemas.openxmlformats.org/"
                    + "package/2006/content-types\">"
                    + "<Default Extension=\"rels\" ContentType=\"application/"
                    + "vnd.openxmlformats-package.relationships+xml\"/>"
                    + "<Default Extension=\"xml\" "
                    + "ContentType=\"application/xml\"/>"
                    + "<Override PartName=\"/word/document.xml\" "
                    + "ContentType=\"application/vnd.openxmlformats-"
                    + "officedocument.wordprocessingml.document.main+xml\"/>"
                    + "</Types>";

    private static final String RELS =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<Relationships xmlns=\"http://schemas.openxmlformats."
                    + "org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas."
                    + "openxmlformats.org/officeDocument/2006/relationships/"
                    + "officeDocument\" Target=\"word/document.xml\"/>"
                    + "</Relationships>";
}
