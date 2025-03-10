package Trafico;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Allure;
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
public class LiquidacionOperativa   {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // =========================================================================
    // VARIABLES CONFIGURABLES
    // =========================================================================
    private static final String NUMERO_CLIENTE   = Variables.CLIENTE;  // Número de cliente
    private static final String NUMERO_RUTA      = Variables.RUTA;  // Número de ruta
    private static final String TIPO_DOCUMENTO   = Variables.DocumentoTraslado;
    private static final String FOLIO_OPERADOR = Variables.Operador;
    // =========================================================================

    // Variable para almacenar el folio del viaje
    private static String folioGuardado;
    private static String numeroViajeGenerado;
    private static String FolioLiquidacion;


    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.softwareparatransporte.com/");
        driver.manage().window().maximize();
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
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio(driver, wait);
        InicioSesion.handleNovedadesScreen(wait);
    }

    @RepeatedTest(2)
    @Order(3)
    @Description("Crear y timbrar el viaje con Carta Porte CFDI - TR")
    public void testCrearViaje() {
        BotonModuloTrafico();
        BotonListadoViajes();
        BotonAgregarCartaPorte();
        TipoDocumentoTraslado();
        GuardarFolio();
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
        AceptarMensajeTimbre();
        EnvioCorreo();
        BotonImpresion();
        SelecionarCartaporteListado();
        Darsalida();
        Aceptarsalida();
        EnvioCorreoseguimiento();
        Darllegada();
        Aceptarllegada();
        EnvioCorreoseguimientollegada();

        ModuloTrafico();
        LiquidacionesOperativas();
        BotonGenerarLiquidacion();
        FolioLiquidacion();
        deseleccionarCampoFecha();
        TipoLiquidacion();
        Operador();
        capturaFolioLiquidacion();
        PestanaCalculoLiquidacion();
        AceptarLiquidacion();
        CierreLiquidacion();
        FechaCierre();
        Poliza();

        //PAGO

        BusquedaLiquidacion();
        SeleccionarLiquidacion();
        BotonAutorizar();
        BotonPagar();
        MensajeNomina();
        MensajeTimbrePago();
        MensajePoliza();




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
    // MÉTODOS DE FLUJO
    // =====================================================

    private static void BotonModuloTrafico() {
        try {
            WebElement ModuloBotonTrafico = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1.jpg')]")));
            ModuloBotonTrafico.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Tráfico no funciona.");
        }
    }

    private static void BotonListadoViajes() {
        try {
            WebElement ListadoBoton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.jpg')]]")));
            ListadoBoton.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Listado de Viajes no funciona.");
        }
    }

    private static void BotonAgregarCartaPorte() {
        try {
            WebElement additionalButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_AGREGAR")));
            additionalButton.click();
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Agregar Carta Porte no funciona.");
        }
    }

    @Step("Seleccionar Tipo de Documento (variable TIPO_DOCUMENTO)")
    public void TipoDocumentoTraslado() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATTIPOSDOCUMENTOS")));
            WebElement opcionTraslado = tipoDocumentoCombo.findElement(
                    By.xpath(".//option[text()='" + TIPO_DOCUMENTO + "']"));
            opcionTraslado.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "No se pudo seleccionar el Tipo de Documento: " + TIPO_DOCUMENTO);
        }
    }

    @Step("Guardar Folio del Viaje")
    private void GuardarFolio() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_FOLIO")));
            folioGuardado = folioField.getAttribute("value"); // Guardar el valor del folio
            System.out.println("El folio guardado es: " + folioGuardado);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al guardar el folio del viaje");
            System.out.println("Error al guardar el folio del viaje: " + e.getMessage());
        }
    }



    @Step("Asignar Número de Viaje (concatena 'PA' al folio)")
    private void NumeroViajeCliente() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_FOLIO")));
            String folioValue = folioField.getAttribute("value");

            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NOVIAJECLIENTE")));

            // Construimos la cadena que concatenará el folio con "PA"
            String numeroViaje = "PA" + folioValue;

            // Limpiamos y escribimos en el campo
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViaje);

            // Guardamos el valor en la variable de clase
            numeroViajeGenerado = numeroViaje;

            // Puedes imprimirlo para verificar
            System.out.println("Número de Viaje generado: " + numeroViajeGenerado);

        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }


    @Step("Asignar Cliente al Viaje (variable NUMERO_CLIENTE)")
    private void CodigoCliente() {
        try {
            WebElement NumeroClienteField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NUMEROCLIENTE")));
            NumeroClienteField.click();
            NumeroClienteField.sendKeys(NUMERO_CLIENTE);
            NumeroClienteField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }

    @Step("Seleccionar Moneda (PESOS / DÓLARES)")
    private void MonedaCartaPorte() {
        try {
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("COMBO_CATMONEDAS")));
            Select comboBox = new Select(tipoDocumentoCombo);

            List<WebElement> opcionesDisponibles = comboBox.getOptions();
            // Lista de monedas que se consideran válidas:
            List<String> opcionesValidas = List.of("PESOS", "DÓLARES");
            List<WebElement> opcionesValidasDisponibles = new ArrayList<>();

            for (WebElement opcion : opcionesDisponibles) {
                if (opcionesValidas.contains(opcion.getText().trim().toUpperCase())) {
                    opcionesValidasDisponibles.add(opcion);
                }
            }

            if (!opcionesValidasDisponibles.isEmpty()) {
                Random random = new Random();
                WebElement opcionAleatoria = opcionesValidasDisponibles
                        .get(random.nextInt(opcionesValidasDisponibles.size()));
                comboBox.selectByVisibleText(opcionAleatoria.getText());
                System.out.println("Se seleccionó la opción: " + opcionAleatoria.getText());
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda");
        }
    }

    @Step("Asignar Folio de Ruta (variable NUMERO_RUTA)")
    private void FolioRuta() {
        try {
            WebElement folioRutaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_FOLIORUTA")));
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
            WebElement numeroEconomicoConvoyField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NUMEROECONOMICOCONVOY")));
            numeroEconomicoConvoyField.sendKeys("E3");
            numeroEconomicoConvoyField.sendKeys(Keys.ENTER);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al manejar el Número Económico del Convoy");
        }
    }

    @Step("Seleccionar Pestaña de Materiales Carga")
    private void SeleccionarPestanaMateriales() {
        try {
            WebElement pestanaMateriales = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("TAB_TAB1_2")));
            WebElement linkPestana = pestanaMateriales.findElement(By.tagName("a"));
            linkPestana.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al seleccionar la pestaña de Materiales Carga");
        }
    }

    @Step("Manejar Botón Importar Materiales")
    private void BotonImportarMaterial() {
        try {
            WebElement botonImportar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_IMPORTARMATERIALES")));
            botonImportar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Importar Materiales");
        }
    }

    @Step("Importar Archivo de Materiales")
    private void ImportacionMaterial() {
        try {
            WebElement inputArchivo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_IMPORTARMATERIALES_ARCHIVO")));

            // Ajusta la ruta del archivo a tu escenario
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
                WebElement iconoExito = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("IMG_ICONO_EXITO")));
                if (iconoExito.isDisplayed()) {
                    System.out.println("Importación de materiales exitosa.");
                }
            } catch (TimeoutException e) {
                WebElement iconoError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("IMG_IMPORTARMATERIALES_ERROR")));
                if (iconoError.isDisplayed()) {
                    UtilidadesAllure.capturaImagen(driver);
                    throw new Exception("La importación de materiales falló, se mostró el icono de error.");
                }
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al importar el archivo de materiales");
        }
    }

    @Step("Aceptar Importación de Materiales")
    private void BotonAceptarImportacion() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_ACEPTAR")));
            botonAceptar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Aceptar después de la importación de materiales");
        }
    }

    @Step("Aceptar Viaje")
    private void BotonAceptarViaje() {
        try {
            WebElement botonAceptarViaje = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_ACEPTAR")));
            botonAceptarViaje.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Aceptar para confirmar el viaje");
        }
    }

    @Step("Aceptar Mensaje de Timbre (BTN_YES)")
    private void AceptarMensajeTimbre() {
        try {
            WebElement botonAceptarTimbre = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_YES")));
            botonAceptarTimbre.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Sí para aceptar el mensaje de timbre");
        }
    }

    @Step("Enviar Correo de Timbre (Sí/No)")
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
            UtilidadesAllure.manejoError(driver, e,
                    "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    @Step("Cerrar Ventana de Impresión (BTN_REGRESAR)")
    private void BotonImpresion() {
        try {
            WebElement botonRegresar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_REGRESAR")));
            botonRegresar.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e,
                    "Error al hacer clic en el botón Regresar para impresión");
        }
    }


    @Step("Seleccionar Carta Porte Listado")
    private void SelecionarCartaporteListado() {
        try {
            // Buscar el campo de búsqueda y llenar con el valor del folio guardado
            WebElement campoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#TABLE_ProViajes_filter input[type='search']")));
            campoBusqueda.clear();
            campoBusqueda.sendKeys(folioGuardado);
            System.out.println("Folio ingresado en la búsqueda: " + folioGuardado);

            // Esperar un momento para que la tabla se filtre
            Thread.sleep(2000);

            // Buscar en la tabla el elemento que contenga el folio
            WebElement tablaViajes = driver.findElement(By.id("TABLE_ProViajes"));
            List<WebElement> filas = tablaViajes.findElements(By.tagName("tr"));

            // Iterar por las filas para encontrar el folio y hacer clic
            for (WebElement fila : filas) {
                if (fila.getText().contains(folioGuardado)) {
                    fila.click();
                    System.out.println("Se seleccionó el folio en la tabla: " + folioGuardado);
                    break;
                }
            }

        } catch (NoSuchElementException | TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la Carta Porte del listado");
            System.out.println("Error al seleccionar la Carta Porte del listado: " + e.getMessage());
        }

        // Espera de 5 segundos al final del método
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("La espera fue interrumpida: " + e.getMessage());
        }
    }

    public void Darsalida() {
        try {

            Thread.sleep(3000);
            // Localiza el <a> que contiene la palabra "Salida"
            WebElement linkSalida = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//td[contains(@class,'COL_SALIDA1')]//a[contains(text(),'Salida')]")
                    )
            );
            linkSalida.click();
        } catch (Exception e) {
            // Manejo de error (usando tu clase UtilidadesAllure o un simple e.printStackTrace())
            e.printStackTrace();
        }
    }

    public void Aceptarsalida() {
        try {
            // Localiza el primer botón con id = BTN_ACEPTAR
            // asumiendo que es el primero en el DOM ( [1] ) en ese momento
            WebElement btnAceptarSalida = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//input[@id='BTN_ACEPTAR'])[1]")
                    )
            );
            btnAceptarSalida.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Step("Enviar Correo de Timbre (Sí/No)")
    private void EnvioCorreoseguimiento() {
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
            UtilidadesAllure.manejoError(driver, e,
                    "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }

    public void Darllegada() {
        try {
            // Localiza el <a> que contiene la palabra "Llegada"
            WebElement linkLlegada = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//td[contains(@class,'COL_LLEGADA1')]//a[contains(text(),'Llegada')]")
                    )
            );
            linkLlegada.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Aceptarllegada() {
        try {
            WebElement btnAceptarLlegada = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("BTN_ACEPTAR")
            ));
            btnAceptarLlegada.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Step("Enviar Correo de Timbre (Sí/No)")
    private void EnvioCorreoseguimientollegada() {
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
            UtilidadesAllure.manejoError(driver, e,
                    "Error al elegir entre Sí o No para el envío del correo de timbre");
        }
    }
    private void ModuloTrafico() {
        try {
            WebElement moduloTrafico = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[contains(@src, 'TRAFICO')]")));
            moduloTrafico.click();
            System.out.println("Se ingresó al módulo de Tráfico.");
        } catch (Exception e) {
            System.err.println("Error al ingresar al módulo de Tráfico: " + e.getMessage());
        }
    }

    private void LiquidacionesOperativas() {
        try {
            WebElement liquidacionesButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//img[@alt='Liquidaciones']")));
            liquidacionesButton.click();
            System.out.println("Se accedió a Liquidaciones Fiscales.");
        } catch (Exception e) {
            System.err.println("Error al seleccionar Liquidaciones Fiscales: " + e.getMessage());
        }
    }

    @Step("Clic en el botón Generar Liquidación")
    public void BotonGenerarLiquidacion() {
        try {
            // Usar ID: "BTN_GENERAR"
            WebElement botonGenerar = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("BTN_GENERAR"))
            );
            botonGenerar.click();
            System.out.println("Se dio clic en el botón Generar Liquidación");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("Error al dar clic en el botón Generar Liquidación: " + e.getMessage());
        }
    }


    public void FolioLiquidacion() {
        boolean folioAsignado = false; // Bandera para controlar el bucle
        Random random = new Random(); // Generador de números aleatorios

        while (!folioAsignado) {
            try {
                // Generar un valor aleatorio entre 65000 y 75000
                int folioAleatorio = 65000 + random.nextInt(75000 - 65000 + 1);
                String folioStr = String.valueOf(folioAleatorio);

                // Esperar a que el input con el full XPath sea visible
                WebElement folioField = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/input")
                        )
                );

                // Asignar el valor al campo usando JavaScript
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].value = arguments[1];", folioField, folioStr);

                System.out.println("Se asignó el folio: " + folioStr);

                // Intentar manejar la alerta si aparece
                try {
                    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                    String alertText = alert.getText();
                    System.out.println("Alerta detectada: " + alertText);
                    alert.accept(); // Aceptar la alerta

                    // Generar otro valor aleatorio y repetir el proceso
                    System.out.println("Generando otro folio aleatorio...");
                } catch (TimeoutException e) {
                    // No se detectó ninguna alerta, el folio fue asignado correctamente
                    System.out.println("No se detectó ninguna alerta. Folio asignado: " + folioStr);

                    // Simular la tecla "Tab" para salir del campo (solo si no hay alerta)
                    folioField.sendKeys(Keys.TAB);
                    folioAsignado = true; // Salir del bucle
                }

                // Pausa de 2s para ver efecto (opcional, solo para depuración)
                Thread.sleep(2000);

            } catch (TimeoutException e) {
                System.err.println("Timeout esperando que EDT_FOLIO sea asignado. Error: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    public void deseleccionarCampoFecha() {
        try {
            WebElement body = driver.findElement(By.tagName("body")); // Clic en el fondo de la página
            body.click();
            body.click();
            System.out.println("Campo EDT_FECHA deseleccionado con un clic en el body.");
        } catch (Exception e) {
            System.err.println("No se pudo deseleccionar el campo EDT_FECHA: " + e.getMessage());
        }
    }





    @Step("Seleccionar la opción 4 en Tipo de Liquidación")
    public void TipoLiquidacion() {
        try {
            // 1. Localizamos el elemento <select>
            WebElement comboTipoLiquidacion = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("COMBO_TIPOLIQUIDACION")
                    )
            );

            // 2. Creamos un objeto Select a partir del <select>
            Select select = new Select(comboTipoLiquidacion);

            // 3. Seleccionamos por 'value' = "4"
            select.selectByValue("4");

            // Opcional: confirmar qué opción se seleccionó
            System.out.println("Se seleccionó la opción 4 en Tipo de Liquidación.");

            Thread.sleep(2000);
        } catch (Exception e) {
            // Manejo de excepción (puedes usar tu clase UtilidadesAllure o simplemente loguear el error)
            System.err.println("Error al seleccionar la opción 4 en Tipo de Liquidación: " + e.getMessage());
        }
    }


    @Step("Ingresar Operador (FOLIO_OPERADOR) en el campo EDT_NUMEROOPERADOR")
    public void Operador() {
        try {
            // Esperamos a que el campo esté visible usando el XPath
            WebElement operadorField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[7]/input")));

            // Limpiamos y escribimos la variable FOLIO_OPERADOR
            operadorField.clear();
            operadorField.sendKeys(FOLIO_OPERADOR);

            // Opcional: Enviar TAB
            operadorField.sendKeys(Keys.TAB);

            // Esperar 2 segundos
            Thread.sleep(2000);

            System.out.println("Se ingresó el operador: " + FOLIO_OPERADOR);
        } catch (InterruptedException e) {
            // Si la pausa es interrumpida, restaurar el estado de la interrupción
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al ingresar el Operador: " + e.getMessage());
            // UtilidadesAllure.manejoError(driver, e, "Error al ingresar el Operador");
        }
    }

    @Step("Capturar el folio de liquidación y guardarlo en la variable FolioLiquidacion")
    public static void capturaFolioLiquidacion() {
        try {
            // Esperamos a que el campo esté visible usando el XPath
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/input")));

            // Capturamos el valor del campo y lo asignamos a la variable estática
            FolioLiquidacion = folioField.getAttribute("value");

            // Imprimimos el valor capturado (opcional)
            System.out.println("El folio de liquidación capturado es: " + FolioLiquidacion);

        } catch (Exception e) {
            System.err.println("Error al capturar el folio de liquidación: " + e.getMessage());
            // UtilidadesAllure.manejoError(driver, e, "Error al capturar el folio de liquidación");
        }
    }

    public void PestanaCalculoLiquidacion() {
        try {
            // Espera a que la celda con id=TAB_TAB1_5 sea clickeable
            WebElement tabTd = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("TAB_TAB1_5"))
            );
            // Dentro de ella, encuentra el <a>
            WebElement link = tabTd.findElement(By.tagName("a"));
            // Hace clic en el link
            link.click();
            Thread.sleep(3000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Step("Aceptar la Liquidación")
    public void AceptarLiquidacion() {
        try {
            // Localiza el botón "Aceptar" dentro del <a> con id="BTN_ACEPTAR"
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a#BTN_ACEPTAR")
            ));
            botonAceptar.click();
            System.out.println("Se hizo clic en Aceptar la Liquidación");
        } catch (Exception e) {
            System.err.println("Error al dar clic en Aceptar la Liquidación: " + e.getMessage());
        }
    }

    @Step("Cerrar la Liquidación")
    public void CierreLiquidacion() {
        try {
            // Localiza el botón "Si" con id="BTN_YES"
            WebElement botonCerrar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input#BTN_YES")
            ));
            botonCerrar.click();
            System.out.println("Se hizo clic en Cerrar la Liquidación (Botón 'Si')");
        } catch (Exception e) {
            System.err.println("Error al dar clic en Cerrar la Liquidación: " + e.getMessage());
        }
    }

    @Step("Fecha de Cierre: Aceptar")
    public void FechaCierre() {
        try {
            // Localiza el botón "Aceptar" con id="BTN_ACEPTAR"
            WebElement botonAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("input#BTN_ACEPTAR"))
            );
            botonAceptar.click();
            System.out.println("Se hizo clic en Aceptar (Fecha Cierre)");
        } catch (Exception e) {
            System.err.println("Error al dar clic en Aceptar (Fecha Cierre): " + e.getMessage());
        }
    }

    @Step("Aceptar Póliza")
    public void Poliza() {
        try {
            // Localiza el botón "Aceptar" con id="BTN_OK"
            WebElement botonAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("input#BTN_OK"))
            );
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón Aceptar (Póliza).");
        } catch (Exception e) {
            System.err.println("Error al dar clic en el botón Aceptar (Póliza): " + e.getMessage());
        }
    }

    @Step("Realizar búsqueda de liquidación usando el folio capturado")
    public void BusquedaLiquidacion() {
        try {
            // Localizar el campo de búsqueda usando el XPath proporcionado
            WebElement campoBusqueda = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[3]/div/table/tbody/tr/td/div/div/div[1]/div[1]/label/input"));

            // Limpiar el campo de búsqueda (opcional, dependiendo de tu caso)
            campoBusqueda.clear();

            // Introducir el valor de FolioLiquidacion en el campo de búsqueda
            campoBusqueda.sendKeys(FolioLiquidacion);

            // Opcional: Enviar la tecla ENTER para realizar la búsqueda automáticamente
            campoBusqueda.sendKeys(Keys.ENTER);

            System.out.println("Búsqueda realizada con el folio: " + FolioLiquidacion);

        } catch (Exception e) {
            System.err.println("Error al realizar la búsqueda de liquidación: " + e.getMessage());
        }
    }

    @Step("Seleccionar la liquidación que coincida con el folio capturado")
    public void SeleccionarLiquidacion() {
        try {
            // Localizar la tabla usando el XPath proporcionado
            WebElement tabla = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[3]/div/table/tbody/tr/td/div/div/div[2]/div[2]"));

            // Buscar todas las filas de la tabla
            List<WebElement> filas = tabla.findElements(By.tagName("tr"));

            // Bandera para indicar si se encontró el folio
            boolean folioEncontrado = false;

            // Recorrer las filas de la tabla
            for (WebElement fila : filas) {
                // Obtener el texto de la fila
                String textoFila = fila.getText();

                // Verificar si el texto de la fila contiene el folio de liquidación
                if (textoFila.contains(FolioLiquidacion)) {
                    // Hacer clic en la fila que contiene el folio
                    fila.click();
                    folioEncontrado = true;
                    System.out.println("Se seleccionó la liquidación con folio: " + FolioLiquidacion);
                    break; // Salir del bucle una vez que se encuentra el folio
                }
            }

            // Si no se encontró el folio, mostrar un mensaje
            if (!folioEncontrado) {
                System.out.println("No se encontró ninguna liquidación con el folio: " + FolioLiquidacion);
            }

        } catch (Exception e) {
            System.err.println("Error al seleccionar la liquidación: " + e.getMessage());
        }
    }

    @Step("Presionar el botón de autorización, confirmar y manejar la alerta")
    public void BotonAutorizar() {
        try {
            // Presionar el botón de autorización
            WebElement botonAutorizar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/a/span/span"));
            botonAutorizar.click();
            System.out.println("Se presionó el botón de autorización.");

            // Esperar a que el segundo botón esté visible y presionarlo
            WebElement botonConfirmar = driver.findElement(
                    By.xpath("/html/body/form/div[8]/table/tbody/tr/td/div[2]/div[1]/div/table/tbody/tr/td/a/span/span"));
            botonConfirmar.click();
            System.out.println("Se presionó el botón de confirmación.");

            // Manejar la alerta que aparece
            Alert alerta = driver.switchTo().alert(); // Cambiar el foco a la alerta
            String textoAlerta = alerta.getText(); // Obtener el texto de la alerta
            System.out.println("Texto de la alerta: " + textoAlerta);

            // Aceptar la alerta
            alerta.accept();
            System.out.println("Se aceptó la alerta.");

            Thread.sleep(3000);

        } catch (NoAlertPresentException e) {
            System.err.println("No se encontró ninguna alerta: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en el método BotonAutorizar: " + e.getMessage());
        }
    }

    @Step("Presionar el botón de pagar y luego confirmar el pago")
    public void BotonPagar() {
        try {
            // Presionar el primer botón de pagar
            WebElement botonPagar1 = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[1]/td/div[3]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/a/span/span"));
            botonPagar1.click();
            System.out.println("Se presionó el primer botón de pagar.");

            // Esperar un momento para que la página procese la acción (opcional)
            Thread.sleep(1000); // Ajusta el tiempo según sea necesario

            // Presionar el segundo botón de confirmación de pago
            WebElement botonPagar2 = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[1]/div/input"));
            botonPagar2.click();
            System.out.println("Se presionó el segundo botón de confirmación de pago.");

        } catch (InterruptedException e) {
            // Si la pausa es interrumpida, restaurar el estado de la interrupción
            Thread.currentThread().interrupt();
            System.err.println("La pausa fue interrumpida: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en el método BotonPagar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el campo de mensaje de nómina")
    public void MensajeNomina() {
        try {
            // Localizar el campo de mensaje de nómina usando el XPath proporcionado
            WebElement campoNomina = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/input"));

            // Hacer clic en el campo
            campoNomina.click();
            System.out.println("Se hizo clic en el campo de mensaje de nómina.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el campo de mensaje de nómina: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el campo de mensaje de timbre de pago")
    public void MensajeTimbrePago() {
        try {
            // Localizar el campo de mensaje de timbre de pago usando el XPath proporcionado
            WebElement campoTimbrePago = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/input"));

            // Hacer clic en el campo
            campoTimbrePago.click();
            System.out.println("Se hizo clic en el campo de mensaje de timbre de pago.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el campo de mensaje de timbre de pago: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el campo de mensaje de póliza")
    public void MensajePoliza() {
        try {
            // Localizar el campo de mensaje de póliza usando el XPath proporcionado
            WebElement campoPoliza = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input"));

            // Hacer clic en el campo
            campoPoliza.click();
            System.out.println("Se hizo clic en el campo de mensaje de póliza.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el campo de mensaje de póliza: " + e.getMessage());
        }
    }
}
