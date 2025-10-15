package CuentasPorPagar;

import Indicadores.InicioSesion;
import Indicadores.Variables;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.util.List;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoContraRecibos {

    private static WebDriver driver;
    private static WebDriverWait wait;


    private static final String CODIGO_PROVEEDOR = Variables.PROVEEDOR;

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
        InicioSesion.handleTipoCambio();       // ✅ Sin parámetros
        InicioSesion.handleNovedadesScreen();  // ✅ Sin parámetros
    }


    @Test
    @Order(2)
    @Description("Se genera Pago de  Contra Recibo")
    public void PagoContraRecibo() {

        //Accedemos al Metodo para crear Contra Recibo
        ContraRecibos contrarecibo = new ContraRecibos();
        contrarecibo.setup();
        contrarecibo.ContraRecibo();

        //Se crea pago de Contra Recibo
        PagarContraRecibo();
        CodigoProveedor();
        BotonAplicar();
        seleccionarCheckboxPasivo();
        BotonGenerarPago();
        AlertaPago();
        CuentaBancaria();
        ChequeTransferencia();
        BotonGenerar();
        BotonPagar();
        AceptarAlerta();
        MensajePoliza();
        Impresion();
        CerrarVentanaPago();
        SalirPasivosPendientes();

    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Pasivos...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    @Step("Hace clic en el botón Pagar Contra Recibo")
    public void PagarContraRecibo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar que el botón "Pagar Contra Recibo" esté presente en el DOM usando XPath
            WebElement botonAgregar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"BTN_PAGARCONTRARECIBOS\"]"
            )));
            wait.until(ExpectedConditions.visibilityOf(botonAgregar));

            // Hacer clic en la opción del menú
            botonAgregar.click();
            System.out.println("Se hizo clic en el botón Pagar Contra Recibo.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Pagar Contra Recibosr: " + e.getMessage());
        }
    }

    @Step("Ingresar el número 1 en el campo Código de Proveedor")
    public void CodigoProveedor() {
        try {
            WebElement inputCodigo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_NUMEROPROVEEDORDESDE\"]")));
            inputCodigo.clear();
            inputCodigo.sendKeys(CODIGO_PROVEEDOR);
            System.out.println("Se ingresó el número " + CODIGO_PROVEEDOR + " en el campo Código de Proveedor.");
        } catch (Exception e) {
            System.err.println("Error en CódigoProveedor: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Aplicar")
    public void BotonAplicar() {
        try {
            // Esperar a que el botón Aplicar esté presente y clickeable
            WebElement botonAplicar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_APLICAR\"]")));

            // Hacer clic en el botón
            botonAplicar.click();
            System.out.println("Se hizo clic en el botón Aplicar.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Aplicar: " + e.getMessage());
        }
    }

    public void seleccionarCheckboxPasivo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // **Clic en el ícono de búsqueda**
            WebElement iconoBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"TABLE_PROPASIVOSPENDIENTESDEPAGO_TITRES_RECH_3\"]")));
            iconoBuscar.click();
            System.out.println("Se hizo clic en el ícono de búsqueda.");
            Thread.sleep(500);

            System.out.println("valor de numero " + Variables.DocumentoGeneradoPasivo);
            // **Interacción con el campo de búsqueda**
            WebElement inputBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form > input")));
            inputBuscar.sendKeys(Variables.DocumentoGeneradoPasivo);
            Thread.sleep(1000);
            inputBuscar.sendKeys(Keys.ENTER);
            System.out.println("Se ingresó y buscó el documento: " + Variables.DocumentoGeneradoPasivo);
            Thread.sleep(500);

            // **Esperar y seleccionar la fila correcta con el número de documento**
            WebElement filaSeleccionada = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_NUMERODOCUMETO')]//div[text()='" + Variables.DocumentoGeneradoPasivo + "']]")));
            System.out.println("Se encontró la fila del documento: " + Variables.DocumentoGeneradoPasivo);

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

            Thread.sleep(2000);

        } catch (NoSuchElementException e) {
            System.err.println("Elemento no encontrado: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Tiempo de espera agotado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón para Generar Pago")
    public void BotonGenerarPago() {
        try {
            // **Localizar el botón usando el XPath proporcionado**
            WebElement botonGenerarContraRecibo = driver.findElement(By.xpath(
                    "//*[@id=\"BTN_GENERARPAGO\"]"));

            // **Hacer clic en el botón**
            botonGenerarContraRecibo.click();
            System.out.println("Se hizo clic en el botón para Generar Pago.");
            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error en BotonGenerarPago: " + e.getMessage());
        }
    }

    @Step("Aceptar la alerta del navegador")
    public void AlertaPago() {
        try {
            // Esperar hasta que aparezca la alerta
            wait.until(ExpectedConditions.alertIsPresent());

            // Cambiar el foco a la alerta y aceptarla
            Alert alerta = driver.switchTo().alert();
            alerta.accept();
            System.out.println("Se aceptó la alerta del navegador.");

        } catch (NoAlertPresentException e) {
            System.err.println("No se encontró ninguna alerta en el navegador.");
        } catch (Exception e) {
            System.err.println("Error al aceptar la alerta: " + e.getMessage());
        }
    }

    @Step("Seleccionar una cuenta bancaria aleatoria")
    public void CuentaBancaria() {
        try {
            // Localizar el campo de selección de cuenta bancaria
            WebElement selectCuenta = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"COMBO_CATCUENTASBANCARIAS\"]")));

            // Crear objeto Select
            Select select = new Select(selectCuenta);

            // Obtener todas las opciones disponibles
            List<WebElement> opciones = select.getOptions();

            if (opciones.size() > 1) {
                // Elegir aleatoriamente una opción (sin contar la primera si es "Seleccionar")
                int indiceAleatorio = new Random().nextInt(opciones.size() - 1) + 1;
                select.selectByIndex(indiceAleatorio);
                System.out.println("Se seleccionó la cuenta bancaria: " + opciones.get(indiceAleatorio).getText());
            } else {
                System.err.println("No hay cuentas bancarias disponibles para seleccionar.");
            }

            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error en CuentaBancaria: " + e.getMessage());
        }
    }

    @Step("Seleccionar aleatoriamente entre Cheque o Transferencia")
    public void ChequeTransferencia() {
        try {
            // Localizar ambos inputs (Cheque y Transferencia)
            WebElement opcionCheque = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"RADIO_TIPOMOVIMIENTO_1\"]")));

            WebElement opcionTransferencia = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"RADIO_TIPOMOVIMIENTO_2\"]")));

            // Crear una lista con ambas opciones
            WebElement[] opciones = {opcionCheque, opcionTransferencia};

            // Elegir aleatoriamente una opción
            int indiceAleatorio = new Random().nextInt(opciones.length);
            WebElement opcionSeleccionada = opciones[indiceAleatorio];

            // Hacer clic en la opción seleccionada
            opcionSeleccionada.click();
            System.out.println("Se seleccionó la opción de pago: " + (indiceAleatorio == 0 ? "Cheque" : "Transferencia"));

            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("Error en ChequeTransferencia: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Generar")
    public void BotonGenerar() {
        try {
            // Esperar a que el botón Generar esté presente y clickeable
            WebElement botonGenerar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_GENERAR\"]")));

            // Hacer clic en el botón
            botonGenerar.click();
            System.out.println("Se hizo clic en el botón Generar.");

            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Generar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Pagar")
    public void BotonPagar() {
        try {
            // Esperar a que el botón Pagar esté presente y clickeable
            WebElement botonPagar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_IMPRIMIR\"]")));

            // Hacer clic en el botón
            botonPagar.click();
            System.out.println("Se hizo clic en el botón Pagar.");

            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Pagar: " + e.getMessage());
        }
    }

    @Step("Aceptar la alerta del navegador")
    public void AceptarAlerta() {
        try {
            // Esperar hasta que aparezca la alerta
            wait.until(ExpectedConditions.alertIsPresent());

            // Cambiar el foco a la alerta y aceptarla
            Alert alerta = driver.switchTo().alert();
            alerta.accept();
            System.out.println("Se aceptó la alerta del navegador.");

            Thread.sleep(2000);

        } catch (NoAlertPresentException e) {
            System.err.println("No se encontró ninguna alerta en el navegador.");
        } catch (Exception e) {
            System.err.println("Error al aceptar la alerta: " + e.getMessage());
        }
    }

    public void MensajePoliza() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            By xpathMensajePoliza = By.xpath("//*[@id=\"BTN_OK\"]");

            if (driver.findElements(xpathMensajePoliza).size() > 0) {
                WebElement mensajePoliza = wait.until(ExpectedConditions.elementToBeClickable(xpathMensajePoliza));
                mensajePoliza.click();
                System.out.println("Se hizo clic en el campo Mensaje de Póliza.");
            } else {
                System.out.println("El campo Mensaje de Póliza no está presente. Continuando...");
            }
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("No se pudo hacer clic en el campo Mensaje de Póliza. Continuando...");
        }
    }

    public void Impresion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            By xpathImpresion = By.xpath("//*[@id=\"BTN_REGRESAR\"]");

            if (driver.findElements(xpathImpresion).size() > 0) {
                WebElement botonImpresion = wait.until(ExpectedConditions.elementToBeClickable(xpathImpresion));
                botonImpresion.click();
                System.out.println("Se hizo clic en el botón Impresión.");
            } else {
                System.out.println("El botón Impresión no está presente. Continuando...");
            }

            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("No se pudo hacer clic en el botón Impresión. Continuando...");
        }
    }

    @Step("Hacer clic en el botón Cerrar Ventana de Pago")
    public void CerrarVentanaPago() {
        try {
            // Esperar a que el botón para cerrar la ventana de pago esté presente y clickeable
            WebElement botonCerrar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"z_BTN_SALIR_IMG\"]/span")));

            // Hacer clic en el botón
            botonCerrar.click();
            System.out.println("Se hizo clic en el botón para cerrar la ventana de pago.");
            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Cerrar Ventana de Pago: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Salir de Pasivos Pendientes")
    public void SalirPasivosPendientes() {
        try {
            // Esperar a que el botón Salir esté presente y clickeable
            WebElement botonSalir = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"BTN_CANCELAR\"]")));

            // Hacer clic en el botón
            botonSalir.click();
            System.out.println("Se hizo clic en el botón Salir de Pasivos Pendientes.");
            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Salir de Pasivos Pendientes: " + e.getMessage());
        }
    }


}
