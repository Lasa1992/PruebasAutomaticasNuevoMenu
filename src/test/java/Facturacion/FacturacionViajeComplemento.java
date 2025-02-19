package Facturacion;
import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import Trafico.CreacionViajeTimbreIngreso;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacturacionViajeComplemento {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Variables para almacenar valores reutilizables
    private static String numeroViajeCliente;
    private static String monedaSeleccionada;


    @BeforeAll
    public static void setup() {
       // System.setProperty("webdriver.chrome.driver", "C:\\RepositorioPrueAuto\\Chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.softwareparatransporte.com/");
        driver.manage().window().maximize(); // Maximizar la ventana para evitar problemas de visibilidad
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
    @Description("Metodos para entrar al listado de viajes")
    public void EntrarAViajes() {
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
        EnvioCorreo(); // Envía un correo de confirmación aleatoriamente (Sí/No).
        BotonImpresion(); // Muestra el mensaje relacionado con la impresión.

        // Bloque donde se controla la facturación del viaje creado
        BotonModuloFacturacion(); // Selecciona el módulo de facturación en la interfaz de usuario.
        BotonFacturacionPorViaje(); // Selecciona la opción de facturación por viaje.
        BotonAgregarFactura(); // Da clic en el botón para agregar una nueva factura.
        CodigoClienteFactura(); // Introduce el código del cliente para la factura.
        MonedaAFacturar(); // Selecciona una moneda aleatoria para la factura.
        FiltroMoneda(); // Aplica un filtro de moneda basado en la moneda previamente seleccionada.
        CampoBusqueda(); // Realiza una búsqueda utilizando el número de viaje.
        SelecionaFactura(); // Selecciona la factura generada.
        AceptarFactura(); // Acepta la factura generada.
        AceptarTimbre(); // Acepta el proceso EDI relacionado con la factura.
        EnvioCorreoFactura(); // Envía un correo para la factura generada (Sí/No).
        AceptarPoliza(); // Acepta la póliza generada.
        AceptarImpresion(); // Acepta el cuadro de diálogo de impresión.

    }


    @AfterAll
    public static void tearDown() {
        // Cierra el navegador después de que todas las pruebas han terminado
        try {
            Thread.sleep(5000); // Reducido a 5 segundos para optimizar el tiempo de prueba
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reestablecer el estado de interrupción
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
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

    @Step("Manejar Tipo de Documento")
    public void TipoDocumentoIngreso() {
        try {
            // Espera que el combo box sea visible
            WebElement tipoDocumentoCombo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("COMBO_CATTIPOSDOCUMENTOS")));

            // Usar XPath para seleccionar la opción con el texto "CARTA PORTE CFDI - CP"
            WebElement opcionIngreso = tipoDocumentoCombo.findElement(By.xpath(".//option[text()='CARTA PORTE CFDI - HMO']"));

            // Hacer clic en la opción para seleccionarla
            opcionIngreso.click();

        } catch (Exception e) {
            // Manejo del error utilizando la clase UtilidadesAllure
            UtilidadesAllure.manejoError(driver, e, "No se pudo seleccionar el Tipo de Documento: CARTA PORTE CFDI - CP");
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
            NumeroCliente.sendKeys("000003");
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
            folioRutaField.sendKeys("000089");
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

            // Especifica la ruta al archivo que deseas importar
            String rutaArchivo = "C:\\Users\\UsuarioY\\Desktop\\Pruebas Automaticas\\XLSXPruebas\\ImportarMaterialesPA.xlsx";
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
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION1.jpg')]")));

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
                    By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/FACTURACION/PORVIAJE1.jpg')]")));

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
            // Espera explícita hasta que el campo de texto del número de cliente sea visible
            WebElement clienteField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("EDT_NUMEROCLIENTE")));

            // Limpiar el campo
            clienteField.clear();

            // Agregar una pequeña pausa antes de ingresar el valor para evitar que el script corra demasiado rápido
            Thread.sleep(500);

            // Llenar el campo con el número de cliente 000001
            clienteField.sendKeys("000003");
            Thread.sleep(200); // Pausa adicional para permitir el procesamiento adecuado

            clienteField.sendKeys(Keys.TAB);
        } catch (Exception e) {
            // Manejo general de excepciones
            UtilidadesAllure.manejoError(driver, e, "Error al llenar el campo del número de cliente para la factura.");
            System.out.println("Error al llenar el campo del número de cliente para la factura.");
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

}