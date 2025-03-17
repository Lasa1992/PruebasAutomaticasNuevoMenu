package Facturacion;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionViajeSustitucion {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // =========================================================================
    // VARIABLES CONFIGURABLES
    // =========================================================================
    private static final String NUMERO_CLIENTE   = Variables.CLIENTE;  // Número de cliente
    private static final String NUMERO_RUTA      = Variables.RUTA;  // Número de ruta
    private static final String TIPO_DOCUMENTO   = Variables.DocumentoIngreso;
    // =========================================================================

    // Variables para almacenar valores reutilizables
    private static String numeroViajeCliente;
    private static String monedaSeleccionada;
    private static String numeroFactura;

    @BeforeAll
    public static void setup() {
        // System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.softwareparatransporte.com/");
        driver.manage().window().maximize();
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
    @Description("Flujo principal: Crear viaje, facturar y luego proceder a la sustitución de la factura.")
    public void EntrarAViajes() {

        // ====================
        // Creación del viaje
        // ====================
        BotonModuloTrafico();
        BotonListadoViajes();
        BotonAgregarCartaPorte();
        TipoDocumentoIngreso();
        NumeroViajeCliente();
        CodigoCliente();
        MonedaCartaPorte();
        FolioRuta();
        NumeroEconomicoConvoy();
        SeleccionarPestanaMateriales();
        BotonImportarMaterial();
        ImportacionMaterial();
        BotonAceptarImportacion();
        BotonAceptarViaje();
        BotonConcurrencia();
        EnvioCorreo();
        BotonImpresion();

        // =============================
        // Facturación del viaje creado
        // =============================
        BotonModuloFacturacion();
        BotonFacturacionPorViaje();
        BotonAgregarFactura();
        NumeroFactura();
        CodigoClienteFactura();
        MonedaAFacturar();
        FiltroMoneda();
        CampoBusqueda();
        SelecionaFactura();
        AceptarFactura();
        BotonConcurrenciaFactura();
        AceptarTimbre();
        EnvioCorreoFactura();
        AceptarPoliza();
        AceptarImpresion();

        // ======================
        // Sustitución de factura
        // ======================
        BusquedaFacturaListado();
        SeleccionarFactura();
        CancelacionFactura();
        SeleccionaMotivoCancelacion();
        MensajeSustitucionRequerida();
        FiltroMonedaSustitucion();
        SeleccionFacturaSutitucion();
        AceptarFacturaSustituida();
        BotonConcurrenciaFacturaSustituida();
        CorreoDesspuesSustitucion();
        CancelacionSAT();
        CancelacionSAT2();
        CancelacionSAT3();
    }

    @AfterAll
    public static void tearDown() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // =====================================================
    // BLOQUE 1: CREACIÓN DEL VIAJE
    // =====================================================

    private static void BotonModuloTrafico() {
        try {
            WebElement ModuloBotonTrafico = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1')]")));
            ModuloBotonTrafico.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    private static void BotonListadoViajes() {
        try {
            WebElement ListadoBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1')]]")));
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
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATTIPOSDOCUMENTOS")));

            // Se usa la variable TIPO_DOCUMENTO para buscar la opción
            WebElement opcionIngreso = tipoDocumentoCombo.findElement(
                    By.xpath(".//option[text()='" + TIPO_DOCUMENTO + "']"));

            opcionIngreso.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "No se pudo seleccionar el Tipo de Documento: " + TIPO_DOCUMENTO);
        }
    }

    @Step("Manejar Número de Viaje (folio + 1)")
    private void NumeroViajeCliente() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            String folioValue = folioField.getAttribute("value");

            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NOVIAJECLIENTE")));

            // Se suma 1 al valor recuperado para formar el número de viaje
            numeroViajeCliente = folioValue + "1";
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViajeCliente);

        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }

    @Step("Asignar Cliente al Viaje (variable NUMERO_CLIENTE)")
    private void CodigoCliente() {
        try {
            WebElement NumeroClienteField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            NumeroClienteField.click();
            NumeroClienteField.sendKeys(NUMERO_CLIENTE);
            NumeroClienteField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }

    @Step("Seleccionar Moneda del Viaje Aleatoriamente")
    private void MonedaCartaPorte() {
        monedaSeleccionada = "";
        try {
            WebElement tipoDocumentoCombo = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            Random random = new Random();
            int indiceAleatorio = random.nextInt(opcionesDisponibles.size());

            WebElement opcionSeleccionada = opcionesDisponibles.get(indiceAleatorio);
            String valorASeleccionar = opcionSeleccionada.getAttribute("value");

            // Forzar la selección con JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'))",
                    tipoDocumentoCombo, valorASeleccionar);

            wait.until(ExpectedConditions.textToBePresentInElement(tipoDocumentoCombo,
                    opcionSeleccionada.getText()));

            monedaSeleccionada = opcionSeleccionada.getText().trim().toUpperCase();
            System.out.println("La moneda seleccionada es: " + monedaSeleccionada);

            String valorActual = comboBox.getFirstSelectedOption().getText().trim().toUpperCase();
            if (!valorActual.equals(monedaSeleccionada)) {
                throw new Exception("Error: no se pudo cambiar la moneda. Valor actual sigue siendo: " + valorActual);
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda en MonedaCartaPorte");
        }
    }

    @Step("Asignar Folio de Ruta (variable NUMERO_RUTA)")
    private void FolioRuta() {
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIORUTA")));
            folioRutaField.click();
            folioRutaField.sendKeys(NUMERO_RUTA);
            folioRutaField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Folio Ruta");
        }
    }

    @Step("Manejar Número Económico Convoy")
    private void NumeroEconomicoConvoy() {
        try {
            WebElement numeroEconomicoConvoyField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROECONOMICOCONVOY")));
            numeroEconomicoConvoyField.sendKeys("E3");
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número Económico del Convoy");
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales() {
        try {
            WebElement pestanaMateriales = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("TAB_TAB1_2")));
            WebElement linkPestana = pestanaMateriales.findElement(By.tagName("a"));
            linkPestana.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    @Step("Manejar Botón Importar Materiales")
    private void BotonImportarMaterial() {
        try {
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_IMPORTARMATERIALES")));
            botonImportar.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Importar Materiales");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {
        try {
            WebElement inputArchivo = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));

            // Ajusta la ruta del archivo según tus necesidades
            String rutaArchivo = "C:\\Users\\UsuarioY\\Desktop\\Pruebas Automaticas\\XLSXPruebas\\ImportarMaterialesPA.xlsx";
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                throw new Exception("El archivo especificado no existe: " + rutaArchivo);
            }

            inputArchivo.sendKeys(rutaArchivo);

            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_IMPORTARMATERIALES_IMPORTAR")));
            botonImportar.click();
            Thread.sleep(3000);

            try {
                WebElement iconoExito = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.id("IMG_ICONO_EXITO")));
                if (iconoExito.isDisplayed()) {
                    System.out.println("Importación de materiales exitosa.");
                }
            } catch (TimeoutException e) {
                WebElement iconoError = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.id("IMG_IMPORTARMATERIALES_ERROR")));
                if (iconoError.isDisplayed()) {
                    UtilidadesAllure.capturaImagen(driver);
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
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en Aceptar después de la importación de materiales");
        }
    }

    @Step("Aceptar Viaje")
    private void BotonAceptarViaje() {
        try {
            WebElement botonAceptarViaje = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("BTN_ACEPTAR")));
            botonAceptarViaje.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en Aceptar para confirmar el viaje");
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

    @Step("Enviar Por Correo (Sí/No)")
    private void EnvioCorreo() {
        try {
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo.");
            } else {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo.");
            }
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    @Step("Botón de Impresión")
    private void BotonImpresion() {
        try {
            WebElement botonRegresar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_REGRESAR")));
            botonRegresar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Regresar para impresión");
        }
    }

    // =====================================================
    // BLOQUE 2: FACTURACIÓN DEL VIAJE
    // =====================================================

    private static void BotonModuloFacturacion() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1')]")));
            imageButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Facturación no funciona.");
        }
    }

    private static void BotonFacturacionPorViaje() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORVIAJE1')]")));
            subMenuButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Facturación por Viaje no funciona.");
        }
    }

    private static void BotonAgregarFactura() {
        try {
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_AGREGAR")));
            addButton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Factura no funciona.");
        }
    }

    @Step("Manejar Número de Factura (obtener folio)")
    private void NumeroFactura() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            numeroFactura = folioField.getAttribute("value");
            System.out.println("El número de factura es: " + numeroFactura);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Factura");
        }
    }

    private static void CodigoClienteFactura() {
        try {
            WebElement clienteField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NUMEROCLIENTE")));
            clienteField.clear();
            Thread.sleep(500);
            // Reemplaza con NUMERO_CLIENTE si deseas usar la misma variable o especifica otra
            clienteField.sendKeys(NUMERO_CLIENTE);
            Thread.sleep(200);
            clienteField.sendKeys(Keys.TAB);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al llenar el campo del número de cliente para la factura.");
        }
    }

    private static void MonedaAFacturar() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            List<String> opcionesValidas = List.of("PESOS", "DÓLARES");
            List<WebElement> opcionesValidasDisponibles = new ArrayList<>();

            for (WebElement opcion : opcionesDisponibles) {
                if (opcionesValidas.contains(opcion.getText())) {
                    opcionesValidasDisponibles.add(opcion);
                }
            }

            if (!opcionesValidasDisponibles.isEmpty()) {
                Random random = new Random();
                WebElement opcionAleatoria = opcionesValidasDisponibles.get(
                        random.nextInt(opcionesValidasDisponibles.size()));
                comboBox.selectByVisibleText(opcionAleatoria.getText());
                System.out.println("Se seleccionó la moneda a facturar: " + opcionAleatoria.getText());
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda a Facturar");
        }
    }

    private static void FiltroMoneda() {
        try {
            WebElement filtroMonedaCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATMONEDAS_FILTRO")));
            Select comboBox = new Select(filtroMonedaCombo);

            String valorInicialComboBox = comboBox.getFirstSelectedOption().getText().trim().toUpperCase();
            System.out.println("Valor actual del filtro de moneda leído: " + valorInicialComboBox);

            if (monedaSeleccionada == null || monedaSeleccionada.isEmpty()) {
                System.out.println("monedaSeleccionada no tiene un valor válido. Se mantiene el valor actual del filtro.");
                return;
            }

            monedaSeleccionada = monedaSeleccionada.trim().toUpperCase();
            if (!valorInicialComboBox.equals(monedaSeleccionada)) {
                String opcionContraria = valorInicialComboBox.equals("PESOS") ? "DÓLARES" : "PESOS";

                boolean opcionExiste = comboBox.getOptions().stream()
                        .anyMatch(option -> option.getText().trim().equalsIgnoreCase(opcionContraria));

                if (opcionExiste) {
                    comboBox.selectByVisibleText(opcionContraria);
                    wait.until(ExpectedConditions.textToBePresentInElement(filtroMonedaCombo, opcionContraria));
                    System.out.println("Se seleccionó la opción contraria en el filtro de moneda: " + opcionContraria);
                } else {
                    System.out.println("No se encontró la opción contraria en el combo box.");
                }
            } else {
                System.out.println("El valor inicial del filtro de moneda ya coincide con el valor deseado: " + monedaSeleccionada);
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el filtro de Moneda");
        }
    }

    private static void CampoBusqueda() {
        try {
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_BUSQUEDAGENERAL")));
            busquedaField.clear();
            busquedaField.sendKeys(numeroViajeCliente);
            System.out.println("Se ingresó el valor de búsqueda: " + numeroViajeCliente);

            WebElement buscarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_BUSCAR")));
            buscarButton.click();
            System.out.println("Se presionó el botón de búsqueda");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el valor en el campo de búsqueda general");
        }
    }

    private static void SelecionaFactura() {
        try {
            WebElement facturaCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("_0_TABLE_PROVIAJES_0")));
            if (!facturaCheckbox.isSelected()) {
                facturaCheckbox.click();
            }
            System.out.println("Se seleccionó la factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la factura");
        }
    }

    private static void AceptarFactura() {
        int intentos = 0;
        boolean exito = false;
        while (intentos < 3 && !exito) {
            try {
                WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("BTN_ACEPTAR")));
                aceptarButton.click();
                System.out.println("Se presionó el botón de aceptar factura en el intento " + (intentos + 1));
                exito = true;
            } catch (Exception e) {
                intentos++;
                if (intentos == 3) {
                    UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar factura");
                }
            }
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

            NumeroFactura();
            AceptarFactura();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }



    private static void AceptarTimbre() {
        try {
            WebElement aceptarEDIButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_YES")));
            aceptarEDIButton.click();
            System.out.println("Se presionó el botón de aceptar timbre");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar timbre");
        }
    }

    private static void EnvioCorreoFactura() {
        try {
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo.");
            } else {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo.");
            }
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    private static void AceptarPoliza() {
        try {
            WebElement aceptarPolizaButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_OK")));
            aceptarPolizaButton.click();
            System.out.println("Se presionó el botón de aceptar póliza");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar póliza");
        }
    }

    private static void AceptarImpresion() {
        try {
            WebElement regresarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_REGRESAR")));
            regresarButton.click();
            System.out.println("Se presionó el botón de regresar después de la impresión");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de regresar después de la impresión");
        }
    }

    // =====================================================
    // BLOQUE 3: SUSTITUCIÓN DE FACTURA
    // =====================================================

    private static void BusquedaFacturaListado() {
        try {
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='search'][aria-controls='TABLE_ProFacturasPorViaje']")));
            busquedaField.clear();
            busquedaField.sendKeys(numeroFactura);
            System.out.println("Se ingresó el folio de la factura para su búsqueda: " + numeroFactura);

            WebElement buscarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("z_BTN_APLICAR_IMG")));
            buscarButton.click();
            System.out.println("Se presionó el botón de búsqueda");

            WebElement resultadoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@id='TABLE_ProFacturasPorViaje']")));
            System.out.println("La búsqueda se completó y los resultados están visibles.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la factura en el listado");
        }
    }

    @Step("Seleccionar Factura en el Listado (para cancelar)")
    private static void SeleccionarFactura() {
        try {
            WebElement fila = driver.findElement(By.xpath(
                    "//table[@id='TABLE_ProFacturasPorViaje']//tr[td[1][contains(text(),'" + numeroFactura + "')]]"));
            fila.click();
            System.out.println("Factura seleccionada: " + numeroFactura);
        } catch (NoSuchElementException e) {
            UtilidadesAllure.manejoError(driver, e,
                    "No se encontró la factura con el número: " + numeroFactura);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al seleccionar la factura en el listado");
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
        }
    }

    public void SeleccionaMotivoCancelacion() {
        try {
            WebElement combo = driver.findElement(By.id("COMBO_MOTIVOCANCELACION"));
            Select select = new Select(combo);
            select.selectByIndex(0);

            WebElement comentarioField = driver.findElement(By.id("EDT_MOTIVO"));
            comentarioField.clear();
            comentarioField.sendKeys("Cancelación de factura por motivo de prueba automática: " + numeroFactura);
            System.out.println(comentarioField);

            WebElement botonAceptar = driver.findElement(By.id("BTN_ACEPTAR"));
            botonAceptar.click();
        } catch (Exception e) {
            System.out.println("Error al seleccionar la primera opción: " + e.getMessage());
        }
    }

    @Step("Mensaje de Sustitución Requerida")
    private static void MensajeSustitucionRequerida() {
        try {
            WebElement botonSi = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("tzBTN_YES")));
            botonSi.click();
            System.out.println("Se presionó el botón de Sí para la sustitución requerida");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al presionar el botón de Sí para la sustitución requerida");
        }
    }

    private static void FiltroMonedaSustitucion() {
        try {
            WebElement filtroMonedaCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATMONEDAS_FILTRO")));
            Select comboBox = new Select(filtroMonedaCombo);

            String valorInicialComboBox = comboBox.getFirstSelectedOption().getText().trim().toUpperCase();
            System.out.println("Valor actual del filtro de moneda leído: " + valorInicialComboBox);

            if (monedaSeleccionada == null || monedaSeleccionada.isEmpty()) {
                System.out.println("monedaSeleccionada no tiene un valor válido. Se mantiene el valor actual.");
                return;
            }

            monedaSeleccionada = monedaSeleccionada.trim().toUpperCase();
            if (!valorInicialComboBox.equals(monedaSeleccionada)) {
                String opcionContraria = valorInicialComboBox.equals("PESOS") ? "DÓLARES" : "PESOS";

                boolean opcionExiste = comboBox.getOptions().stream()
                        .anyMatch(option -> option.getText().trim().equalsIgnoreCase(opcionContraria));

                if (opcionExiste) {
                    comboBox.selectByVisibleText(opcionContraria);
                    wait.until(ExpectedConditions.textToBePresentInElement(filtroMonedaCombo, opcionContraria));
                    System.out.println("Se seleccionó la opción contraria en el filtro de moneda: " + opcionContraria);
                } else {
                    System.out.println("No se encontró la opción contraria en el combo box.");
                }
            } else {
                System.out.println("El valor inicial del filtro de moneda ya coincide con el valor deseado: " + monedaSeleccionada);
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el filtro de Moneda");
        }
    }

    @Step("Seleccionar Factura para Sustitución")
    private static void SeleccionFacturaSutitucion() {
        try {
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_BUSQUEDAGENERAL")));
            busquedaField.clear();
            busquedaField.sendKeys(numeroViajeCliente);
            System.out.println("Se ingresó el valor de búsqueda: " + numeroViajeCliente);

            WebElement buscarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_BUSCAR")));
            buscarButton.click();
            System.out.println("Se presionó el botón de búsqueda");

            WebElement facturaCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("_0_TABLE_PROVIAJES_0")));
            if (!facturaCheckbox.isSelected()) {
                facturaCheckbox.click();
            }
            System.out.println("Se seleccionó la factura");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al ingresar el valor en el campo de búsqueda para la sustitución");
        }
    }

    private static void AceptarFacturaSustituida() {
        int intentos = 0;
        boolean exito = false;
        while (intentos < 3 && !exito) {
            try {
                WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("z_BTN_ACEPTAR_IMG")));
                aceptarButton.click();
                System.out.println("Se presionó el botón de aceptar factura en el intento " + (intentos + 1));
                exito = true;
            } catch (Exception e) {
                intentos++;
                if (intentos == 3) {
                    UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar factura (sustitución)");
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

    @Step("Enviar Correo Después de Sustitución")
    private static void CorreoDesspuesSustitucion() {
        try {
            Random random = new Random();
            boolean elegirSi = random.nextBoolean();

            WebElement boton;
            if (elegirSi) {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("tzBTN_YES")));
                System.out.println("Se eligió la opción Sí para el envío del correo después de la sustitución.");
            } else {
                boton = wait.until(ExpectedConditions.elementToBeClickable(By.id("tzBTN_NO")));
                System.out.println("Se eligió la opción No para el envío del correo después de la sustitución.");
            }
            boton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al elegir entre Sí o No para el envío del correo después de la sustitución");
        }
    }

    @Step("Aceptar Cancelación en el SAT")
    private static void CancelacionSAT() {
        try {
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("tzBTN_YES")));
            aceptarButton.click();
            System.out.println("Se presionó el botón de aceptar para la cancelación en el SAT");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al presionar el botón de aceptar para la cancelación en el SAT");
        }
    }

    @Step("Aceptar Cancelación en el SAT (Segundo Mensaje)")
    private static void CancelacionSAT2() {
        try {
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("tzBTN_YES")));
            aceptarButton.click();
            System.out.println("Se presionó el botón de aceptar para la cancelación en el SAT (segundo mensaje)");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al presionar el botón de aceptar para la cancelación en el SAT (segundo mensaje)");
        }
    }

    @Step("Aceptar Cancelación en el SAT (Tercer Mensaje)")
    private static void CancelacionSAT3() {
        try {
            WebElement aceptarButton = driver.findElement(By.id("BTN_OK"));
            if (aceptarButton.isDisplayed() && aceptarButton.isEnabled()) {
                aceptarButton.click();
                System.out.println("Se hizo clic en el botón de aceptar para la cancelación en el SAT.");
            } else {
                System.out.println("El botón 'tzBTN_YES' está presente pero no es clicable.");
            }
        } catch (NoSuchElementException e) {
            System.out.println("No se encontró el botón 'tzBTN_YES' en la página.");
        } catch (Exception e) {
            System.out.println("Error inesperado al presionar el botón de aceptar en la cancelación SAT: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón en la cancelación SAT.");
        }
    }
}
