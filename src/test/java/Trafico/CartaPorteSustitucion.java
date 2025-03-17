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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartaPorteSustitucion {

    private static WebDriver driver;
    private static WebDriverWait wait;


    private static final String NUMERO_CLIENTE = Variables.CLIENTE;       // Número de cliente
    private static final String NUMERO_RUTA = Variables.RUTA;        // Número de ruta
    private static final String TIPO_DOCUMENTO = Variables.DocumentoTraslado;

    // Variables para almacenar valores reutilizables

    private static String folioGuardado;


    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.softwareparatransporte.com/");
        driver.manage().window().maximize(); // Maximizar la ventana para evitar problemas de visibilidad
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
    public void alertaTipoCambio() {
        InicioSesion.handleTipoCambio();
        InicioSesion.handleNovedadesScreen();
    }

    @Test
    @Order(3)
    @Description("Metodos para entrar al listado de viajes")
    public void EntrarAViajes() {
        BotonModuloTrafico();
        BotonListadoViajes();
    }

    @RepeatedTest(2)
    @Order(4)
    @Description("Se ingresa al listado de Viajes y se crea una Carta Porte, se factura y timbra el viaje.")
    public void testCrearViaje() {
        // Ejecuta el flujo de pruebas repetidamente para crear un viaje
        BotonAgregarCartaPorte(); // Maneja el botón adicional
        TipoDocumentoTraslado(); // Maneja el tipo de documento
        GuardarFolio(); // Guarda el folio del viaje
        NumeroViajeCliente(); // Maneja el número de viaje
        CodigoCliente(); // Asigna un cliente al viaje
        MonedaCartaPorte(); // Maneja la moneda
        FolioRuta(); // Maneja el folio de ruta
        NumeroEconomicoConvoy(); // Maneja el número económico del convoy
        SeleccionarPestanaMateriales(); // Selecciona la pestaña de materiales carga
        BotonImportarMaterial(); // Damos Click en el boton importar
        ImportacionMaterial(); // MMetodo que importa Materiales desde un excel
        BotonAceptarImportacion(); // Aceptamos la ventana dell importacion
        BotonAceptarViaje(); // Boton Aceptar Viaje
        BotonConcurrencia(); // Maneja el mensaje de concurrencia
        AceptarMensajeTimbre(); // Timbramos el viaje
        EnvioCorreo(); // Envio de Correo Aleatorio
        BotonImpresion(); // Manejamos Mensaje de iMPRECION
        SelecionarCartaporteListado();  // Seleccionar Carta Porte del listado
        SeleccionarOpcionCancelarSATCP(); // Seleccionar opción de Cancelar SAT CP
        SeleccionaMotivoCancelacion(); // Elige un motivo de cancelacion aleatorio para realizar la sustitucion o no.
        MensajeSustitucionRequerida(); // Acepta el mensaje de si se desea realizar la sustitucion.
        BotonAceptarViaje(); // Boton Aceptar Viaje
        BotonTimbreSustitucion(); // Timbra la factura sustituida.
        CancelacionSAT(); // Acepta la cancelacion de la factura anterior en el SAT.
        CancelacionSAT2(); // Acepta la cancelacion de la factura anterion en el SAT mensaje 2.
        CancelacionSAT3(); // Acepta el tercer mensaje de que sera sustituida la factura
        CancelacionSAT4(); // Acepta el tercer mensaje de que sera sustituida la factura



    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Método auxiliar para ejecutar comandos del sistema
    private static void ejecutarComando(String comando) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("sh", "-c", comando); // Usa "cmd /c" en Windows
        processBuilder.inheritIO(); // Hereda la entrada y salida del proceso principal

        Process process = processBuilder.start();
        int exitCode = process.waitFor(); // Espera a que el comando termine

        if (exitCode != 0) {
            throw new RuntimeException("Error al ejecutar el comando: " + comando);
        }
    }


    private static void BotonModuloTrafico() {
        try {
            // Espera explícita hasta que el botón (enlace) que contiene la imagen sea clicable
            WebElement ModuloBotonTrafico = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO1.jpg')]")));

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
            WebElement ListadoBoton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[img[contains(@src, '/GMTERPV8_WEB/Imagenes/TRAFICO/VIAJES1.jpg')]]")));

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
    public void TipoDocumentoTraslado() {
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

    @Step("Guardar Folio del Viaje")
    private void GuardarFolio() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            folioGuardado = folioField.getAttribute("value"); // Guardar el valor del folio en la variable
            System.out.println("El folio guardado es: " + folioGuardado);
        } catch (NoSuchElementException | TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al guardar el folio del viaje");
            System.out.println("Error al guardar el folio del viaje: " + e.getMessage());
        }
    }


    @Step("Manejar Número de Viaje")
    private void NumeroViajeCliente() {
        try {
            WebElement folioField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_FOLIO")));
            String folioValue = folioField.getAttribute("value");
            WebElement numeroViajeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NOVIAJECLIENTE")));
            String numeroViaje = "PA" + folioValue;
            numeroViajeField.clear();
            numeroViajeField.sendKeys(numeroViaje);
        } catch (TimeoutException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el Número de Viaje");
        }
    }

    @Step("Asignar Cliente al Viaje (variable NUMERO_CLIENTE)")
    private void CodigoCliente() {
        try {
            WebElement clienteField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EDT_NUMEROCLIENTE")));
            clienteField.click();
            clienteField.sendKeys(NUMERO_CLIENTE);
            clienteField.sendKeys(Keys.TAB);
            Thread.sleep(1000);
        } catch (TimeoutException | InterruptedException e) {
            UtilidadesAllure.manejoError(driver, e, "Error al asignar el cliente al viaje");
        }
    }

    @Step("Manejar Moneda")
    private void MonedaCartaPorte() {
        try {
            // Espera a que el combo box de moneda sea visible
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATMONEDAS")));
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
                System.out.println("Se seleccionó la opción: " + opcionAleatoria.getText());
            }
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar la Moneda");
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

            // Especifica la ruta al archivo que deseas importar
            String rutaArchivo = "C:\\Users\\Lasa9\\Desktop\\XLSXPruebas\\ImportarMaterialesPA.xlsx";
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
            GuardarFolio();
            NumeroViajeCliente();
            BotonAceptarViaje();
        } catch (TimeoutException e) {
            // Si no aparece el mensaje, continuar normalmente
            System.out.println("No se detectó mensaje de concurrencia.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al manejar el mensaje de concurrencia");
        }
    }

    @Step("Aceptar Mensaje Timbre")
    private void AceptarMensajeTimbre() {
        try {
            // Espera a que el botón de aceptar el mensaje de timbre sea clicable
            WebElement botonAceptarTimbre = wait.until(ExpectedConditions.elementToBeClickable(By.id("BTN_YES")));

            // Hacer clic en el botón de aceptar el mensaje de timbre
            botonAceptarTimbre.click();
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Sí para aceptar el mensaje de timbre");
        }
    }

    @Step("Enviar Correo de Timbre")
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

    @Step("Seleccionar Carta Porte Listado")
    private void SelecionarCartaporteListado() {
        try {
            // Buscar el campo de búsqueda y llenar con el valor del folio guardado
            WebElement campoBusqueda = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#TABLE_ProViajes_filter input[type='search']")));
            campoBusqueda.clear();
            campoBusqueda.sendKeys(folioGuardado);
            System.out.println("Folio ingresado en la búsqueda: " + folioGuardado);

            // Esperar un momento para que la tabla sea filtrada
            Thread.sleep(2000);

            // Buscar en la tabla el elemento que contenga el folio
            WebElement tablaViajes = driver.findElement(By.id("TABLE_ProViajes"));
            List<WebElement> filas = tablaViajes.findElements(By.tagName("tr"));

            // Iterar por las filas de la tabla para encontrar el folio buscado y hacer clic
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
    }

    public void SeleccionarOpcionCancelarSATCP() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // 1️⃣ Esperar que el menú "Cancelar" sea visible
            WebElement menuCancelar = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("MENU_CANCELAR")));

            // 2️⃣ Mover el cursor sobre el menú para desplegar opciones
            actions.moveToElement(menuCancelar).perform();
            Thread.sleep(1000); // Pequeña espera para asegurar el despliegue

            // 3️⃣ Esperar que el submenú "Cancelar SAT CP" sea visible
            WebElement opcionCancelarSATCP = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("OPT_CANCELARSATCP")));

            // 4️⃣ Hacer clic en la opción (con JavaScript como alternativa si es necesario)
            try {
                opcionCancelarSATCP.click();
            } catch (Exception e) {
                System.out.println("Click falló, intentando con JavaScript...");
                js.executeScript("arguments[0].click();", opcionCancelarSATCP);
            }

            System.out.println("✅ Opción 'Cancelar SAT CP' seleccionada con éxito.");

        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar 'Cancelar SAT CP': " + e.getMessage());
        }
    }

    public void SeleccionaMotivoCancelacion() {
        try {
            // Localizar el combo
            WebElement combo = driver.findElement(By.id("COMBO_MOTIVOCANCELACION"));

            // Seleccionar la primera opción
            Select select = new Select(combo);
            select.selectByIndex(0);

            // Espera explícita hasta que el campo de comentario sea visible
            WebElement comentarioField = driver.findElement(By.id("EDT_MOTIVO"));

            // Agregar un comentario al campo
            comentarioField.clear();
            comentarioField.sendKeys("Cancelación de Carta Porte por motivo de prueba automática." + folioGuardado);
            System.out.println(comentarioField);

            // Confirmar la selección presionando el botón "Aceptar"
            WebElement botonAceptar = driver.findElement(By.id("BTN_ACEPTAR"));
            botonAceptar.click();

        } catch (Exception e) {
            System.out.println("Error al seleccionar la primera opción: " + e.getMessage());
        }
    }

    @Step("Mensaje de Sustitución Requerida")
    private static void MensajeSustitucionRequerida() {
        try {
            // Espera explícita hasta que el botón de Sí sea clicable
            WebElement botonSi = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("tzBTN_YES")));


            botonSi.click();
            System.out.println("Se presionó el botón de Sí para la sustitución requerida");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de Sí para la sustitución requerida");
            System.out.println("Error al presionar el botón de Sí para la sustitución requerida");
        }
    }


    private void BotonTimbreSustitucion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Espera 3 segundos antes de continuar
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


    @Step("Aceptar Cancelación en el SAT")
    private static void CancelacionSAT() {
        try {
            // Espera explícita hasta que el botón de aceptar sea clicable
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("tzBTN_YES")));

            // Hacer clic en el botón de aceptar
            aceptarButton.click();
            System.out.println("Se presionó el botón de aceptar para la cancelación en el SAT");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar para la cancelación en el SAT");
            System.out.println("Error al presionar el botón de aceptar para la cancelación en el SAT");
        }
    }

    @Step("Aceptar Cancelación en el SAT (Segundo Mensaje)")
    private static void CancelacionSAT2() {
        try {
            // Espera explícita hasta que el botón de aceptar sea clicable
            WebElement aceptarButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("tzBTN_YES")));

            // Hacer clic en el botón de aceptar
            aceptarButton.click();
            System.out.println("Se presionó el botón de aceptar para la cancelación en el SAT (segundo mensaje)");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón de aceptar para la cancelación en el SAT (segundo mensaje)");
            System.out.println("Error al presionar el botón de aceptar para la cancelación en el SAT (segundo mensaje)");
        }
    }

    @Step("Aceptar Cancelación en el SAT (Tercer Mensaje)")
    private static void CancelacionSAT3() {
        try {
            // Intentar encontrar el botón inmediatamente sin esperar
            WebElement aceptarButton = driver.findElement(By.id("BTN_OK"));

            // Verificar si el botón es visible y habilitado antes de hacer clic
            if (aceptarButton.isDisplayed() && aceptarButton.isEnabled()) {
                aceptarButton.click();
                System.out.println("Se hizo clic en el botón de aceptar para la cancelación en el SAT.");
            } else {
                System.out.println("El botón 'tzBTN_YES' está presente pero no es clicable.");
            }

        } catch (NoSuchElementException e) {
            System.out.println("No se encontró el botón 'tzBTN_YES' en la página.");
        } catch (Exception e) {
            System.out.println(" Error inesperado al presionar el botón de aceptar en la cancelación SAT: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón en la cancelación SAT.");
        }
    }

    @Step("Aceptar Cancelación en el SAT (Cuarto Mensaje)")
    private static void CancelacionSAT4() {
        try {
            // Intentar encontrar el botón inmediatamente sin esperar
            WebElement aceptarButton = driver.findElement(By.id("BTN_OK"));

            // Verificar si el botón es visible y habilitado antes de hacer clic
            if (aceptarButton.isDisplayed() && aceptarButton.isEnabled()) {
                aceptarButton.click();
                System.out.println("Se hizo clic en el botón de aceptar para la cancelación en el SAT.");
            } else {
                System.out.println("El botón 'tzBTN_YES' está presente pero no es clicable.");
            }

        } catch (NoSuchElementException e) {
            System.out.println("No se encontró el botón 'tzBTN_YES' en la página.");
        } catch (Exception e) {
            System.out.println(" Error inesperado al presionar el botón de aceptar en la cancelación SAT: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e, "Error al presionar el botón en la cancelación SAT.");
        }
    }

}
