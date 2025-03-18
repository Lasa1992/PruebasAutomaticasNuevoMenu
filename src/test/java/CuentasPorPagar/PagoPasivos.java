package CuentasPorPagar;
import Indicadores.InicioSesion;
import Indicadores.Variables;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;





import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoPasivos {

    private static WebDriver driver;
    private static WebDriverWait wait;


    private static final String CODIGO_PROVEEDOR = Variables.PROVEEDOR;


    private String folioGuardado; // Variable de instancia para almacenar el folio
    // Variable para almacenar el número de documento generado
    private String numeroDocumentoGenerado;


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
    @Description("Ingresar al modulo de Cuentas por Pagar.")
    public void ingresarModuloCuentasPorPagar() {
        BotonCuentasPorPagar();
        BotonPasivos();
    }

    @RepeatedTest(2)
    @Order(3)
    @Description("Se genera Pago de pasivo")
    public void PagoPasivo() {

        AgregarPasivo();
        //QuitarCampoFecha();
        CodigoProveedor();
        NoDocumento();
        CopiarFolio();
        MonedaPasivo();
        SubtotalPasivo();
        IVAPasivo();
        AceptarPasivo();
        AceptarPoliza();


        //Pago pasivo

        BotonPagoRapido();
        FechaDesde();
        FechaHasta();
        BotonAplicar();
        seleccionarCheckboxPasivo();
        BotonGenerarPago();
        AlertaPago();
        CuentaBancaria();
        ChequeTransferencia();
        BotonGenerar();
        BotonImprimir();
        AceptarAlerta();
        MensajePoliza();
        CorreoProveedor();
        Impresion();
        CerrarVentanaPago();
        SalirPasivosPendientes();



    }


    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde FacturacionGeneral...");
        InicioSesion.cerrarSesion(); // Asegurar que se libere el WebDriver correctamente
    }

    @Step("Hacer clic en el botón de Cuentas por Pagar")
    public void BotonCuentasPorPagar() {
        try {
            // Esperar a que el botón de Cuentas por Pagar esté presente y clickeable
            WebElement botonCuentasPorPagar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[7]/a/img")));

            // Hacer clic en el botón
            botonCuentasPorPagar.click();
            System.out.println("Se hizo clic en el botón de Cuentas por Pagar.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Cuentas por Pagar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de Pagos")
    public void BotonPasivos() {
        try {
            // Esperar a que el botón de Pagos esté presente y clickeable
            WebElement botonPagos = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[7]/ul/li[2]/a/img")));

            // Hacer clic en el botón
            botonPagos.click();
            System.out.println("Se hizo clic en el botón de Pagos.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Pagos: " + e.getMessage());
        }
    }


    @Step("Desplegar el menú Agregar y hacer clic en la opción Agregar")
    public void AgregarPasivo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar que el botón "Agregar" esté presente en el DOM usando XPath
            WebElement botonAgregar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[14]/table/tbody/tr/td/table/tbody/tr[1]/td[2]"
            )));
            wait.until(ExpectedConditions.visibilityOf(botonAgregar));

            // Realizar hover sobre el botón para desplegar el menú con una pausa breve
            Actions actions = new Actions(driver);
            actions.moveToElement(botonAgregar).pause(Duration.ofSeconds(1)).perform();
            System.out.println("Se realizó hover sobre el botón Agregar.");

            // Esperar que la opción "Agregar" del menú se haga visible y sea clickeable
            WebElement opcionAgregar = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("OPT_AGREGAR")));
            wait.until(ExpectedConditions.visibilityOf(opcionAgregar));
            wait.until(ExpectedConditions.elementToBeClickable(opcionAgregar));

            // Hacer clic en la opción del menú usando Actions para mayor estabilidad
            actions.moveToElement(opcionAgregar).click().perform();
            System.out.println("Se hizo clic en la opción Agregar del menú.");

        } catch (Exception e) {
            System.err.println("Error al interactuar con el menú Agregar: " + e.getMessage());
        }
    }

    public void QuitarCampoFecha() {
        try {
            WebElement body = driver.findElement(By.id("tzSTC_TITULO1")); // Clic en el fondo de la página
            body.click();
            body.click();
            System.out.println("Campo EDT_FECHA deseleccionado con un clic en el body.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.err.println("No se pudo deseleccionar el campo EDT_FECHA: " + e.getMessage());
        }
    }

    @Step("Ingresar el número 1 en el campo Código de Proveedor")
    public void CodigoProveedor() {
        try {
            WebElement inputCodigo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[19]/table/tbody/tr/td/ul/li[2]/input"
            )));
            inputCodigo.clear();
            inputCodigo.sendKeys(CODIGO_PROVEEDOR);
            System.out.println("Se ingresó el número " + CODIGO_PROVEEDOR + " en el campo Código de Proveedor.");
        } catch (Exception e) {
            System.err.println("Error en CódigoProveedor: " + e.getMessage());
        }
    }

    @Step("Ingresar 'PA' en el primer campo de NoDocumento, copiar el folio al segundo campo y concatenarlos correctamente")
    public void NoDocumento() {
        try {
            // **Localizar y escribir 'PA' en el primer campo de NoDocumento**
            WebElement inputPrimerCampo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[35]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/ul/li[2]/input")));
            inputPrimerCampo.clear();
            inputPrimerCampo.sendKeys("PA");
            System.out.println("Se ingresó 'PA' en el primer campo de NoDocumento.");

            // **Localizar el campo del folio y copiar su valor**
            WebElement inputFolio = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[6]/table/tbody/tr/td/ul/li[2]/input")));
            String folioCopiado = inputFolio.getAttribute("value").trim(); // Obtener y limpiar el valor del folio
            System.out.println("Se copió el folio: " + folioCopiado);

            // **Localizar el segundo campo de NoDocumento y pegar el folio**
            WebElement inputSegundoCampo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[35]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/table/tbody/tr[2]/td/div[4]/table/tbody/tr/td/ul/li[2]/input")));
            inputSegundoCampo.clear();
            inputSegundoCampo.sendKeys(folioCopiado);
            System.out.println("Se pegó el folio en el segundo campo de NoDocumento.");

            // **Generar la variable con formato 'PA-FOLIO'**
            numeroDocumentoGenerado = "PA-" + folioCopiado;
            System.out.println("Se generó correctamente el número de documento: " + numeroDocumentoGenerado);

        } catch (Exception e) {
            System.err.println("Error en NoDocumento: " + e.getMessage());
        }
    }




    @Step("Copiar y almacenar el folio en memoria")
    public void CopiarFolio() {
        try {
            // Localizar el campo y obtener su valor
            WebElement inputFolio = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[6]/table/tbody/tr/td/ul/li[2]/input")));

            // Guardar el valor del campo en la variable
            folioGuardado = inputFolio.getAttribute("value");

        } catch (Exception e) {
            System.err.println("Error en CopiarFolio: " + e.getMessage());
        }
    }

    // Método para recuperar el folio almacenado
    public String obtenerFolioGuardado() {
        return folioGuardado;
    }

    @Step("Seleccionar entre PESOS y DÓLARES en el campo Moneda")
    public void MonedaPasivo() {
        try {
            WebElement selectMoneda = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[35]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select")));

            Select select = new Select(selectMoneda);
            List<WebElement> opciones = select.getOptions();
            int indiceAleatorio = new Random().nextInt(opciones.size());
            select.selectByIndex(indiceAleatorio);
            System.out.println("Se seleccionó la moneda: " + opciones.get(indiceAleatorio).getText());

            Thread.sleep(2000); // Pausa de 2 segundos (2000 milisegundos)

        } catch (Exception e) {
            System.err.println("Error en MonedaPasivo: " + e.getMessage());
        }
    }

    @Step("Ingresar un valor aleatorio entre 99 y 999.99 en el campo Subtotal")
    public void SubtotalPasivo() {
        try {
            WebElement inputSubtotal = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[35]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/table/tbody/tr[2]/td/div[8]/table/tbody/tr/td/ul/li[2]/input")));

            double valorAleatorio = 99 + (new Random().nextDouble() * 900);
            String valorTexto = String.format("%.2f", valorAleatorio);
            inputSubtotal.clear();
            inputSubtotal.sendKeys(valorTexto);
            inputSubtotal.sendKeys(Keys.ENTER);

            System.out.println("Se ingresó el valor: " + valorTexto + " en el campo Subtotal.");

            Thread.sleep(2000); // Pausa de 2 segundos (2000 milisegundos)

        } catch (Exception e) {
            System.err.println("Error en SubtotalPasivo: " + e.getMessage());
        }
    }

    @Step("Seleccionar un valor aleatorio en el combo de IVA")
    public void IVAPasivo() {
        try {
            WebElement selectIVA = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[35]/table/tbody/tr[2]/td/table[1]/tbody/tr/td/table/tbody/tr[2]/td/div[6]/table/tbody/tr/td/select")));

            Select select = new Select(selectIVA);
            List<WebElement> opciones = select.getOptions();
            int indiceAleatorio = new Random().nextInt(opciones.size());
            select.selectByIndex(indiceAleatorio);
            System.out.println("Se seleccionó el IVA: " + opciones.get(indiceAleatorio).getText());

            Thread.sleep(5000); // Pausa de 2 segundos (2000 milisegundos)

        } catch (Exception e) {
            System.err.println("Error en IVAPasivo: " + e.getMessage());
        }
    }

    @Step("Aceptar el formulario")
    public void AceptarPasivo() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[12]/table/tbody/tr/td/table/tbody/tr[2]/td/div[22]/table/tbody/tr/td/button")));
            wait.until(ExpectedConditions.elementToBeClickable(botonAceptar));
            botonAceptar.click();
            System.out.println("Se hizo clic en Aceptar.");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("Error en AceptarPasivo: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Aceptar Póliza")
    public void AceptarPoliza() {
        try {
            // Localizar el botón "Aceptar Póliza" usando el XPath proporcionado
            WebElement botonAceptar = driver.findElement(
                    By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input"));

            // Hacer clic en el botón "Aceptar Póliza"
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón Aceptar Póliza.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Aceptar Póliza: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón de Pago Rápido")
    public void BotonPagoRapido() {
        try {
            // Esperar a que el botón esté presente y clickeable
            WebElement botonPagoRapido = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "/html/body/form/table/tbody/tr/td/div/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[3]/td/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[6]/input")));

            // Hacer clic en el botón
            botonPagoRapido.click();
            System.out.println("Se hizo clic en el botón de Pago Rápido.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Pago Rápido: " + e.getMessage());
        }
    }

    @Step("Ingresar la fecha actual en el campo Fecha Desde")
    public void FechaDesde() {
        try {
            // Obtener la fecha actual en formato dd/MM/yyyy
            String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Localizar el campo de Fecha Desde
            WebElement inputFechaDesde = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[2]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")));

            // Hacer clic en el campo para activarlo
            inputFechaDesde.click();
            Thread.sleep(300); // Pequeña pausa para evitar bloqueos

            // Usar JavaScript para establecer el valor de la fecha
            ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('value', arguments[1]);", inputFechaDesde, fechaActual);
            Thread.sleep(500);

            // Simular tecla "TAB" para que el sistema registre el cambio
            inputFechaDesde.sendKeys(Keys.TAB);

            System.out.println("Se ingresó la fecha actual (" + fechaActual + ") en el campo Fecha Desde.");

        } catch (Exception e) {
            System.err.println("Error en FechaDesde: " + e.getMessage());
        }
    }

    @Step("Ingresar la fecha de 3 meses después en el campo Fecha Hasta")
    public void FechaHasta() {
        try {
            // Obtener la fecha de 3 meses después en formato dd/MM/yyyy
            String fechaTresMeses = LocalDate.now().plus(3, ChronoUnit.MONTHS)
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Localizar el campo de Fecha Hasta
            WebElement inputFechaHasta = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[2]/div[2]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/input")));

            // Hacer clic en el campo para activarlo
            inputFechaHasta.click();
            Thread.sleep(300);

            // Usar JavaScript para establecer el valor de la fecha
            ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('value', arguments[1]);", inputFechaHasta, fechaTresMeses);
            Thread.sleep(500);

            // Simular tecla "TAB" para que el sistema registre el cambio
            inputFechaHasta.sendKeys(Keys.TAB);

            System.out.println("Se ingresó la fecha de 3 meses después (" + fechaTresMeses + ") en el campo Fecha Hasta.");

        } catch (Exception e) {
            System.err.println("Error en FechaHasta: " + e.getMessage());
        }
    }



    @Step("Hacer clic en el botón Aplicar")
    public void BotonAplicar() {
        try {
            // Esperar a que el botón Aplicar esté presente y clickeable
            WebElement botonAplicar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[2]/div[9]/div/a/span/span")));

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
                    By.id("TABLE_PROPASIVOSPENDIENTESDEPAGO_TITRES_RECH_3")));
            iconoBuscar.click();
            System.out.println("Se hizo clic en el ícono de búsqueda.");
            Thread.sleep(500);

            // **Interacción con el campo de búsqueda**
            WebElement inputBuscar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("form > input")));
            inputBuscar.sendKeys(numeroDocumentoGenerado);
            Thread.sleep(1000);
            inputBuscar.sendKeys(Keys.ENTER);
            System.out.println("Se ingresó y buscó el documento: " + numeroDocumentoGenerado);
            Thread.sleep(500);

            // **Esperar y seleccionar la fila correcta con el número de documento**
            WebElement filaSeleccionada = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tr[contains(@class, 'TABLE-Selected') and .//td[contains(@class, 'wbcolCOL_NUMERODOCUMETO')]//div[text()='" + numeroDocumentoGenerado + "']]")));
            System.out.println("Se encontró la fila del documento: " + numeroDocumentoGenerado);

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




    @Step("Hacer clic en el botón para generar pago")
    public void BotonGenerarPago() {
        try {
            // **Localizar el botón usando el XPath proporcionado**
            WebElement botonGenerarPago = driver.findElement(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/table/tbody/tr/td/div/div[1]/div/table/tbody/tr/td/input"));

            // **Hacer clic en el botón**
            botonGenerarPago.click();
            System.out.println("Se hizo clic en el botón para generar pago.");
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
                    "/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[3]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/div[1]/div/table/tbody/tr/td/table/tbody/tr/td/ul/li[2]/table/tbody/tr/td/select")));

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
                    "/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[3]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[1]/div/table/tbody/tr[1]/td/label/input")));

            WebElement opcionTransferencia = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[3]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div[1]/div/table/tbody/tr[2]/td/label/input")));

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
                    "/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[3]/div[1]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/input")));

            // Hacer clic en el botón
            botonGenerar.click();
            System.out.println("Se hizo clic en el botón Generar.");

            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Generar: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Imprimir")
    public void BotonImprimir() {
        try {
            // Esperar a que el botón Imprimir esté presente y clickeable
            WebElement botonImprimir = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[2]/table/tbody/tr/td/div/div[1]/table/tbody/tr/td/div/div[5]/div[1]/div/input")));

            // Hacer clic en el botón
            botonImprimir.click();
            System.out.println("Se hizo clic en el botón Imprimir.");

            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Imprimir: " + e.getMessage());
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
            By xpathMensajePoliza = By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input");

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

    public void CorreoProveedor() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Random random = new Random();

            String[] botonesXPath = {
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td/input",
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/input"
            };

            boolean clickExitoso = false;
            for (String xpath : botonesXPath) {
                By botonBy = By.xpath(xpath);
                if (driver.findElements(botonBy).size() > 0) {
                    WebElement boton = wait.until(ExpectedConditions.elementToBeClickable(botonBy));
                    System.out.println("Intentando hacer clic en el botón con XPath: " + xpath);
                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", boton);

                    try {
                        boton.click();
                        System.out.println("Se hizo clic en el botón correctamente.");
                    } catch (Exception e) {
                        js.executeScript("arguments[0].click();", boton);
                        System.out.println("Se hizo clic en el botón con JavaScript.");
                    }
                    clickExitoso = true;
                    break;
                }
            }

            if (!clickExitoso) {
                System.out.println("Ninguno de los botones de Correo Proveedor estaba disponible. Continuando...");
            }

        } catch (Exception e) {
            System.out.println("Error en CorreoProveedor. Continuando...");
        }
    }

    public void Impresion() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            By xpathImpresion = By.xpath("/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[3]/div/table/tbody/tr/td/input");

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
                    "/html/body/form/table/tbody/tr/td[2]/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[1]/div[4]/div/table/tbody/tr/td/a/span/span")));

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
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/div/div[5]/div[2]/div/table/tbody/tr/td/input")));

            // Hacer clic en el botón
            botonSalir.click();
            System.out.println("Se hizo clic en el botón Salir de Pasivos Pendientes.");
            Thread.sleep(2000);


        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Salir de Pasivos Pendientes: " + e.getMessage());
        }
    }


}


