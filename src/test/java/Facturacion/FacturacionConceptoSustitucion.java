package Facturacion;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import Indicadores.Variables;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionConceptoSustitucion {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Se crean variables para almacenar la información concatenada de las Facturas
    // y mostrarlas en el reporte de Allure
    static StringBuilder informacionFactura = new StringBuilder();
    static StringBuilder informacionConcepto = new StringBuilder();
    static StringBuilder informacionTimbrado = new StringBuilder();

    // Variable para almacenar el folio de la factura
    private static String FolioFactura;

    // Número de cliente en variable para poder modificarlo fácilmente
    private static final String NUMERO_CLIENTE = Variables.CLIENTE;

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

    @Test
    @Order(3)
    @Description("Ingresar al modulo de facturación.")
    public void ingresarModuloFacturacion() {
        handleImageButton();
        handleSubMenuButton();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Se genera una factura con conceptos aleatorios")
    public void FacturacionporConcepto() {
        // Limpia variables de Allure para los reportes.
        informacionFactura.setLength(0);
        informacionConcepto.setLength(0);
        informacionTimbrado.setLength(0);

        // Comienza el flujo de facturación
        BotonAgregarListado();
        AsignarCliente();
        MonedaFactura();
        ConceptofacturacionAgregar();
        IngresaValorCantidad();
        AsignarCodigoConceptoFacturacion();
        IngresaPrecioUnitario();
        BotonAgregarConcepto();
        ObtenerFolioFactura();
        AceptarFactura();
        BotonConcurrenciaFactura();
        BotonTimbre();
        //ValidarYEnviarCorreo();
        //BotonPoliza();
        BotonImpresion();

        // Bloque donde se sustituirá la factura
        BusquedaFacturaListado();
        SeleccionarFactura();
        CancelacionFactura();
        SeleccionaMotivoCancelacion();
        MensajeSustitucionRequerida();
        MonedaSustitucion();
        AceptarFacturaSustituida();
        BotonConcurrenciaFacturaSustituida();
        BotonTimbreSustitucion();
        BotonImpresion();
        //BotonFechaAjuste();
        //CorreoDesspuesSustitucion();
        CancelacionSAT();
        CancelacionSAT2();
        CancelacionSAT3();
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[3]/a/img")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[3]/ul/li[2]/a/img")));
            subMenuButton.click();
        } catch (Exception e) {
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

    @Step("Asignar Cliente a la Factura")
    private static void AsignarCliente() {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        try {
            WebElement numeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='EDT_NUMEROCLIENTE']")));
            numeroCliente.click();

            // Se utiliza la variable NUMERO_CLIENTE
            numeroCliente.sendKeys(NUMERO_CLIENTE);
            informacionFactura.append("Número de Cliente: ").append(NUMERO_CLIENTE).append("\n");
            numeroCliente.sendKeys(Keys.TAB);

            fluentWait.until(driver -> {
                WebElement field = driver.findElement(By.xpath("//*[@id='EDT_NUMEROCLIENTE']"));
                return !Objects.requireNonNull(field.getAttribute("value")).isEmpty();
            });

            System.out.println("El campo de cliente tiene información.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al realizar la acción en el campo 'Número de Cliente'.");
            System.out.println("Error al realizar la acción en el campo 'Número de Cliente'.");
            e.printStackTrace();
        }
    }

    private static void MonedaFactura() {
        try {
            Select primerComboBox = new Select(driver.findElement(By.id("COMBO_CATMONEDAS")));
            List<String> opciones = List.of("PESOS", "DÓLARES");

            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));

            primerComboBox.selectByVisibleText(opcionSeleccionada);

            System.out.println("La Moneda es: " + opcionSeleccionada);
            informacionFactura.append("Moneda: ").append(opcionSeleccionada).append("\n\n");
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
            while (intentos < 10) {
                intentos++;
                int codigoConcepto = intentos;

                campoCodigo.clear();
                campoCodigo.sendKeys(String.valueOf(codigoConcepto));
                campoCodigo.sendKeys(Keys.TAB);

                wait.until(ExpectedConditions.attributeToBeNotEmpty(campoConcepto, "value"));
                String valorConcepto = campoConcepto.getAttribute("value");

                if (valorConcepto != null && !valorConcepto.equals("El concepto de facturación no está activo, Revisar")) {
                    System.out.println("Valor válido encontrado: " + codigoConcepto);
                    System.out.println("El concepto de facturación es: " + valorConcepto);

                    informacionConcepto.append("Número Concepto: ").append(codigoConcepto).append("\n");
                    informacionConcepto.append("Nombre Concepto: ").append(valorConcepto).append("\n");
                    return;
                } else {
                    System.out.println("Intento " + intentos + ": Valor " + codigoConcepto + " produjo el mensaje de error.");
                }
            }
            System.out.println("No se encontró un valor válido después de 10 intentos.");
            informacionConcepto.append("Concepto: Error, no se encontró un valor válido después de 10 intentos.\n");
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
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTARDETALLE"));
            botonAgregar.click();
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");

            try {
                WebElement botonConfirmar = driver.findElement(By.id("BTN_YES"));
                if (botonConfirmar.isDisplayed()) {
                    System.out.println("Se ha mostrado un mensaje de confirmación.");
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

    @Step("Obtener folio de la factura")
    public void ObtenerFolioFactura() {
        try {
            WebElement campoFactura = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[contains(@class, 'EDT_FOLIO')]")));
            FolioFactura = campoFactura.getAttribute("value").trim();
            System.out.println("Folio de la factura obtenida: " + FolioFactura);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al obtener el folio de la factura.");
            System.out.println("Error al obtener el folio de la factura.");
        }
    }

    private void AceptarFactura() {
        try {
            // Localizar el botón "Agregar" por su ID
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTAR"));

            // Hacer clic en el botón
            botonAgregar.click();

            // Imprimir un mensaje para confirmar que se ha hecho clic en el botón
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");

        } catch (Exception e) {
            //Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, null);
            // Manejar cualquier excepción que ocurra
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
            WebElement botonAceptar = driver.findElement(By.xpath("//*[@id='BTN_YES']"));
            // Espera que sea clickeable si existe
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
                // Se pueden imprimir más detalles si lo deseas
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

    private void BotonPoliza() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='button' and contains(@onclick, 'BTN_OK')]")
            ));
            botonAceptar.click();
            System.out.println("Se presionó el botón BTN_OK");
        } catch (Exception e) {
            System.out.println("No se encontró el botón BTN_OK o no se pudo hacer clic.");
        }
    }

    private void BotonImpresion() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement botonRegresar = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_REGRESAR\"]")
            ));
            if (botonRegresar != null) {
                botonRegresar.click();
                System.out.println("Se presionó el botón de regresar");
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

    private static void BusquedaFacturaListado() {
        try {
            // Buscar el campo de búsqueda con XPath en lugar de CSS Selector
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@type='search' and @aria-controls='TABLE_ProFacturasPorConcepto']")));

            busquedaField.clear();
            busquedaField.sendKeys(FolioFactura);
            System.out.println("Se ingresó el folio de la factura para su búsqueda: " + FolioFactura);
            busquedaField.sendKeys(Keys.ENTER);

            // Esperar a que la tabla muestre el folio buscado
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='TABLE_ProFacturasPorConcepto']//tbody"), FolioFactura));

            System.out.println("La búsqueda se completó y los resultados están visibles.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la factura: " + FolioFactura);
        }
    }


    @Step("Seleccionar Factura en el Listado")
    private static void SeleccionarFactura() {
        try {
            Thread.sleep(3000);
            WebElement tablaFacturas = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("TABLE_ProFacturasPorConcepto")));

            WebElement fila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//table[@id='TABLE_ProFacturasPorConcepto']//tr[td[contains(text(),'" + FolioFactura + "')]]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", fila);
            try {
                fila.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fila);
            }
            System.out.println("Factura seleccionada correctamente: " + FolioFactura);
        } catch (NoSuchElementException e) {
            System.out.println("ERROR: No se encontró la factura con folio: " + FolioFactura);
            UtilidadesAllure.manejoError(driver, e, "No se encontró la factura con el número: " + FolioFactura);
        } catch (TimeoutException e) {
            System.out.println("ERROR: La tabla no cargó los resultados a tiempo.");
            UtilidadesAllure.manejoError(driver, e, "La tabla de facturas no cargó correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR: Ocurrió un problema al seleccionar la factura.");
            UtilidadesAllure.manejoError(driver, e, "Error inesperado al seleccionar la factura.");
        }
    }

    @Step("Cancelar Factura")
    private static void CancelacionFactura() {
        try {
            WebElement cancelarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("z_BTN_CANCELAR_IMG")));
            cancelarButton.click();
            System.out.println("Se presionó el botón de cancelar factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de cancelar factura");
            System.out.println("Error al presionar el botón de cancelar factura");
        }
    }

    public void SeleccionaMotivoCancelacion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar y seleccionar motivo de cancelación
            WebElement combo = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='COMBO_MOTIVOCANCELACION']")));
            Select select = new Select(combo);
            select.selectByIndex(0);

            // Esperar campo de comentario y escribir mensaje
            WebElement comentarioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='EDT_MOTIVO']")));
            comentarioField.clear();
            comentarioField.sendKeys("Cancelación de factura por motivo de prueba automática. " + FolioFactura);
            System.out.println("Comentario ingresado: " + comentarioField.getAttribute("value"));

            // Esperar y hacer clic en botón Aceptar
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_ACEPTAR']")));
            botonAceptar.click();

        } catch (Exception e) {
            System.out.println("Error al seleccionar motivo de cancelación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Step("Mensaje de Sustitución Requerida")
    private void MensajeSustitucionRequerida() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Esperar hasta que el botón esté presente usando XPath
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"BTN_YES\"]")));

            // Ahora esperar que esté visible y habilitado para clic
            WebElement botonYes = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"BTN_YES\"]")));

            botonYes.click();
            System.out.println("Se hizo clic en BTN_YES.");
        } catch (TimeoutException te) {
            System.out.println("El botón BTN_YES no apareció o no fue clickeable a tiempo.");
            UtilidadesAllure.manejoError(driver, te, "BTN_YES no disponible en MensajeSustitucionRequerida.");
        } catch (Exception e) {
            System.out.println("Error inesperado en MensajeSustitucionRequerida: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error en MensajeSustitucionRequerida.");
        }
    }

    private void MonedaSustitucion() {
        try {
            Select primerComboBox = new Select(driver.findElement(By.xpath("//*[@id='COMBO_CATMONEDAS']")));
            List<String> opciones = List.of("PESOS", "DÓLARES");

            Random random = new Random();
            String opcionSeleccionada = opciones.get(random.nextInt(opciones.size()));

            primerComboBox.selectByVisibleText(opcionSeleccionada);

            System.out.println("La Moneda es: " + opcionSeleccionada);
            informacionFactura.append("Moneda: ").append(opcionSeleccionada).append("\n\n");
            Allure.addAttachment("Informacion Factura", informacionFactura.toString());
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Se ha producido un error: " + e.getMessage());
            System.out.println("Se ha producido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void AceptarFacturaSustituida() {
        int intentos = 0;
        boolean exito = false;
        while (intentos < 3 && !exito) {
            try {
                WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
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

    @Step("Aceptar mensaje de concurrencia si aparece")
    private void BotonConcurrenciaFacturaSustituida() {
        try {
            // Esperar unos segundos para ver si aparece el mensaje de concurrencia
            WebElement botonAceptarConcurrencia = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")));

            // Si el botón está disponible, hacer clic en él
            botonAceptarConcurrencia.click();
            System.out.println("Mensaje de concurrencia detectado y aceptado.");

            // Llamar a los métodos que deben repetirse

            AceptarFacturaSustituida();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    private void BotonTimbreSustitucion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            WebElement botonAceptar;
            try {
                // Usar XPath en lugar de ID
                botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='BTN_YES']")));
            } catch (Exception noButton) {
                System.out.println("El botón de aceptar Timbre no está disponible. Continuando...");
                return;
            }

            botonAceptar.click();
            System.out.println("Se presionó el botón de aceptar Timbre");

        } catch (Exception e) {
            System.out.println("Error al presionar el botón de aceptar Timbre. Continuando...");
            e.printStackTrace();
        }
    }


    @Step("Enviar Correo Después de Sustitución")
    private static void CorreoDesspuesSustitucion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera que el modal con los botones esté presente en el DOM y sea visible
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'modal-content')]")));

            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;

            if (elegirSi) {
                System.out.println("Intentando hacer clic en el botón 'Sí'...");
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_YES']")));
                System.out.println("Se eligió la opción Sí para el envío del correo después de la sustitución.");
            } else {
                System.out.println("Intentando hacer clic en el botón 'No'...");
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='BTN_NO']")));
                System.out.println("Se eligió la opción No para el envío del correo después de la sustitución.");
            }

            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo después de la sustitución");
            System.out.println("Error al elegir entre Sí o No para el envío del correo después de la sustitución: " + e.getMessage());
        }
    }

    @Step("Aceptar Cancelación en el SAT")
    private static void CancelacionSAT() {
        try {
            System.out.println("Esperando que el botón 'Aceptar' del SAT sea clickable...");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_YES\"]")
            ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", aceptarButton);
            aceptarButton.click();
            System.out.println("✅ Se presionó correctamente el botón de aceptar para la cancelación en el SAT");

        } catch (TimeoutException e) {
            System.out.println("❌ Timeout: El botón 'Aceptar' no fue clickable en el tiempo esperado.");
            UtilidadesAllure.manejoError(driver, e, "Timeout al esperar que el botón 'Aceptar' del SAT sea clickable");

        } catch (Exception e) {
            System.out.println("❌ Error inesperado al presionar el botón 'Aceptar' del SAT: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar para la cancelación en el SAT");
        }
    }

    @Step("Aceptar Cancelación en el SAT (Segundo Mensaje)")
    private static void CancelacionSAT2() {
        try {
            System.out.println("Esperando botón 'Aceptar' del segundo mensaje de cancelación SAT...");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_YES\"]")
            ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", aceptarButton);
            aceptarButton.click();
            System.out.println("✅ Se presionó el botón de aceptar para la cancelación en el SAT (segundo mensaje)");

        } catch (TimeoutException e) {
            System.out.println("❌ Timeout: No se pudo hacer clic en el botón 'Aceptar' del segundo mensaje SAT.");
            UtilidadesAllure.manejoError(driver, e, "Timeout al esperar el botón 'Aceptar' en la cancelación SAT (segundo mensaje)");

        } catch (Exception e) {
            System.out.println("❌ Error inesperado al presionar el botón 'Aceptar' en la cancelación SAT (segundo mensaje): " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar para la cancelación en el SAT (segundo mensaje)");
        }
    }

    @Step("Aceptar Cancelación en el SAT (Tercer Mensaje)")
    private static void CancelacionSAT3() {
        try {
            WebElement aceptarButton = driver.findElement(By.xpath("//*[@id=\"BTN_OK\"]"));
            if (aceptarButton.isDisplayed() && aceptarButton.isEnabled()) {
                aceptarButton.click();
                System.out.println("✅ Se hizo clic en el botón 'BTN_OK' para la cancelación en el SAT.");
            } else {
                System.out.println("⚠️ El botón 'BTN_OK' está presente pero no es clicable.");
            }
        } catch (NoSuchElementException e) {
            System.out.println("❌ No se encontró el botón 'BTN_OK' en la página.");
        } catch (Exception e) {
            System.out.println("❌ Error inesperado al presionar el botón 'BTN_OK' en la cancelación SAT: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón 'BTN_OK' en la cancelación SAT (tercer mensaje).");
        }
    }

}
