package Cobranza;
import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import Indicadores.Variables;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;



import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoFacturaViaje {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Variables para almacenar valores reutilizables
    private static String numeroViajeCliente;
    private static String monedaSeleccionada;
    private static Double MontoaPAGAR;

    private static final String NUMERO_CLIENTE = Variables.CLIENTE; // Número de cliente configurable
    private static final String FOLIO_RUTA = Variables.RUTA; // Folio de la ruta configurable
    private static final String TIPO_DOCUMENTO = Variables.DocumentoIngreso;
    // Definir la ruta del archivo en una variable
    private static final String rutaArchivo  = Variables.Docmateriales;




    @BeforeEach
    public void setup() {
        // 🛠️ Obtener el navegador dinámicamente desde la variable del sistema
        String navegador = System.getProperty("navegador", "chrome"); // Si no se especifica, usa Chrome
        System.out.println("🌍 Configurando pruebas en: " + navegador.toUpperCase());

        // 🛠️ Configurar el WebDriver con el navegador correcto
        InicioSesion.setup(navegador);
        driver = InicioSesion.getDriver();
        wait = InicioSesion.getWait();
    }


    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesión - Se utiliza un usuario disponible en la cola")
    public void inicioSesion() {
        InicioSesion.fillForm();   // ✅ Sin parámetros
        InicioSesion.submitForm(); // ✅ Sin parámetros
        InicioSesion.handleAlert(); // ✅ Sin parámetros
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio();       // ✅ Sin parámetros
        InicioSesion.handleNovedadesScreen();  // ✅ Sin parámetros
    }


    @RepeatedTest(2)
    @Order(3)
    @Description("Metodos para entrar al listado de viajes")
    public void EntrarAViajes() throws InterruptedException {
        BotonModuloTrafico(); // Selecciona el módulo de tráfico en la interfaz de usuario.
        BotonListadoViajes(); // Abre el listado de viajes en el módulo de tráfico.
        BotonAgregarCartaPorte(); // Agrega un nuevo 'Carta Porte' para crear un viaje.
        TipoDocumentoIngreso(); // Selecciona el tipo de documento en el formulario del viaje.
        NumeroViajeCliente(); // Introduce el número de viaje correspondiente al cliente.
        CodigoCliente(); // Asigna el cliente correspondiente al viaje.
        MonedaCartaPorte(); // Selecciona aleatoriamente una moneda para el 'Carta Porte'.
        FolioRuta(); // Introduce el folio de la ruta en el formulario del viaje.
        NumeroEconomicoConvoy(); // Introduce el número económico del convoy.
        SeleccionarPestanaMateriales(); // Selecciona la pestaña de materiales de carga.
        BotonImportarMaterial(); // Da clic en el botón de importación de materiales.
        ImportacionMaterial(); // Importa los materiales desde un archivo de Excel.
        BotonAceptarImportacion(); // Acepta el cuadro de diálogo de importación de materiales.
        BotonAceptarViaje(); // Acepta el registro del viaje recién creado.
        BotonConcurrencia(); // Acepta el mensaje de concurrencia si aparece.
        EnvioCorreo(); // Envía un correo de confirmación aleatoriamente (Sí/No).
        BotonImpresion(); // Muestra el mensaje relacionado con la impresión.

        // Bloque donde se controla la facturación del viaje creado
        BotonModuloFacturacion(); // Selecciona el módulo de facturación en la interfaz de usuario.
        BotonFacturacionPorViaje(); //Seleccion Submodulo facturacion por Viaje
        BotonAgregarFactura(); // Da clic en el botón para agregar una nueva factura.
        CodigoClienteFactura(); // Introduce el código del cliente para la factura.
        MonedaAFacturar(); // Selecciona una moneda aleatoria para la factura.
        CampoBusqueda(); // Realiza una búsqueda utilizando el número de viaje.
        FiltroMoneda(); // Aplica un filtro de moneda basado en la moneda previamente seleccionada.
        SelecionaFactura(); // Selecciona la factura generada.
        AceptarFactura(); // Acepta la factura generada.
        AceptarTimbre(); // Acepta el proceso EDI relacionado con la factura.
        EnvioCorreoFactura(); // Envía un correo para la factura generada (Sí/No).
        AceptarPoliza(); // Acepta la póliza generada.
        AceptarImpresion(); // Acepta el cuadro de diálogo de impresión.


        //Bloque donde se controla el pago de la factura
        BotonModuloCobranza(); // Selecciona el módulo de Cobranza en la interfaz de usuario.
        BotonPagosAbonos(); // Seleccion Submodulo pagos/abonos.
        BotonRegistrarPagoAbono(); // Da clic en el boton para agregar pago/abono.
        deseleccionarCampoFecha(); // Deselecciona el campo de fecha.
        CodigoClientPago(); // Introduce el código del cliente para el pago.
        SeleccionarCuentaBancariaAleatoria(); // Selecciona una cuenta bancaria aleatoria para el pago.
        BusquedaFacturaPagar(); // Busca la factura a pagar.
        SeleccionFactura(); // Selecciona la factura a pagar.
        ValidarConversion(); // Validar la conversión de la moneda.
        IntroducirMontoPago(); // Introduce el monto a pagar.
        IntroducirReferencia(); // Introduce una referencia para el pago.
        AceptarPagoAbono(); // Acepta el pago/abono.
        TimbrePago(); // Acepta el timbre del pago.
        EnvioCorreoPago(); // Envía un correo para el pago (Sí/No).
        AceptarPolizaPago(); // Acepta la póliza del pago.
        deseleccionarCampoFecha2(); // Deselecciona el campo de fecha.
        SalirVentanaPago(); // Sale de la ventana de pago.


    }


    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }


    private static void BotonModuloTrafico() {
        try {
            // Espera explícita hasta que el botón (enlace) que contiene la imagen sea clicable
            WebElement ModuloBotonTrafico = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1')]")));

            // Hacer clic en el botón una vez esté listo
            ModuloBotonTrafico.click();

        } catch (Exception e) {
            // Manejo del error utilizando la clase UtilidadesAllure
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    private static void BotonListadoViajes() {
        try {
            // Espera explícita hasta que el enlace que contiene la imagen sea clicable
            WebElement ListadoBoton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.')]]")));

            // Hacer clic en el enlace una vez esté listo
            ListadoBoton.click();

        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Viajes no funciona.");
        }
    }

    private static void BotonAgregarCartaPorte() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Carta Porte no funciona.");
        }
    }

    @Step("Seleccionar Tipo de Documento (variable TIPO_DOCUMENTO)")
    public void TipoDocumentoIngreso() {
        try {
            WebElement comboTipoDoc = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATTIPOSDOCUMENTOS")));
            WebElement opcion = comboTipoDoc.findElement(
                    By.xpath(".//option[text()='" + TIPO_DOCUMENTO + "']"));
            opcion.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "No se pudo seleccionar el Tipo de Documento: " + TIPO_DOCUMENTO);
        }
    }


    @Step("Manejar Número de Viaje")
    private void NumeroViajeCliente() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            String folioValue = folioField.getAttribute("value");
            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            numeroViajeCliente = "PAING" + folioValue; // Guarda el valor en la variable
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViajeCliente);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }

    @Step("Asignar Cliente al Viaje")
    private void CodigoCliente() {
        try {
            WebElement NumeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroCliente.click();
            NumeroCliente.sendKeys(NUMERO_CLIENTE); // Usar la variable NUMERO_CLIENTE
            NumeroCliente.sendKeys(Keys.TAB);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }


    @Step("Manejar Moneda")
    private void MonedaCartaPorte() {
        monedaSeleccionada = ""; // Limpiar la variable al inicio de cada ejecución
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            // Leer y obtener las opciones disponibles en el combo box
            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            Random random = new Random();

            // Seleccionar aleatoriamente una opción
            int indiceAleatorio = random.nextInt(opcionesDisponibles.size());
            WebElement opcionSeleccionada = opcionesDisponibles.get(indiceAleatorio);

            // Usar JavaScript para asegurar que el cambio de moneda se realiza correctamente
            String valorASeleccionar = opcionSeleccionada.getAttribute("value");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'))", tipoDocumentoCombo, valorASeleccionar);

            // Esperar a que el valor del combo box se haya actualizado correctamente
            wait.until(ExpectedConditions.textToBePresentInElement(tipoDocumentoCombo, opcionSeleccionada.getText()));

            // Actualizar el valor de monedaSeleccionada con la opción seleccionada
            monedaSeleccionada = opcionSeleccionada.getText().trim().toUpperCase();
            System.out.println("La moneda seleccionada es: " + monedaSeleccionada);

            // Validar si el cambio de moneda se aplicó correctamente
            String valorActual = comboBox.getFirstSelectedOption().getText().trim().toUpperCase();
            if (!valorActual.equals(monedaSeleccionada)) {
                throw new Exception("Error: no se pudo cambiar la moneda. Valor actual sigue siendo: " + valorActual);
            }
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda en MonedaCartaPorte");
            System.out.println("Error al manejar la Moneda en MonedaCartaPorte: " + e.getMessage());
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al cambiar la moneda en MonedaCartaPorte");
            System.out.println("Error al cambiar la moneda en MonedaCartaPorte: " + e.getMessage());
        }
    }


    @Step("Manejar Folio Ruta")
    private void FolioRuta() {
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            folioRutaField.click();
            folioRutaField.sendKeys(FOLIO_RUTA); // Usar la variable FOLIO_RUTA
            folioRutaField.sendKeys(Keys.TAB);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Folio Ruta");
        }
    }


    @Step("Manejar Número Económico Convoy")
    private void NumeroEconomicoConvoy() {
        try {
            WebElement numeroEconomicoConvoyField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            numeroEconomicoConvoyField.sendKeys("E3");
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER);
            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número Económico del Convoy");
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales() {
        try {
            // Espera a que el elemento <td> que contiene la pestaña esté visible
            WebElement pestanaMateriales = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));

            // Busca el elemento <a> dentro de la pestaña y haz clic en él
            WebElement linkPestana = pestanaMateriales.findElement(By.tagName("a"));
            linkPestana.click();

            Thread.sleep(1000); // Reducido para optimizar
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    @Step("Manejar Botón Importar Materiales")
    private void BotonImportarMaterial() {
        try {
            // Espera a que el botón sea clicable
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES")));

            // Hacer clic en el botón una vez esté listo
            botonImportar.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Importar Materiales");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {


        try {
            // Espera a que el campo de archivo sea visible
            WebElement inputArchivo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));

            // Verificar si el archivo existe
            File archivo = new File(rutaArchivo);
            if (archivo.exists()) {
                // Enviar la ruta del archivo al campo de tipo "file"
                inputArchivo.sendKeys(rutaArchivo);
            } else {
                throw new Exception("El archivo especificado no existe: " + rutaArchivo);
            }

            // Espera a que el botón de importar sea clicable
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_IMPORTARMATERIALES_IMPORTAR")));

            // Hacer clic en el botón de importar
            botonImportar.click();

            // Esperar unos 3 segundos después de hacer clic
            Thread.sleep(3000);

            // Verificar si la importación fue exitosa o falló
            try {
                WebElement iconoExito = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_ICONO_EXITO")));
                if (iconoExito.isDisplayed()) {
                    System.out.println("Importación de materiales exitosa.");
                }
            } catch (TimeoutException e) {
                WebElement iconoError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("IMG_IMPORTARMATERIALES_ERROR")));
                if (iconoError.isDisplayed()) {
                    UtilidadesAllure.capturaImagen(driver); // Toma captura de pantalla en caso de error
                    throw new Exception("La importación de materiales falló, se mostró el icono de error.");
                }
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al importar el archivo de materiales");
        }
    }


    @Step("Aceptar Importación de Materiales")
    private void BotonAceptarImportacion() {
        try {
            // Espera a que el botón de aceptar sea clicable
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));

            // Hacer clic en el botón de aceptar
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar después de la importación de materiales");
        }
    }

    @Step("Aceptar Viaje")
    private void BotonAceptarViaje() {
        try {
            // Espera a que el botón de aceptar viaje sea clicable
            WebElement botonAceptarViaje = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));

            // Hacer clic en el botón de aceptar viaje
            botonAceptarViaje.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Aceptar para confirmar el viaje");
        }
    }

    @Step("Aceptar mensaje de concurrencia si aparece")
    private void BotonConcurrencia() {
        try {
            // Esperar unos segundos para ver si aparece el mensaje de concurrencia
            WebElement botonAceptarConcurrencia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")));

            // Si el botón está disponible, hacer clic en él
            botonAceptarConcurrencia.click();
            System.out.println("Mensaje de concurrencia detectado y aceptado.");

            // Llamar a los métodos que deben repetirse
            NumeroViajeCliente();
            BotonAceptarViaje();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    @Step("Enviar Por Correo")
    private void EnvioCorreo() {
        try {
            // Elegir aleatoriamente entre el botón Sí o No
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                // Espera a que el botón de Sí sea clicable
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo.");
            } else {
                // Espera a que el botón de No sea clicable
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo.");
            }

            // Hacer clic en el botón elegido
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    @Step("Botón de Impresión")
    private void BotonImpresion() {
        try {
            // Espera a que el botón de regresar sea clicable
            WebElement botonRegresar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_REGRESAR")));

            // Hacer clic en el botón de regresar
            botonRegresar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Regresar para impresión");
        }
    }

    private static void BotonModuloFacturacion() {
        try {
            // Espera explícita hasta que el botón que contiene la imagen de facturación sea clicable
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1')]")));

            // Hacer clic en el botón una vez esté listo
            imageButton.click();
        } catch (Exception e) {
            // Manejo general de excepciones
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }


    private static void BotonFacturacionPorViaje() {
        try {
            // Espera explícita hasta que el botón que contiene la imagen de facturación por viaje sea clicable
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORVIAJE1')]")));

            // Hacer clic en el botón una vez esté listo
            subMenuButton.click();
        } catch (Exception e) {
            // Manejo general de excepciones
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Viaje no funciona.");
            System.out.println("Botón listado Facturas por Viaje no funciona.");
        }
    }

    private static void BotonAgregarFactura() {
        try {
            // Espera explícita hasta que el botón "Agregar" sea clicable
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_AGREGAR")));

            // Hacer clic en el botón una vez esté listo
            addButton.click();
        } catch (Exception e) {
            // Manejo general de excepciones
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Factura no funciona.");
            System.out.println("Botón Agregar Factura no funciona.");
        }
    }

    private static void CodigoClienteFactura() {
        try {
            WebElement clienteField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            clienteField.clear();
            Thread.sleep(500);
            clienteField.sendKeys(NUMERO_CLIENTE); // Usar la variable NUMERO_CLIENTE
            Thread.sleep(200);
            clienteField.sendKeys(Keys.TAB);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al llenar el campo del número de cliente para la factura.");
        }
    }


    private static void MonedaAFacturar() {
        try {
            // Espera a que el combo box de moneda sea visible
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            // Obtener todas las opciones disponibles
            List<WebElement> opcionesDisponibles = comboBox.getOptions();

            // Definir las opciones válidas según el texto visible
            List<String> opcionesValidas = List.of("PESOS", "DÓLARES");
            List<WebElement> opcionesValidasDisponibles = new ArrayList<>();

            // Filtrar las opciones disponibles para casar solo las válidas
            for (WebElement opcion : opcionesDisponibles) {
                if (opcionesValidas.contains(opcion.getText())) {
                    opcionesValidasDisponibles.add(opcion);
                }
            }

            // Elegir una opción aleatoria del combo box entre las opciones válidas
            if (!opcionesValidasDisponibles.isEmpty()) {
                Random random = new Random();
                WebElement opcionAleatoria = opcionesValidasDisponibles.get(random.nextInt(opcionesValidasDisponibles.size()));

                // Seleccionar la opción aleatoria utilizando el texto visible
                comboBox.selectByVisibleText(opcionAleatoria.getText());
                System.out.println("Se seleccionó la opción moneda a facturar: " + opcionAleatoria.getText());
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda a Facturar");
            System.out.println("Error al manejar la Moneda a Facturar");
        }
    }


    private static void CampoBusqueda() {
        try {
            // Espera explícita hasta que el campo de búsqueda sea visible
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_BUSQUEDAGENERAL")));

            // Llenar el campo con el número de viaje generado
            busquedaField.clear();
            busquedaField.sendKeys(numeroViajeCliente);
            System.out.println("Se ingresó el valor de búsqueda: " + numeroViajeCliente);

            // Espera explícita hasta que el botón de búsqueda sea clicable
            WebElement buscarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_BUSCAR")));

            // Hacer clic en el botón de búsqueda
            buscarButton.click();
            System.out.println("Se presionó el botón de búsqueda");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el valor en el campo de búsqueda general o al presionar el botón de búsqueda");
            System.out.println("Error al ingresar el valor en el campo de búsqueda general o al presionar el botón de búsqueda");
        }
    }


    private static void FiltroMoneda() {
        try {
            WebElement filtroMonedaCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS_FILTRO")));
            Select comboBox = new Select(filtroMonedaCombo);

            String valorInicialComboBox = comboBox.getFirstSelectedOption().getText().trim().toUpperCase();
            System.out.println("Valor actual del filtro de moneda leído: " + valorInicialComboBox);

            if (!valorInicialComboBox.equals(monedaSeleccionada)) {
                comboBox.selectByVisibleText(monedaSeleccionada);
                wait.until(ExpectedConditions.textToBePresentInElement(filtroMonedaCombo, monedaSeleccionada));
                System.out.println("Se seleccionó la opción de moneda en el filtro: " + monedaSeleccionada);
            } else {
                System.out.println("El valor inicial del filtro de moneda ya coincide con el valor deseado: " + monedaSeleccionada);
            }
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el filtro de Moneda");
            System.out.println("Error al manejar el filtro de Moneda: " + e.getMessage());
        }
    }

    private static void SelecionaFactura() {
        try {
            // Espera explícita hasta que el checkbox de la factura sea visible y clicable
            WebElement facturaCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("_0_TABLE_PROVIAJES_0")));

            // Seleccionar el checkbox de la factura
            if (!facturaCheckbox.isSelected()) {
                facturaCheckbox.click();
            }
            System.out.println("Se seleccionó la factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la factura");
            System.out.println("Error al seleccionar la factura");
        }
    }

    private static void AceptarFactura() {
        int intentos = 0;
        boolean exito = false;
        while (intentos < 3 && !exito) {
            try {
                WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("z_BTN_ACEPTAR_IMG")));
                aceptarButton.click();
                System.out.println("Se presionó el botón de aceptar factura en el intento " + (intentos + 1));
                exito = true;
            } catch (Exception e) {
                intentos++;
                if (intentos == 3) {
                    UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar factura");
                    System.out.println("Error al presionar el botón de aceptar factura después de 3 intentos");
                }
            }
        }
    }

    private static void AceptarTimbre() {
        try {
            // Espera explícita hasta que el botón de aceptar sea clicable usando el nuevo ID "BTN_YES"
            WebElement aceptarEDIButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));

            // Hacer clic en el botón de aceptar
            aceptarEDIButton.click();
            System.out.println("Se presionó el botón de aceptar timbre");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar timbre");
            System.out.println("Error al presionar el botón de aceptar timbre");
        }
    }


    private static void EnvioCorreoFactura() {
        try {
            // Elegir aleatoriamente entre el botón Sí o No
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                // Espera a que el botón de Sí sea clicable
                boton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("BTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo.");
            } else {
                // Espera a que el botón de No sea clicable
                boton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("BTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo.");
            }

            // Hacer clic en el botón elegido
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo de timbre");
            System.out.println("Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    private static void AceptarPoliza() {
        try {
            // Espera explícita hasta que el botón de aceptar póliza sea clicable
            WebElement aceptarPolizaButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_OK")));

            // Hacer clic en el botón de aceptar póliza
            aceptarPolizaButton.click();
            System.out.println("Se presionó el botón de aceptar póliza");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar póliza");
            System.out.println("Error al presionar el botón de aceptar póliza");
        }
    }

    private static void AceptarImpresion() {
        try {
            // Espera explícita hasta que el botón de regresar sea clicable
            WebElement regresarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_REGRESAR")));

            // Hacer clic en el botón de regresar
            regresarButton.click();
            System.out.println("Se presionó el botón de regresar después de la impresión");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de regresar después de la impresión");
            System.out.println("Error al presionar el botón de regresar después de la impresión");
        }
    }


    public void BotonModuloCobranza() {
        try {
            // Espera explícita hasta que el botón (imagen) de Cobranza sea clicable
            WebElement ModuloBotonCobranza = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA1.jpg')]")
                    )
            );

            // Hacer clic en el botón una vez esté listo
            ModuloBotonCobranza.click();
            System.out.println("Botón Módulo Cobranza presionado correctamente.");

        } catch (Exception e) {
            // Manejo del error con captura de pantalla
            UtilidadesAllure.manejoError(driver, e, " Botón Módulo Cobranza no funciona.");
        }
    }


    public void BotonPagosAbonos() {
        try {
            // Espera explícita hasta que el botón de Pagos/Abonos sea clicable
            WebElement pagosAbonosBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA/PAGOSABONOS1.jpg')]")));

            // Hacer clic en el botón
            pagosAbonosBoton.click();
            System.out.println("Submódulo Pagos/Abonos seleccionado correctamente.");
        } catch (Exception e) {
            // Manejo del error con captura de pantalla
            UtilidadesAllure.manejoError(driver, e, "Botón Pagos/Abonos no funciona.");
        }
    }

    public void BotonRegistrarPagoAbono() {
        try {
            // Espera a que la opción principal "Registrar" en el menú sea clickeable
            WebElement menuRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("OPT_REGISTRARMENU")));

            // Hacer clic en la opción "Registrar" para desplegar el submenú
            menuRegistrar.click();
            System.out.println("Menú 'Registrar' abierto.");

            // Espera a que la opción específica "Registrar" dentro del submenú sea clickeable
            WebElement opcionRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("OPT_REGISTRAR")));

            // Hacer clic en la opción "Registrar"
            opcionRegistrar.click();
            System.out.println("Opción 'Registrar' seleccionada correctamente.");

        } catch (Exception e) {
            // Manejo del error con captura de pantalla
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Registrar'.");
        }

    }


    public void deseleccionarCampoFecha() {
        try {
            WebElement body = driver.findElement(By.tagName("body")); // Clic en el fondo de la página
            body.click();
            body.click();
            System.out.println("✅ Campo EDT_FECHA deseleccionado con un clic en el body.");
        } catch (Exception e) {
            System.err.println("⚠ No se pudo deseleccionar el campo EDT_FECHA: " + e.getMessage());
        }
    }


    @Step("Asignar Cliente al Pago")
    private void CodigoClientPago() {
        try {
            WebElement NumeroClientePago = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroClientePago.click();
            NumeroClientePago.sendKeys(NUMERO_CLIENTE);
            NumeroClientePago.sendKeys(Keys.TAB);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al pago");
        }
    }

    @Step("Seleccionar una cuenta bancaria aleatoria")
    public void SeleccionarCuentaBancariaAleatoria() {
        try {
            // Localizar el combo box usando XPath
            WebElement comboBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[contains(@class, 'COMBO_CATCUENTASBANCARIAS')]")));
            // Instanciar el objeto Select para manipular el dropdown
            Select comboBox = new Select(comboBoxElement);
            // Obtener todas las opciones disponibles en el combo box
            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            // Verificar si hay opciones disponibles
            if (opcionesDisponibles.size() > 1) { // Excluye la primera opción si es un placeholder
                // Generar un índice aleatorio para seleccionar una opción válida (excluyendo la primera)
                Random random = new Random();
                int indiceAleatorio = random.nextInt(opcionesDisponibles.size() - 1) + 1; // Evita seleccionar la opción 0 si es vacía
                // Obtener la opción seleccionada
                WebElement opcionSeleccionada = opcionesDisponibles.get(indiceAleatorio);
                String textoSeleccionado = opcionSeleccionada.getText();
                // Seleccionar la opción aleatoria por índice
                comboBox.selectByIndex(indiceAleatorio);
                System.out.println("Cuenta bancaria seleccionada: " + textoSeleccionado);
            } else {
                System.out.println("No hay cuentas bancarias disponibles para seleccionar.");
            }
        } catch (TimeoutException | NoSuchElementException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una cuenta bancaria aleatoria.");
            System.out.println("Error al seleccionar una cuenta bancaria aleatoria.");
        }
    }



    @Step("Buscar factura para pagar")
    public void BusquedaFacturaPagar() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {

            // Localizar el campo de búsqueda usando XPath en lugar de name
            WebElement campoBusqueda = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@class, 'EDT_BUSQUEDAGENERAL')]")));
            campoBusqueda.click();

            // Limpiar el campo antes de ingresar el número de viaje
            campoBusqueda.clear();

            // Ingresar el número de viaje del cliente
            campoBusqueda.sendKeys(numeroViajeCliente);

            System.out.println("Número de viaje ingresado en búsqueda: " + numeroViajeCliente);
        } catch (Exception e) {
            System.out.println("Error en la búsqueda de factura: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error en la búsqueda de factura.");
        }
    }



    public void SeleccionFactura() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        try {
            // Espera explícita hasta que el checkbox de la factura sea visible y clicable
            WebElement facturaCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("_0_TABLE_PROFACTURAS_0")));

            // Seleccionar el checkbox de la factura
            if (!facturaCheckbox.isSelected()) {
                facturaCheckbox.click();
            }
            System.out.println("Se seleccionó la factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la factura");
            System.out.println("Error al seleccionar la factura");
        }
    }

    @Step("Validar moneda y calcular el monto a pagar")
    public void ValidarConversion() {
        try {
            // Obtener la moneda seleccionada
            WebElement monedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tzSTC_MONEDA")));
            String monedaSeleccionada = monedaElement.getText().trim().toUpperCase();
            System.out.println("Moneda detectada: " + monedaSeleccionada);
            // Obtener valores de los campos relevantes
            WebElement montoPesosElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEPESOS")));
            WebElement montoDolaresElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEDLLS")));
            WebElement tipoCambioElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TIPOCAMBIO")));
            WebElement conversionMonedaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tzSTC_CONVERSIONDEMONEDA")));
            // Obtener valores y limpiar caracteres extraños
            String valorPesos = montoPesosElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorDolares = montoDolaresElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorTipoCambio = tipoCambioElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String textoConversion = conversionMonedaElement.getText().trim();
            // Extraer número de tzSTC_CONVERSIONDEMONEDA
            double conversionAutomatica = 0.0;
            Pattern pattern = Pattern.compile("\\$([0-9]+\\.?[0-9]*)");
            Matcher matcher = pattern.matcher(textoConversion);
            if (matcher.find()) {
                conversionAutomatica = Double.parseDouble(matcher.group(1));
            }
            // Convertir valores a double
            double montoPesos = (!valorPesos.isEmpty() && !valorPesos.equals("0.00")) ? Double.parseDouble(valorPesos) : 0.0;
            double montoDolares = (!valorDolares.isEmpty() && !valorDolares.equals("0.00")) ? Double.parseDouble(valorDolares) : 0.0;
            double tipoCambio = (!valorTipoCambio.isEmpty() && !valorTipoCambio.equals("0.00")) ? Double.parseDouble(valorTipoCambio) : 1.0;
            // Lógica de cálculo basada en la moneda detectada
            if (monedaSeleccionada.equals("PESOS")) {
                if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos;
                } else if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares * tipoCambio;
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica * tipoCambio;
                } else {
                    System.out.println("Error: No hay monto válido en PESOS ni en DÓLARES.");
                    UtilidadesAllure.manejoError(driver, new Exception("Monto no válido"), "No hay monto ingresado.");
                }
            } else if (monedaSeleccionada.equals("DÓLARES")) {
                if (montoDolares > 0) {
                    MontoaPAGAR = montoDolares;
                } else if (montoPesos > 0) {
                    MontoaPAGAR = montoPesos / tipoCambio;
                } else if (conversionAutomatica > 0) {
                    MontoaPAGAR = conversionAutomatica;
                } else {
                    System.out.println("Error: No hay monto válido en PESOS ni en DÓLARES.");
                    UtilidadesAllure.manejoError(driver, new Exception("Monto no válido"), "No hay monto ingresado.");
                }
            } else {
                System.out.println("Error: Moneda no reconocida.");
                UtilidadesAllure.manejoError(driver, new Exception("Moneda no válida"), "Error al validar la moneda.");
            }
            System.out.println("Monto calculado: " + String.format(Locale.US, "%.2f", MontoaPAGAR));
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al obtener la moneda y calcular el monto.");
            System.out.println("Error al validar la moneda y calcular el monto.");
        }
    }

    @Step("Introducir monto calculado en el campo de pago")
    public void IntroducirMontoPago() {
        if (MontoaPAGAR == 0.0) {
            System.out.println("Advertencia: El monto a pagar es inválido o cero, no se ingresará en el campo.");
            return;
        }

        try {
            WebElement campoImporte = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTE")));
            campoImporte.clear();
            String montoFormateado = String.format(Locale.US, "%.2f", MontoaPAGAR);
            campoImporte.sendKeys(montoFormateado);
            System.out.println("Monto ingresado en el campo de pago: " + montoFormateado);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el monto en el campo de pago.");
            System.out.println("Error al encontrar o ingresar el monto en el campo de pago.");
        }
    }

    @Step("Introducir referencia bancaria")
    public void IntroducirReferencia() {
        try {
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("EDT_REFERENCIABANCARIA")));
            campoReferencia.clear();
            String referencia = "pago" + numeroViajeCliente;
            campoReferencia.sendKeys(referencia);
            System.out.println("Referencia ingresada: " + referencia);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la referencia bancaria.");
            System.out.println("Error al ingresar la referencia bancaria.");
        }
    }

    @Step("Aceptar Pago o Abono")
    public void AceptarPagoAbono() {
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_ACEPTAR')]")));
            btnAceptar.click();
            System.out.println("Pago/Abono aceptado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el pago o abono.");
            System.out.println("Error al aceptar el pago o abono.");
        }
    }

    @Step("Generar Timbre del Pago")
    public void TimbrePago() {
        try {
            WebElement btnSi = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN_YES')]")));
            btnSi.click();
            System.out.println("Timbre del pago generado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al generar el timbre del pago.");
            System.out.println("Error al generar el timbre del pago.");
        }
    }

    @Step("Enviar Comprobante de Pago por Correo")
    public void EnvioCorreoPago() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> botones = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@onclick]")));
            List<WebElement> botonesValidos = botones.stream()
                    .filter(b -> b.isDisplayed() && b.isEnabled())
                    .collect(Collectors.toList());
            if (botonesValidos.isEmpty()) {
                System.out.println("No hay botones visibles y clickeables. Continuando...");
                return;
            }
            System.out.println("Botones válidos encontrados:");
            for (WebElement boton : botonesValidos) {
                // Se pueden agregar detalles adicionales si es necesario
            }
            WebElement botonSeleccionado = botonesValidos.get(new Random().nextInt(botonesValidos.size()));
            try {
                System.out.println("Se hizo clic en el botón con ID: " + botonSeleccionado.getAttribute("id"));
                botonSeleccionado.click();
            } catch (Exception e) {
                System.out.println("Click() falló, intentando con JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonSeleccionado);
                System.out.println("Click ejecutado con JavaScript en el botón con ID: " + botonSeleccionado.getAttribute("id"));
            }
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón. Continuando...");
            e.printStackTrace();
        }
    }

    @Step("Aceptar Póliza Contable del Pago")
    public void AceptarPolizaPago() {
        try {
            // Localizar el botón de aceptar póliza usando el nuevo XPath
            WebElement btnAceptar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input"));

            // Hacer clic en el botón
            btnAceptar.click();
            System.out.println("Póliza contable aceptada.");

        } catch (Exception e) {
            System.err.println("Error al aceptar la póliza: " + e.getMessage());
        }
    }

    public void deseleccionarCampoFecha2() {
        try {
            WebElement body = driver.findElement(By.tagName("body"));
            body.click();
            body.click();
            System.out.println("Campo EDT_FECHA deseleccionado con un clic en el body.");
        } catch (Exception e) {
            System.err.println("No se pudo deseleccionar el campo EDT_FECHA: " + e.getMessage());
        }
    }

    @Step("Salir de la Ventana de Pago")
    public void SalirVentanaPago() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class, 'BTN-BotonClasicoGris BTN_CANCELAR')]"))).click();
            System.out.println("Salida de la ventana realizada.");
        } catch (Exception e) {
            System.err.println("Error al salir de la ventana: " + e.getMessage());
        }
    }
}


