package Cobranza;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoConsolidado {

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

    private static final String Forma_Pago   = "TRANSFERENCIA ELECTRÓNICA DE FONDOS";


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
    @Description("Prueba de Inicio de Sesion - Se utiliza usuario GM")
    public void inicioSesion() {
        InicioSesion.fillForm();
        InicioSesion.submitForm();
        InicioSesion.handleAlert();
    }

    @Test
    @Order(2)
    @Description("Prueba para el manejo del tipo de Cambio y de la ventana de novedades.")
    public void AlertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
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
        //Se hace primer factura
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
        //BotonPoliza(); // Aceptar botón generó póliza
        BotonImpresion(); // Regresar a la pantalla principal

        //Se genera la segunda factura
        BotonAgregarListado();
        CodigoClienteFactura2();
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
        //BotonPoliza(); // Aceptar botón generó póliza
        BotonImpresion(); // Regresar a la pantalla principal


        // Bloque donde se controla el pago por factoraje
        BotonModuloCobranza(); // Selecciona el módulo de Cobranza en la interfaz de usuario.
        BotonPagosAbonos(); // Selecciona el submódulo pagos/abonos.
        BotonRegistrarPagoConsolidado(); // Selecciona la opción de registrar pago de factoraje.
        deseleccionarCampoFecha(); // Deselecciona el campo de fecha.
        CodigoClientPago(); // Introduce el código del cliente para el pago.
        SeleccionarClientAdicional(); // Selecciona un cliente adicional.
        SeleccionarCuentaBancariaPesosAleatoria(); // Selecciona una cuenta bancaria aleatoria para el pago.
        FormaPago(); // Selecciona la forma de pago.
        SeleccionarFacturas(); // Selecciona las facturas a pagar.
        ValidarConversion(); // Se valida la conversión de la moneda.
        IntroducirMontoPago(); // Introduce el monto a pagar.
        IntroducirReferencia(); // Introduce una referencia pago
        AceptarPagoAbono(); // Acepta el pago/abono.
        TimbrePago(); // Acepta el timbre del pago.
        EnvioCorreoPago(); // Envía un correo para el pago (Sí/No).
        //AceptarPolizaPago(); // Acepta la póliza del pago.
        deseleccionarCampoFecha2(); // Deselecciona el campo de fecha.
        SalirVentanaPago(); // Sale de la ventana de pago.
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Pago Consolidado...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
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

    private static void CodigoClienteFactura2() {
        try {
            WebElement clienteField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            clienteField.clear();
            Thread.sleep(500);
            clienteField.sendKeys("2"); // crea una factura para el cliente #2
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
            List<String> opciones = List.of("PESOS");

            // Elige aleatoriamente una opción
            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));
            //String opcionSeleccionada = "PESOS"; // Selecciona Moneda PESOS en la factura

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

            nuevoCampo.sendKeys(String.format("%.0f", valorAleatorio));
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
            WebElement numeroClientePago = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"EDT_NUMEROCLIENTE\"]")
            ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", numeroClientePago);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", numeroClientePago);

            numeroClientePago.sendKeys(NUMERO_CLIENTE);
            numeroClientePago.sendKeys(Keys.TAB);

        } catch (TimeoutException e) {
            System.out.println("Campo 'Número Cliente' no se hizo clickeable a tiempo: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error al esperar por el campo 'Número Cliente'");
        } catch (Exception e) {
            System.out.println("Error inesperado en el método CodigoClientPago: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error inesperado al asignar el cliente al pago");
        }
    }



    @Step("Seleccionar cliente adicional desde botón 'Más Clientes'")
    private void SeleccionarClientAdicional() {
        try {
            // Paso 1: Clic en el botón "Más Clientes"
            WebElement botonMasClientes = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_MASCLIENTES")));
            botonMasClientes.click();
            System.out.println("✅ Botón 'Más Clientes' seleccionado correctamente.");

            // Paso 2: Selección del cliente adicional (por ejemplo, fila 2 de la tabla)
            WebElement clienteAdicional = wait.until(ExpectedConditions.elementToBeClickable(By.id("_1_TABLE_CATCLIENTES_1")));
            clienteAdicional.click();
            System.out.println("✅ Cliente adicional #2 seleccionado correctamente.");

            // Paso 3: Confirmar selección con el botón "Seleccionar Cliente"
            WebElement botonAceptarCliente = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_SELECCIONARCLIENTE")));
            botonAceptarCliente.click();
            System.out.println("✅ Cliente adicional guardado correctamente.");
            // Espera de 3 segundos antes de continuar
            Thread.sleep(3000);

        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "⏳ Timeout al interactuar con el cliente adicional.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // buena práctica en caso de interrupción
            UtilidadesAllure.manejoError(driver, e, "⏸ Interrupción durante la espera antes de guardar el cliente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "❌ Error general al seleccionar cliente adicional.");
        }
    }

    @Step("Seleccionar una cuenta bancaria en pesos aleatoria")
    public void SeleccionarCuentaBancariaPesosAleatoria() {
        try {
            // Localizar el combo box usando XPath
            WebElement comboBoxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[contains(@class, 'COMBO_CATCUENTASBANCARIAS')]")));

            // Instanciar el objeto Select para manipular el dropdown
            Select comboBox = new Select(comboBoxElement);

            // Obtener todas las opciones disponibles en el combo box
            List<WebElement> opcionesDisponibles = comboBox.getOptions();

            // Filtrar las opciones que contienen "Pesos" o "MXN"
            List<WebElement> opcionesPesos = opcionesDisponibles.stream()
                    .filter(opcion -> opcion.getText().contains("Pesos") || opcion.getText().contains("MXN"))
                    .collect(Collectors.toList());

            // Verificar si hay opciones en pesos disponibles
            if (!opcionesPesos.isEmpty()) {
                // Seleccionar una opción aleatoria de las filtradas
                Random random = new Random();
                int indiceAleatorio = random.nextInt(opcionesPesos.size()); // Índice dentro de las opciones filtradas
                WebElement opcionSeleccionada = opcionesPesos.get(indiceAleatorio);

                // Obtener el texto de la opción seleccionada
                String textoSeleccionado = opcionSeleccionada.getText();

                // Seleccionar la opción en el combo box
                comboBox.selectByVisibleText(textoSeleccionado);
                System.out.println("Cuenta bancaria seleccionada: " + textoSeleccionado);
            } else {
                System.out.println("No hay cuentas bancarias en pesos disponibles para seleccionar.");
            }
        } catch (TimeoutException | NoSuchElementException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar una cuenta bancaria en pesos aleatoria.");
            System.out.println("Error al seleccionar una cuenta bancaria en pesos aleatoria.");
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

    @Step("Validar moneda y calcular el monto a pagar del factoraje")
    public void ValidarConversion() {
        try {
            // Obtener valores de los campos necesarios
            WebElement montoPesosElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTEPESOS")));
            WebElement compensacionElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_TOTALCOMPENSACION")));

            // Limpiar caracteres no numéricos
            String valorPesos = montoPesosElement.getAttribute("value").trim().replace(",", "").replace("$", "");
            String valorCompensacion = compensacionElement.getAttribute("value").trim().replace(",", "").replace("$", "");

            // Convertir a double
            double montoPesos = (!valorPesos.isEmpty()) ? Double.parseDouble(valorPesos) : 0.0;
            double compensacion = (!valorCompensacion.isEmpty()) ? Double.parseDouble(valorCompensacion) : 0.0;

            // Calcular el monto a pagar restando la compensación
            MontoaPAGAR = montoPesos - compensacion;

            System.out.println("Monto en pesos: " + montoPesos);
            System.out.println("Compensación: " + compensacion);
            System.out.println("Monto a pagar calculado: " + String.format(Locale.US, "%.2f", MontoaPAGAR));
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al calcular el monto a pagar.");
            System.out.println("Error al calcular el monto a pagar.");
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
            System.out.println("Entra a registrar la referencia bancaria");
            WebElement campoReferencia = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("EDT_REFERENCIABANCARIA")));
            campoReferencia.clear();

            // Generar número aleatorio de 3 dígitos
            Random rand = new Random();
            int numeroAleatorio = rand.nextInt(900) + 100; // Genera entre 100 y 999

            // Construir la referencia con el número aleatorio
            String referencia = "Pago Consolidado AU REF" + numeroAleatorio;
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
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"BTN_ACEPTAR\"]")));

            // Esperar 5 segundos antes de hacer clic
            Thread.sleep(5000);

            btnAceptar.click();
            System.out.println("Pago/Abono con Factoraje Aceptado.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al aceptar el pago o abono.");
            System.out.println("Error al aceptar el pago o abono.");
        }
    }

    @Step("Generar Timbre del Pago")
    public void TimbrePago() {
        try {
            WebElement btnSi = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"BTN_YES\"]")));
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
            // Espera explícita para verificar si el botón es visible
            WebElement btnOk = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@class, 'BTN-BotonClasicoGris BTN_OK ')]")));

            // Verifica si el botón está presente antes de hacer clic
            if (btnOk.isDisplayed()) {
                btnOk.click();
                System.out.println("Póliza contable aceptada.");
            } else {
                System.err.println("El botón 'Aceptar Póliza' no está visible.");
            }

        } catch (TimeoutException e) {
            // Se lanza si el botón no se encuentra en el tiempo dado
            System.err.println("El botón 'Aceptar Póliza' no apareció dentro del tiempo esperado.");
        } catch (NoSuchElementException e) {
            // Se lanza si el botón no se encuentra en el DOM
            System.err.println("No se encontró el botón 'Aceptar Póliza': " + e.getMessage());
        } catch (Exception e) {
            // Para manejar cualquier otra excepción inesperada
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

    public void SeleccionarFacturas() { //Método para seleccionar las facturas
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Aumentamos el tiempo de espera
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Iterar sobre el array/lista de Facturas
            for (String factura : Facturas) {

                // **Clic en el ícono de búsqueda**
                WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("TABLE_PROFACTURAS_TITRES_RECH_1")));
                iconoBuscar.click();
                System.out.println("Se hizo clic en el ícono de búsqueda de facturas para seleccionarlas y agregarlas al contra recibo.");
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
                        By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_DOCUMENTO')]//div[text()='" + factura + "']]")));
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
                        System.out.println("Se selecciono la factura correctamente");
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

    public void BotonRegistrarPagoConsolidado() {
        try {
            // Espera a que la opción principal "Registrar" en el menú sea clickeable
            WebElement menuRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("z_BTN_PAGOCONSOLIDADO_IMG")));
            menuRegistrar.click();
            System.out.println("Se seleccionó el botón 'Registrar Pago Consolidado'.");

            // Usa la misma instancia de wait
            WebElement opcionRegistrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("CBOX_PAGOABONO_1")));
            opcionRegistrar.click();
            System.out.println("Opción 'Pago/Abono consolidado' seleccionada correctamente.");

            //Aceptar el menú
            WebElement Aceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("z_BTN_ACEPTAR_IMG")));
            Aceptar.click();
            System.out.println("Se seleccionó el botón 'Aceptar' para crear el pago consolidado.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'Pago Consolidado'.");
        }
    }

    @Step("Seleccionar botón información de pago")
    private void BotonInformacionPago() {
        try {
            WebElement botonInfoAdicional = driver.findElement(By.id("BTN_INFORMACIONPAGO"));
            botonInfoAdicional.click();

            // Esperar 10 segundos después del clic
            Thread.sleep(5000);

            System.out.println("Se ha hecho clic en el botón 'Info Adicional' y se esperó 5 segundos.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón 'Info Adicional'.");
            System.out.println("Se ha producido un error al hacer clic en el botón 'Info Adicional': " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Step("Seleccionar Transferencia Electrónica")
    public void FormaPago() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATMETODOSPAGOS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            // Se usa la variable
            comboBox.selectByVisibleText(Forma_Pago);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "No se pudo seleccionar forma de pago: " + Forma_Pago);
        }
    }
}