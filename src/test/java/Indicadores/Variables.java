package Indicadores;

public class Variables {

    // Variables de configuración para la BD de CACX7605101P8
    public static final String RFC = "CACX7605101P8"; // RFC de prueba
    public static final String CLIENTE = "001430"; // Número de cliente configurable
    public static final String RUTA = "201048";      // Folio de la ruta configurable
    public static final String DocumentoIngreso = "CARTA PORTE CFDI - CCP"; // Número de documento de traslado
    public static final String DocumentoTraslado = "CARTA PORTE CFDI"; // Número de documento de traslado
    public static final String Operador = "000001"; // Número de operador
    public static final String PROVEEDOR = "1"; // Número de proveedor
    public static String numeroDocumentoGenerado; //Número de documento para Pagar Pasivos
    public static String DocumentoGeneradoPasivo; // Número de documento para Contra Recibo y Pagar Contra Recibos
    public static String Facturas;// Variable para almancenar Documento + Folio de Factura.
    public static String Moneda;// Variable para almancenar la Moneda de la Factura.
    public static final String BENEFICIARIO = "18"; //Número de Beneficiarios
    public static String FolioMovimientoBancario;//Variable para almacenar Folio de Movimiento Bancario
    public static String Total; //Variable para almacenar el Total

/*
   //.ariables de configuración para la BD de IIA040805DZ4
    public static final String RFC = "IIA040805DZ4"; // RFC de prueba
    public static final String CLIENTE = "000003"; // Número de cliente configurable
    public static final String RUTA = "000089";      // Folio de la ruta configurable
    public static final String DocumentoIngreso = "CARTA PORTE CFDI - CP"; // Número de documento de traslado
    public static final String DocumentoTraslado = "CARTA PORTE CFDI - TR"; // Número de documento de traslado
    public static final String Operador = "024518"; // Número de operador
    public static final String PROVEEDOR = "1"; // Número de proveedor
    public static String numeroDocumentoGenerado; //Número de documento para Pagar Pasivos
    public static String DocumentoGeneradoPasivo; // Número de documento para Contra Recibo y Pagar Contra Recibos
    public static String Facturas;// Variable para almancenar Documento + Folio de Factura.
    public static String Moneda;// Variable para almancenar la Moneda de la Factura.
    public static final String BENEFICIARIO = "18"; //Número de Beneficiarios
    public static String FolioMovimientoBancario;//Variable para almacenar Folio de Movimiento Bancario
    public static String Total; //Variable para almacenar el Total
*/
    // Variables de configuración para la BD de KIJ0906199R1 o TST080808000
//    public static final String RFC = "TST080808000"; // RFC de prueba
//    public static final String CLIENTE = "000001"; // Número de cliente configurable
//    public static final String RUTA = "000004";      // Folio de la ruta configurable
//    public static final String DocumentoIngreso = "CARTA PORTE CFDI - CCP"; // Número de documento de traslado
//    public static final String DocumentoTraslado = "CARTA PORTE CFDI - CCPTT"; // Número de documento de traslado
//    public static final String Operador = "000006"; // Número de operado
//    public static final String PROVEEDOR = "1"; // Número de proveedor

    // Variables de configuración para la BD de TST040404000 Paquetería PSG
//    public static final String RFC = "TST040404000"; // RFC de prueba
//    public static final String CLIENTE = "014878"; // Número de cliente configurable
//    public static final String RUTA = "003275";      // Folio de la ruta configurable
//    public static final String DocumentoIngreso = "CARTA PORTE CFDI - GMT"; // Número de documento de traslado
//    public static final String DocumentoTraslado = "CARTA PORTE CFDI - CCPTA"; // Número de documento de traslado
//    public static final String Operador = "000020"; // Número de operador
//    public static final String PROVEEDOR = "1"; // Número de proveedor

    // Variables importacion archivos.
    public static final String Docmateriales = "C:\\RepositorioPrueAuto\\XLSXPruebas\\ImportarMaterialesPA.xlsx";
}
