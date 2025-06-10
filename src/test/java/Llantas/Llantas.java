package Llantas;

import Indicadores.InicioSesion;
import Utilidades.UtilidadesAllure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Llantas {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static String Economico; // Ejemplo de número económico

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
    @Description("Se genera un flujo completo de Llantas desde Requisición de Compras.")
    public void LlantasPA() throws InterruptedException {

        handleImageButton();
        handleSubMenuButton();

        //Creamos una llanta
        BotonAgregarLlanta();
        IngresarNumeroEconomico();
        IngresarCodigoModeloLlanta();
        IngresarPresionActual();
        SeleccionarEstatusLlantaNueva();
        IngresarVidaUtilAleatoria();
        IngresarCostoLlanta();
        BotonRegistrarLlanta();

        //Entramos a la pantalla de asignación de llantas
        BuscarLlanta();
        SeleccionarLlanta();
        BotonAsignar();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Llantas...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    private static void handleImageButton() {
        try {
            WebElement imageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/LLANTAS1')]")));
            imageButton.click();
            System.out.println("Botón Módulo Llantas seleccionado correctamente...");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Botón Módulo Llantas no funciona.");
            System.out.println("Botón Módulo Llantas no funciona.");
        }
    }

    private static void handleSubMenuButton() {
        try {
            WebElement subMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@src, '/GMTERPV8_WEB/Imagenes/LLANTAS/LLANTAS1')]")));
            subMenuButton.click();
            System.out.println("Botón Ordenes de Servicio seleccionado correctamente...");
        } catch (Exception e) {
            // Captura el mensaje de error, toma una captura de pantalla y lo despliega en el reporte de Allure.
            UtilidadesAllure.manejoError(driver, e, "Botón listado de Llantas no funciona.");
            System.out.println("Botón listado Llantas no funciona.");
        }
    }

    @Step("Seleccionar botón Agregar Llantas")
    private static void BotonAgregarLlanta() {
        try {
            WebElement botonAgregarImagen = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_REGISTRAR\"]")));
            botonAgregarImagen.click();
            System.out.println("Botón 'Agregar Llanta' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Agregar Llanta.");
        }
    }

    private static void IngresarNumeroEconomico() {
        try {
            // Generar número aleatorio de 6 dígitos
            int numero = new Random().nextInt(900000) + 100000; // entre 100000 y 999999
            String numeroEconomico = "PA" + numero;

            // Esperar el campo y enviar el número económico
            WebElement campoNumeroEconomico = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_NUMEROECONOMICO']")));
            campoNumeroEconomico.clear();
            campoNumeroEconomico.sendKeys(numeroEconomico);

            Economico = numeroEconomico; // Guardar el número económico para uso posterior

            System.out.println("Se ingresó el número económico: " + Economico);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el número económico.");
        }
    }

    private static void IngresarCodigoModeloLlanta() {
        try {
            // Esperar que el campo esté visible y sea clickeable
            WebElement campoCodigoLlanta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_CODIGOMODELOLLANTA']")));

            // Limpiar el campo y enviar el número 1
            campoCodigoLlanta.clear();
            campoCodigoLlanta.sendKeys("1");

            System.out.println("Se ingresó el número '1' en el campo Código Modelo Llanta.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar el código del modelo de llanta.");
        }
    }

    private static void IngresarPresionActual() {
        try {
            // Esperar que el campo esté visible y sea clickeable
            WebElement campoPresionActual = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_PRESIONACTUAL']")));

            // Limpiar el campo y enviar el valor 35
            campoPresionActual.clear();
            campoPresionActual.sendKeys("35");

            System.out.println("Se ingresó el valor '35' en el campo Presión Actual.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la presión actual.");
        }
    }

    private static void SeleccionarEstatusLlantaNueva() {
        try {
            // Esperar que el combo esté visible
            WebElement comboEstatusLlanta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='COMBO_CATESTATUSLLANTA']")));

            // Crear el objeto Select y seleccionar por texto visible
            Select selectEstatus = new Select(comboEstatusLlanta);
            selectEstatus.selectByVisibleText("NUEVA");

            System.out.println("Se seleccionó la opción 'NUEVA' en el combo Estatus Llanta.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al seleccionar la opción 'NUEVA' en el combo Estatus Llanta.");
        }
    }

    private static void IngresarVidaUtilAleatoria() {
        try {
            // Generar número aleatorio entre 30000 y 50000
            Random random = new Random();
            int vidaUtil = 30000 + random.nextInt(20001); // 50000 - 30000 + 1

            // Esperar a que el campo sea clickeable
            WebElement campoVidaUtil = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='EDT_VIDAUTIL']")));

            // Ingresar el valor generado
            campoVidaUtil.clear();
            campoVidaUtil.sendKeys(String.valueOf(vidaUtil));

            System.out.println("Se ingresó la vida útil: " + vidaUtil);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la vida útil en el campo EDT_VIDAUTIL.");
        }
    }

    private static void IngresarCostoLlanta() {
        try {

            // Generar número aleatorio entre 1000 y 1800
            Random random = new Random();
            int costoLlanta = 1000 + random.nextInt(801); // 1800 - 1000 + 1

            // Esperar a que el campo sea clickeable
            WebElement campoCostoLlanta = driver.findElement(
                    By.xpath("//*[@id=\"EDT_COSTOLLANTA\"]"));

            // Ingresar el valor generado
            campoCostoLlanta.click();
            Thread.sleep(3000);
            campoCostoLlanta.sendKeys(String.valueOf(costoLlanta));
            campoCostoLlanta.sendKeys(Keys.TAB);

            System.out.println("Se ingresó el Costo de Llanta: $" + costoLlanta);
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al ingresar la vida útil en el campo Costo Llanta.");
        }
    }

    // Método auxiliar para verificar si hay una alerta
    private static boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException ex) {
            return false;
        }
    }


    private static void BotonRegistrarLlanta() {
        try {
            // Esperar a que el botón sea visible y clickeable
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='BTN_ACEPTAR']")));

            // Hacer clic en el botón
            botonAceptar.click();

            if (isAlertPresent()) {
                String alertText = driver.switchTo().alert().getText();
                System.out.println("⚠️ Alerta detectada: " + alertText);
                driver.switchTo().alert().accept(); // Cierra la alerta
            }

            System.out.println("Botón 'Registrar Llanta' fue clickeado correctamente con número Económico: " + Economico);

            System.out.println("Se hizo clic en el botón 'Registrar Llanta' (BTN_ACEPTAR).");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón 'Registrar Llanta'.");
        }
    }

    private static void BuscarLlanta() {
        try {
            WebElement busquedaField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"TABLE_ProLlantas_filter\"]")));

            busquedaField.clear();
            busquedaField.sendKeys(Economico);
            System.out.println("Se ingresó el numero economico de la llanta para su búsqueda: " + Economico);
            busquedaField.sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//table[@id='TABLE_ProLlantas']//tbody"), Economico));

            System.out.println("La búsqueda se completó y los resultados están visibles.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al buscar la llanta: " + Economico);
        }
    }

    @Step("Seleccionar Llanta en el Listado")
    private static void SeleccionarLlanta() {
        try {
            Thread.sleep(3000);
            WebElement tablaLlantas = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("TABLE_ProLlantas")));

            WebElement fila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//table[@id='TABLE_ProLlantas']//tr[td[contains(text(),'" + Economico + "')]]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", fila);
            try {
                fila.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fila);
            }
            System.out.println("Llanta seleccionada correctamente: " + Economico);
        } catch (NoSuchElementException e) {
            System.out.println("ERROR: No se encontró la llanta con numero economico: " + Economico);
            UtilidadesAllure.manejoError(driver, e, "No se encontró la llanta con numero exonomico: " + Economico);
        } catch (TimeoutException e) {
            System.out.println("ERROR: La tabla no cargó los resultados a tiempo.");
            UtilidadesAllure.manejoError(driver, e, "La tabla de llantas no cargó correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR: Ocurrió un problema al seleccionar la llanta.");
            UtilidadesAllure.manejoError(driver, e, "Error inesperado al seleccionar la llanta.");
        }
    }

    @Step("Seleccionar Botón Asignar/Desasignar Llantas")
    private static void BotonAsignar() {
        try {
            WebElement botonAutorizar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"BTN_ASIGNARDESASIGNAR\"]")));
            botonAutorizar.click();
            System.out.println("Botón 'Asignar/Desasignar' fue clickeado correctamente.");
        } catch (Exception e) {
            UtilidadesAllure.manejoError(driver, e, "Error al hacer clic en el botón Asignar/Desasignar.");
        }
    }



}
    