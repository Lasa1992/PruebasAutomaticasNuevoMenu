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

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionConceptoDescImpr {

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

    @BeforeAll
    public static void setup() {
        // System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
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
        BotonTimbre();
        ValidarYEnviarCorreo();
        BotonPoliza();
        BotonImpresion();

        // Bloque donde se sustituirá la factura
        BusquedaFacturaListado();
        SeleccionarFactura();
        BotonImprimir();
        SeleccionarFormato();
        Imprimir();
        CerrarVistaPrevia();
        MarcarImpresion();
        BotonDescargar();
        SeleccionarOpcionDescarga();
        // Pausa de 3 segundos
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
            System.out.println("Botón Módulo Facturación no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORCONCEPTO1')]")));
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
            WebElement numeroCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            numeroCliente.click();

            // Se utiliza la variable NUMERO_CLIENTE
            numeroCliente.sendKeys(NUMERO_CLIENTE);
            informacionFactura.append("Número de Cliente: ").append(NUMERO_CLIENTE).append("\n");
            numeroCliente.sendKeys(Keys.TAB);

            fluentWait.until(driver -> {
                WebElement field = driver.findElement(By.id("EDT_NUMEROCLIENTE"));
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
            WebElement botonAgregar = driver.findElement(By.id("BTN_ACEPTAR"));
            botonAgregar.click();
            System.out.println("Se ha hecho clic en el botón 'Agregar'.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, null);
            System.out.println("Se ha producido un error al hacer clic en el botón 'Agregar': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void BotonTimbre() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            WebElement botonAceptar;
            try {
                botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
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
                    By.xpath("//input[@type='button' and @name='BTN_REGRESAR' and contains(@onclick, 'BTN_REGRESAR')]")
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
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='search'][aria-controls='TABLE_ProFacturasPorConcepto']")));

            busquedaField.clear();
            busquedaField.sendKeys(FolioFactura);
            System.out.println("Se ingresó el folio de la factura para su búsqueda: " + FolioFactura);
            busquedaField.sendKeys(Keys.ENTER);

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

    // ------------------ Impresión y Descarga ------------------

    @Step("Hacer clic en el botón de impresión")
    public void BotonImprimir() {
        try {
            WebElement botonImprimir = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div/table/tbody/tr/td/a/span/span"));
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón de impresión.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de impresión: " + e.getMessage());
        }
    }

    @Step("Seleccionar el último formato de impresión")
    public void SeleccionarFormato() {
        try {
            WebElement comboFormatos = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/div/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select"));
            Select selectFormato = new Select(comboFormatos);
            int ultimaOpcion = selectFormato.getOptions().size() - 1;
            selectFormato.selectByIndex(ultimaOpcion);
            System.out.println("Se seleccionó el último formato de impresión.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al seleccionar el formato de impresión: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de imprimir")
    public void Imprimir() {
        try {
            WebElement botonImprimir = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div/table/tbody/tr/td/input"));
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón de imprimir.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de imprimir: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón para cerrar la vista previa")
    public void CerrarVistaPrevia() {
        try {
            WebElement botonCerrar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div[3]/div/table/tbody/tr/td/a/span/span"));
            botonCerrar.click();
            System.out.println("Se hizo clic en el botón para cerrar la vista previa.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón para cerrar la vista previa: " + e.getMessage());
        }
    }

    @Step("Marcar impresión eligiendo aleatoriamente entre dos opciones")
    public void MarcarImpresion() {
        try {
            Random random = new Random();
            int opcion = random.nextInt(2);

            String xpathOpcion1 = "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/input";
            String xpathOpcion2 = "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/input";

            String xpathSeleccionado = (opcion == 0) ? xpathOpcion1 : xpathOpcion2;
            WebElement opcionSeleccionada = driver.findElement(By.xpath(xpathSeleccionado));
            opcionSeleccionada.click();
            System.out.println("Se marcó la impresión con la opción: " + (opcion + 1));
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al marcar la impresión: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de descargar carta porte")
    public void BotonDescargar() {
        try {
            WebElement botonDescargar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr/td/table/tbody/tr[1]/td[2]"));
            botonDescargar.click();
            System.out.println("Se hizo clic en el botón de descargar carta porte.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de descargar carta porte: " + e.getMessage());
        }
    }

    @Step("Seleccionar la opción de descarga y verificar si se descargó un archivo")
    public void SeleccionarOpcionDescarga() {
        try {
            WebElement opcionDescarga = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr/td/table/tbody/tr[2]/td/div/table/tbody/tr[1]/td[2]/a"));
            opcionDescarga.click();
            System.out.println("Se seleccionó la opción de descarga.");
            Thread.sleep(5000);

            if (verificarDescarga()) {
                System.out.println("El archivo se descargó correctamente.");
            } else {
                System.out.println("No se encontró el archivo descargado.");
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La pausa fue interrumpida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al seleccionar la opción de descarga: " + e.getMessage());
        }
    }

    // Método auxiliar para verificar si se descargó un archivo recientemente
    private boolean verificarDescarga() {
        String rutaDescargas = System.getProperty("user.home") + "/Downloads/";
        File carpetaDescargas = new File(rutaDescargas);
        File[] archivos = carpetaDescargas.listFiles();
        long tiempoActual = System.currentTimeMillis();

        if (archivos == null) {
            return false;
        }

        for (File archivo : archivos) {
            if (archivo.isFile() && (tiempoActual - archivo.lastModified()) <= 10000) {
                System.out.println("Archivo descargado: " + archivo.getName());
                return true;
            }
        }
        return false;
    }
}