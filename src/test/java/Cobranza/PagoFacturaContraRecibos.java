package Cobranza;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoFacturaContraRecibos {

    private static WebDriver driver;
    private static WebDriverWait wait;
    // Se crean variables para almacenar la información concatenada de las Facturas y mostrarlas en el reporte de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();

    private static String FolioFactura; // Variable para almacenar el valor
    private static String FolioCR; // Número de cliente configurable

    private static List<String> Facturas = new ArrayList<>();

    private static final String NUMERO_CLIENTE = Variables.CLIENTE; // Número de cliente configurable
    private double MontoaPAGAR;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize(); // Maximizar la ventana para evitar problemas de visibilidad
        driver.get("https://www.softwareparatransporte.com/GMTERPV8_WEB/ES/PAGE_CatUsuariosLoginAWP.awp");
    }

    @Test
    @Order(1)
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm(driver);
        InicioSesion.submitForm(wait);
        InicioSesion.handleAlert(wait);
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @RepeatedTest(1)
    @Order(4)
    @Description("Se genera una factura con conceptos aleatorios")
    public void FacturacionporConcepto() throws InterruptedException {
        // Limpia variables de Allure para los reportes.
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);

        // Comienza el flujo de facturación
        //Se hacen 2 facturas
        for (int i = 0; i < 2; i++) {
            handleImageButton();
            handleSubMenuButton();
            BotonAgregarListado();
            CodigoClienteFactura();
            MonedaFactura();
            ConceptofacturacionAgregar(); // Abre el formulario de facturación de concepto
            IngresaValorCantidad(); // Ingresa la cantidad
            AsignarCodigoConceptoFacturacion(); // Aquí le pasas el código único
            IngresaPrecioUnitario(); // Ingresa el precio unitario
            BotonAgregarConcepto(); // Agrega el concepto
            ObtenerFolioFactura(); // Captura el folio de la factura
            AceptarFactura(); // Acepta la factura
            BotonConcurrenciaFactura();
            BotonTimbre(); // Timbrar la factura
            ValidarYEnviarCorreo(); // Validar posibles errores
            BotonPoliza(); // Aceptar botón generó póliza
            BotonImpresion(); // Regresar a la pantalla principal
            i++;
        }
        /*
        handleImageButton();
        handleSubMenuButton();
        BotonAgregarListado();
        CodigoClienteFactura();
        MonedaFactura();
        ConceptofacturacionAgregar(); // Abre el formulario de facturación de concepto
        IngresaValorCantidad(); // Ingresa la cantidad
        AsignarCodigoConceptoFacturacion(); // Aquí le pasas el código único
        IngresaPrecioUnitario(); // Ingresa el precio unitario
        BotonAgregarConcepto(); // Agrega el concepto
        ObtenerFolioFactura(); // Captura el folio de la factura
        AceptarFactura(); // Acepta la factura
        BotonConcurrenciaFactura();
        BotonTimbre(); // Timbrar la factura
        ValidarYEnviarCorreo(); // Validar posibles errores
        BotonPoliza(); // Aceptar botón generó póliza
        BotonImpresion(); // Regresar a la pantalla principal
        */


       // Crea del contra recibo de las facturas que se crearon
        BotonModuloCobranza(); // Selecciona el módulo de Cobranza en la interfaz de usuario.
        BotonContraRecibos(); // Selecciona el submódulo de Contra Recibos.
        BotonAgregarContraRecibo(); // Da clic en el botón para agregar un contra recibo.
        CodigoClientCR();  // Introduce el código del cliente en contra recibo.
        ObtenerFolioCR();  // Obtiene el folio del contra recibo.
        IntroducirReferenciaCR();  // Introduce una referencia del contra recibo.
        DeseleccionarTodoCR();  // Deselecciona todas las facturas del cliente en contra recibo.
        SeleccionarFacturasCR(); // Busca y selecciona las facturas creadas y las agrega al contra recibo.
        AceptarAgregarCR(); // Acepta la adición de las facturas al contra recibo.

      /*  // Bloque donde se controla el pago de la factura
        BotonModuloCobranza(); // Selecciona el módulo de Cobranza en la interfaz de usuario.
        BotonPagosAbonos(); // Selecciona el submódulo pagos/abonos.
        BotonRegistrarPagoAbono(); // Da clic en el botón para agregar pago/abono.
        deseleccionarCampoFecha(); // Deselecciona el campo de fecha.
        CodigoClientPago(); // Introduce el código del cliente para el pago.
        SeleccionarCuentaBancariaAleatoria(); // Selecciona una cuenta bancaria aleatoria para el pago.
        BusquedaFacturaPagar(); // Busca la factura a pagar.
        SeleccionFactura(); // Selecciona la factura a pagar.
        ValidarConversion(); // Se valida la conversión de la moneda.
        IntroducirMontoPago(); // Introduce el monto a pagar.
        IntroducirReferencia(); // Introduce una referencia para el pago.
        AceptarPagoAbono(); // Acepta el pago/abono.
        TimbrePago(); // Acepta el timbre del pago.
        EnvioCorreoPago(); // Envía un correo para el pago (Sí/No).
        AceptarPolizaPago(); // Acepta la póliza del pago.
        deseleccionarCampoFecha2(); // Deselecciona el campo de fecha.
        SalirVentanaPago(); // Sale de la ventana de pago.
       */
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1')]")));
            subMenuButton.click();
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Facturas por Concepto no funciona.");
            System.out.println("Botón listado Facturas por Concepto no funciona.");
        }
    }

    private static void BotonAgregarListado() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón agregar no encontrado o no clickeable.");
            System.out.println("Botón agregar no encontrado o no clickeable.");
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

    private static void MonedaFactura() {
        try {
            // Encuentra el primer combo box (select) por ID
            Select primerComboBox = new Select(driver.findElement(By.id("COMBO_CATMONEDAS")));

            // Define las opciones disponibles
            List<String> opciones = List.of("PESOS", "DÓLARES");

            // Elige aleatoriamente una opción
            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));

            // Selecciona la opción en el primer combo box
            primerComboBox.selectByVisibleText(opcionSeleccionada);

            // Imprime la opción seleccionada
            System.out.println("La Moneda es: " + opcionSeleccionada);
            informacionFactura.append("Moneda: ").append(opcionSeleccionada).append("\n\n");

            // Agrega al reporte de Allure la información de la factura generada.
            Allure.addAttachment("Informacion Factura", informacionFactura.toString());
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error: " + e.getMessage());
            System.out.println("Se ha producido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ConceptofacturacionAgregar() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // Espera explícita hasta que el botón sea clicable
            WebElement botonAgregar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            botonAgregar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón Agregar: " + e.getMessage());
            System.out.println("Error al presionar el botón Agregar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void IngresaValorCantidad() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement nuevoCampo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_CANTIDAD")));
            nuevoCampo.click();

            Random random = new Random();
            double valorAleatorio = 1.0000 + (99.9999 - 1.0000) * random.nextDouble();

            nuevoCampo.sendKeys(String.format("%.4f", valorAleatorio));
            informacionConcepto.append("Cantidad del Concepto: ").append(valorAleatorio).append("\n");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la cantidad: " + e.getMessage());
            System.out.println("Error al ingresar la cantidad: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void AsignarCodigoConceptoFacturacion() {
        try {
            Thread.sleep(1000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement campoCodigo = driver.findElement(By.id("EDT_CODIGOCONCEPTOFACTURACION"));
            WebElement campoConcepto = driver.findElement(By.id("EDT_CONCEPTOFACTURACION"));

            int intentos = 0;

            // Intentar diferentes valores entre 1 y 10
            while (intentos < 10) {
                intentos++;

                // Generar el valor entre 1 y 10
                int codigoConcepto = intentos;

                // Limpiar el campo y asignar el valor
                campoCodigo.clear();
                campoCodigo.sendKeys(String.valueOf(codigoConcepto));
                campoCodigo.sendKeys(Keys.TAB); // Cambiar de foco para activar validaciones

                // Esperar hasta que el campo de concepto esté visible
                wait.until(ExpectedConditions.attributeToBeNotEmpty(campoConcepto, "value"));
                String valorConcepto = campoConcepto.getAttribute("value");

                if (valorConcepto != null && !valorConcepto.equals("El concepto de facturación no está activo, Revisar")) {
                    System.out.println("Valor válido encontrado: " + codigoConcepto);
                    System.out.println("El concepto de facturación es: " + valorConcepto);

                    // Se agrega información al reporte de Allure
                    informacionConcepto.append("Número Concepto: ").append(codigoConcepto).append("\n");
                    informacionConcepto.append("Nombre Concepto: ").append(valorConcepto).append("\n");

                    // Retornar y detener el flujo
                    return;
                } else {
                    System.out.println("Intento " + intentos + ": Valor " + codigoConcepto + " produjo el mensaje de error.");
                }
            }

            // Si no se encuentra un valor válido después de 10 intentos
            System.out.println("No se encontró un valor válido después de 10 intentos.");
            informacionConcepto.append("Concepto: Error, no se encontró un valor válido después de 10 intentos.\n");

            return;
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el código de concepto de facturación: " + e.getMessage());
            System.out.println("Error al asignar el código de concepto de facturación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void IngresaPrecioUnitario() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement CampoPrecioUnitario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_PRECIOUNITARIO")));

            Random random = new Random();
            double valorAleatorio = 1 + (1000 - 1) * random.nextDouble();
            CampoPrecioUnitario.sendKeys(Keys.TAB);
            CampoPrecioUnitario.sendKeys(String.format("%.2f", valorAleatorio));
            informacionConcepto.append("Precio Unitario: ").append(valorAleatorio).append("\n");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el precio unitario: " + e.getMessage());
            System.out.println("Error al ingresar el precio unitario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void BotonAgregarConcepto() {
        try {
            // Localizar el botón "Agregar" por su ID
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTARDETALLE"));
            // Hacer clic en el botón "Agregar"
            botonAgregar.click();
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");

            // Verificar si el mensaje de confirmación está presente
            try {
                WebElement botonConfirmar = driver.findElement(By.id("BTN_YES"));
                if (botonConfirmar.isDisplayed()) {
                    System.out.println("Se ha mostrado un mensaje de confirmación.");
                    // Hacer clic en el botón "Sí" para confirmar
                    botonConfirmar.click();
                    System.out.println("Se ha hecho clic en el botón 'Sí' para confirmar.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("No se ha mostrado un mensaje de confirmación.");
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
            System.out.println("Se ha producido un error al hacer clic en el botón 'Agregar': " + e.getMessage());
            e.printStackTrace();
        }
        Allure.addAttachment("Informacion del Concepto", informacionConcepto.toString());
    }

    @Step("Obtener Folio de Factura")
    private void ObtenerFolioFactura() {
        try {
            WebElement folioFacturaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            FolioFactura = folioFacturaField.getAttribute("value");
            //Facturas.add(FolioFactura); // Ejemplo de folio de la factura a la lista de facturas
            Facturas.add("FACTURA CFDI "+FolioFactura+"-CALMXLI"); // Ejemplo de folio de la factura a la lista de facturas
            System.out.println("Documento Optenido: " + Facturas);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al obtener el folio de la factura");
        }
    }

    private void AceptarFactura() {
        try {
            // Localizar el botón "Agregar" por su ID
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTAR"));
            // Hacer clic en el botón
            botonAgregar.click();
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
            System.out.println("Se ha producido un error al hacer clic en el botón 'Agregar': " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Step("Aceptar mensaje de concurrencia si aparece")
    private void BotonConcurrenciaFactura() {
        try {
            // Esperar unos segundos para ver si aparece el mensaje de concurrencia
            WebElement botonAceptarConcurrencia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")));

            // Si el botón está disponible, hacer clic en él
            botonAceptarConcurrencia.click();
            System.out.println("Mensaje de concurrencia detectado y aceptado.");

            // Llamar a los métodos que deben repetirse
            ObtenerFolioFactura();
            AceptarFactura();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    private void BotonTimbre() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // Espera hasta que el body esté presente
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            // Intentar localizar el botón "Aceptar"
            WebElement botonAceptar;
            try {
                botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
            } catch (Exception noButton) {
                System.out.println("El botón de aceptar Timbre no está disponible. Continuando...");
                return;
            }
            // Si el botón se encontró, hacer clic
            botonAceptar.click();
            System.out.println("Se presionó el botón de aceptar Timbre");
        } catch (Exception e) {
            System.out.println("Error al presionar el botón de aceptar Timbre. Continuando...");
            e.printStackTrace();
        }
    }

    public void ValidarYEnviarCorreo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // Buscar todos los botones con "onclick"
            List<WebElement> botones = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@onclick]")));
            // Filtrar botones que son visibles y clickeables
            List<WebElement> botonesValidos = botones.stream()
                    .filter(b -> b.isDisplayed() && b.isEnabled())
                    .collect(Collectors.toList());
            // Si no hay botones disponibles, continuar sin fallar
            if (botonesValidos.isEmpty()) {
                System.out.println("No hay botones visibles y clickeables. Continuando...");
                return;
            }
            System.out.println("Botones válidos encontrados:");
            for (WebElement boton : botonesValidos) {
                // Se pueden agregar más detalles si es necesario
            }
            // Seleccionar aleatoriamente un botón de la lista de botones válidos
            WebElement botonSeleccionado = botonesValidos.get(new Random().nextInt(botonesValidos.size()));
            // Intentar hacer clic en el botón seleccionado
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

    private void BotonPoliza() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // Buscar el botón con XPath usando "onclick"
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and contains(@onclick, 'BTN_OK')]")
            ));
            // Intentar hacer clic
            botonAceptar.click();
            System.out.println("Se presionó el botón BTN_OK");
        } catch (Exception e) {
            System.out.println("No se encontró el botón BTN_OK o no se pudo hacer clic.");
        }
    }

    private void BotonImpresion() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            // Buscar el botón con "onclick" que contiene "BTN_REGRESAR"
            WebElement botonRegresar = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and @name='BTN_REGRESAR' and contains(@onclick, 'BTN_REGRESAR')]")
            ));
            if (botonRegresar != null) {
                botonRegresar.click();
                System.out.println("Se presionó el botón de regresar y se genró la factura correctamente.");
            } else {
                System.out.println("No se encontró el botón de regresar, continuando con la ejecución.");
            }
        } catch (TimeoutException e) {
            System.out.println("El botón de regresar no se mostró, continuando la ejecución normalmente");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de regresar");
            System.out.println("Error al presionar el botón de regresar");
            e.printStackTrace();
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
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Cobranza no funciona.");
        }
    }

    public void BotonPagosAbonos() {
        try {
            // Espera explícita hasta que el botón de Pagos/Abonos sea clicable
            WebElement pagosAbonosBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/COBRANZA/PAGOSABONOS1.jpg')]")
            ));
            // Hacer clic en el botón
            pagosAbonosBoton.click();
            System.out.println("Submódulo Pagos/Abonos seleccionado correctamente.");
        } catch (Exception e) {
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
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Registrar'.");
        }
    }

    public void deseleccionarCampoFecha() {
        try {
            // Esperar a que el body esté presente antes de interactuar
            WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            body.click();
            System.out.println("Se hizo clic en el body correctamente y desmarco la fecha correctamente.");
        } catch (StaleElementReferenceException e) {
            System.out.println("Se reintento clickear en el body para deseleccionar campo fecha.");
            // Reubicar el elemento y hacer clic nuevamente
            WebElement body = driver.findElement(By.tagName("body"));
            body.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el body.");
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
            // Localizar el campo de búsqueda usando XPath
            WebElement campoBusqueda = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@class, 'EDT_BUSQUEDAGENERAL')]")));
            campoBusqueda.click();
            // Limpiar el campo antes de ingresar el número de viaje
            campoBusqueda.clear();
            // Ingresar el número de viaje del cliente
            campoBusqueda.sendKeys(FolioFactura);
            System.out.println("Número de viaje ingresado en búsqueda: " + FolioFactura);
        } catch (Exception e) {
            System.out.println("Error en la búsqueda de factura: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error en la búsqueda de factura.");
        }
    }

    public void SeleccionFactura() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        try {
            // Espera explícita hasta que el checkbox de la factura sea visible y clickeable
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
            String referencia = "pago" + FolioFactura;
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
            WebElement btnOk = driver.findElement(By.xpath("//input[contains(@class, 'BTN-BotonClasicoGris BTN_OK ')]"));
            btnOk.click();
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

    @Step("Hacer clic en la imagen de Contra Recibos de clientes")
    private void BotonContraRecibos() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement imagen = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[@src='https://gmterpv8puntocdn.azureedge.net/gmterpv8/GMTERPV8_WEB/Imagenes/COBRANZA/CONTRARECIBOSCLIENTE1.jpg']")
            ));
            imagen.click();
            System.out.println("Se hizo clic en la imagen de Contra Recibos.");
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "El elemento contra recibos de clientes no fue encontrado a tiempo.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en la imagen de Contra Recibos.");
        }
    }

    public void BotonAgregarContraRecibo() {
        try {
            // Espera la opción de Registrar del Contra Recibo
            WebElement opcionRegistrarCR = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_AGREGAR")));
            // Hacer clic en la opción "Registrar"
            opcionRegistrarCR.click();
            System.out.println("Opción 'Agregar Contra Recibo' seleccionada correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Registrar Contra Recibo'.");
        }
    }


    @Step("Asignar Cliente al Contra Recibo")
    private void CodigoClientCR() {
        try {
            WebElement NumeroClientePago = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroClientePago.click();
            NumeroClientePago.sendKeys(NUMERO_CLIENTE);
            NumeroClientePago.sendKeys(Keys.TAB);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al Contra Recibo");
        }
    }

    @Step("Seleccionar o deseleccionar el checkbox de marcar todos en el Contra Recibo")
    private void DeseleccionarTodoCR() {
        try {
            WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("CBOX_MARCARDESMARCAR_1")));
            // Verifica si el checkbox está seleccionado
            if (!checkbox.isSelected()) {
                System.out.println("El checkbox ya estaba seleccionado.");
            } else {
                checkbox.click(); // Sí está seleccionado, lo deselecciona
                System.out.println("Se desmarcan todas las facturas del Contra Recibo.");
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al interactuar con el checkbox.");
        }
    }

    @Step("Obtener Folio Conrtra Recibo")
    private void ObtenerFolioCR() {
        try {
            WebElement folioFacturaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            FolioCR = folioFacturaField.getAttribute("value");
            System.out.println("Folio de Contra Recibo: #" + FolioCR);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al obtener el folio de la factura");
        }
    }

    @Step("Introducir referencia del Contra Recibo")
    public void IntroducirReferenciaCR() {
        try {
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("EDT_REFERENCIA")));
            campoReferencia.clear();
            String referenciaCR = "CR" + FolioCR;
            campoReferencia.sendKeys(referenciaCR);
            System.out.println("Referencia ingresada al Contra Recibo: " + referenciaCR);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la referencia del Contra Recibo.");
            System.out.println("Error al ingresar la referencia del Contra Recibo.");
        }
    }


    public void SeleccionarFacturasCR() { //Método para seleccionar las facturas en el Contra Recibo
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Aumentamos el tiempo de espera
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Iterar sobre el array/lista de Facturas
            for (String factura : Facturas) {

                // **Clic en el ícono de búsqueda**
                WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("TABLE_PROPASIVOSPENDIENTESDEPAGO_TITRES_RECH_3")));
                iconoBuscar.click();
                System.out.println("Se hizo clic en el ícono de búsqueda.");
                Thread.sleep(500);

                // **Interacción con el campo de búsqueda**
                WebElement inputBuscar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("form > input"))); // Cambié a `visibilityOfElementLocated`
                inputBuscar.clear();  // Limpiar el campo antes de ingresar el nuevo valor
                inputBuscar.sendKeys(factura);  // Buscar el documento actual
                Thread.sleep(1000);
                inputBuscar.sendKeys(Keys.ENTER);
                System.out.println("Se ingresó y buscó el documento: " + factura);
                Thread.sleep(500);

                // **Esperar y seleccionar la fila correcta con el número de documento**
                WebElement filaSeleccionada = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_NUMERODOCUMETO')]//div[text()='" + factura + "']]")));
                System.out.println("Se encontró la fila del documento: " + factura);

                // **Buscar el checkbox dentro de la fila seleccionada**
                WebElement checkBox = filaSeleccionada.findElement(By.xpath(".//td[contains(@class, 'wbcolCOL_MARCAR')]//input[@type='checkbox']"));

                // **Imprimir el ID del checkbox encontrado**
                String checkBoxId = checkBox.getAttribute("id");
                System.out.println("Checkbox encontrado con ID: " + checkBoxId);

                // **Si el checkbox no es visible, hacer scroll hasta él**
                if (!checkBox.isDisplayed()) {
                    System.out.println("El checkbox no es visible, desplazándose hacia él...");
                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", checkBox);
                    Thread.sleep(500);
                }

                // **Si el checkbox está deshabilitado, habilitarlo**
                if (!checkBox.isEnabled()) {
                    System.out.println("El checkbox está deshabilitado, intentando habilitarlo...");
                    js.executeScript("arguments[0].removeAttribute('disabled');", checkBox);
                }

                // **Verificar si ya está marcado antes de hacer clic**
                if (!checkBox.isSelected()) {
                    try {
                        checkBox.click();
                        System.out.println("Se marcó el checkbox con `click()` estándar.");
                    } catch (Exception e) {
                        System.out.println("`click()` falló, probando con JavaScript...");
                        js.executeScript("arguments[0].click();", checkBox);
                        System.out.println("Se marcó el checkbox con `JavaScriptExecutor`.");
                    }
                } else {
                    System.out.println("El checkbox ya estaba marcado.");
                }

                Thread.sleep(2000);  // Espera entre búsquedas de cada factura
            }

        } catch (NoSuchElementException e) {
            System.err.println("Elemento no encontrado: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Tiempo de espera agotado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    private void AceptarAgregarCR() {
        try {
            // Localizar el botón "Agregar" por su ID
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTAR"));
            // Hacer clic en el botón
            botonAgregar.click();
            System.out.println("Se ha hecho clic en el botón 'Agregar' y se registra el CR #"+FolioCR);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
            System.out.println("Se ha producido un error al hacer clic en el botón 'Agregar': " + e.getMessage());
            e.printStackTrace();
        }
    }

}
