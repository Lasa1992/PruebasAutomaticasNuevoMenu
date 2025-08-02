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
public class PasivoManual {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String CODIGO_PROVEEDOR = Variables.PROVEEDOR;

    private String folioGuardado; // Variable de instancia para almacenar el folio

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
    @Description("Ingresar al modulo de Cuentas Por Pagar.")
    public void ingresarModuloCuentasPorPagar() {
        BotonCuentasPorPagar();
        BotonPasivos();
    }

    @RepeatedTest(1)
    @Order(3)
    @Description("Se genera una Pasivo")
    public void Pasivo() {

        AgregarPasivo();
        QuitarCampoFecha();
        CodigoProveedor();
        NoDocumento();
        CopiarFolio();
        MonedaPasivo();
        SubtotalPasivo();
        IVAPasivo();
        AceptarPasivo();
        AceptarPoliza();


    }

    @AfterAll
    public static void tearDown() {
        System.out.println("🔒 Cerrando sesión y liberando WebDriver desde Pasivos...");
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

    @Step("Hacer clic en el botón de Pasivos")
    public void BotonPasivos() {
        try {
            // Esperar a que el botón de Pagos esté presente y clickeable
            WebElement botonPagos = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//*[@id=\"HTM_HTMLTEMPLATE1\"]/div/ul/li[7]/ul/li[2]/a/img")));

            // Hacer clic en el botón
            botonPagos.click();
            System.out.println("Se hizo clic en el botón de Pasivos.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de Pasivos: " + e.getMessage());
        }
    }


    @Step("Desplegar el menú Agregar y hacer clic en la opción Agregar")
    public void AgregarPasivo() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Esperar que el botón "Agregar" esté presente en el DOM usando XPath
            WebElement botonAgregar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"tzOPT_AGREGARMENU\"]/table/tbody/tr[1]/td[2]"
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
            Thread.sleep(3000);
            // Localizamos el elemento que queremos validar (reemplaza el XPath por el correcto)
            WebElement campoFecha = driver.findElement(By.xpath("//*[@id=\"EDT_FECHA\"]"));
            int maxIntentos = 3;
            int intentos = 0;

            // Verificamos si el elemento tiene el foco y repetimos el clic en el body si es necesario
            while(campoFecha.equals(driver.switchTo().activeElement()) && intentos < maxIntentos) {
                System.out.println("El campo está seleccionado. Haciendo clic en el body para quitar la selección... (Intento " + (intentos+1) + ")");
                WebElement body = driver.findElement(By.tagName("body"));
                body.click();
                intentos++;
            }

            // Validamos el estado final
            if (!campoFecha.equals(driver.switchTo().activeElement())) {
                System.out.println("El campo se deseleccionó correctamente tras " + intentos + " intento(s).");
            } else {
                System.out.println("El campo sigue seleccionado tras " + intentos + " intento(s).");
            }
        } catch (Exception e) {
            System.err.println("Error al deseleccionar el campo: " + e.getMessage());
        }
    }


    @Step("Ingresar el número 1 en el campo Código de Proveedor")
    public void CodigoProveedor() {
        try {
            WebElement inputCodigo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//*[@id=\"EDT_NUMEROPROVEEDOR\"]")));
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
            Variables.numeroDocumentoGenerado = "PA-" + folioCopiado;
            Variables.DocumentoGeneradoPasivo = "PA-0000" + folioCopiado;
            System.out.println("Se generó correctamente el número de documento: " + Variables.numeroDocumentoGenerado);


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
            System.out.println("Se ingresó el valor: " + valorTexto + " en el campo Subtotal.");
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
        } catch (Exception e) {
            System.err.println("Error en AceptarPasivo: " + e.getMessage());
        }
    }

    @Step("Hacer clic en el botón Aceptar Póliza")
    public void AceptarPoliza() {
        try {
            // Localizar el botón "Aceptar Póliza" usando el XPath proporcionado
            WebElement botonAceptar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "/html/body/form/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/div/div[3]/table/tbody/tr/td/table/tbody/tr[2]/td/div[1]/table/tbody/tr/td/input")));
            wait.until(ExpectedConditions.elementToBeClickable(botonAceptar));
            // Hacer clic en el botón "Aceptar Póliza"
            botonAceptar.click();
            System.out.println("Se hizo clic en el botón Aceptar Póliza.");

        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón Aceptar Póliza: " + e.getMessage());
        }
    }
}