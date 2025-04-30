package GMLogistico;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreacionViajeLogi5 {

    public static WebDriver driver;
    public static WebDriverWait wait;

    public static String currentRFC;

    static class Cliente {
        String rfc;
        String email;
        String contrasena;

        public Cliente(String rfc, String email, String contrasena) {
            this.rfc = rfc;
            this.email = email;
            this.contrasena = contrasena;
        }
    }

    @BeforeAll
    public static void setup() {
        ChromeOptions options = new ChromeOptions();
        // Forzar headless de forma standard y legacy
        options.addArguments("--headless=new");
        options.addArguments("--headless");         // fallback
        // Evitar infobars y mensajes de automatización
        options.setExperimentalOption("excludeSwitches",
                Arrays.asList("enable-automation", "enable-logging"));
        options.setExperimentalOption("useAutomationExtension", false);
        // Deshabilitar shortcuts que abren DevTools
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-features=IsolateOrigins,site-per-process");
        // Tamaño de ventana y sandbox
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        // Inyectar en Chrome un pequeño snippet para desactivar F12 / Ctrl+Shift+I
        ((JavascriptExecutor) driver).executeScript(
                "window.addEventListener('keydown', e => { " +
                        "  if ((e.key === 'F12') || (e.ctrlKey && e.shiftKey && e.key==='I')) {" +
                        "    e.preventDefault(); e.stopPropagation();" +
                        "  }" +
                        "});"
        );

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().setSize(new Dimension(1920,1080));
        driver.get("https://logisticav1.gmtransport.co/");
    }


    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void InicioSeSionLog() {
        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();
    }

    @RepeatedTest(4000)
    @Order(2)
    @Description("Generación Viaje Logistico")
    public void CrearViaje() {
        try {
            ClickBotonComprometidos();
            // Filtros();
            abrirMenuYSeleccionarCrearViaje();
            pause();
            seleccionarTipoDocumentoIngreso();
            pause();
            clickTabMateriales();
            pause();
            //importarMaterialesCompleto();
            // pause();
            clickContinuarMateriales();
            pause();
            clickContinuarEspecificaciones();
            pause();
            seleccionarCondicionPagoAleatoria();
            pause();
            seleccionarMetodoPagoAleatorio();
            pause();
            seleccionarUsoCFDIAleatorio();
            pause();
            EliminarConcepto();
            pause();
            seleccionarConceptoFacturacionFlete();
            pause();
            asignarImporteAleatorio();
            pause();
            agregarConcepto();
            pause();
            clickGuardarViaje();
            pause();
            clickEnviarConfirmacion();
            clickBotonAceptar();
            pause();
            clickSubastaViajes();
            pause();



        } catch (Exception e) {
            System.out.println("Error dentro de Crear Viaje(): " + e.getMessage());
            try {
                regresarAPaginaSubasta();
            } catch (Exception ex) {
                System.out.println("No se pudo regresar al listado de Subastas. Reiniciando sesión...");
                reiniciarSesionDesdeCero();
            }
        }
    }

    @Description("Reinicia la sesión cerrando el navegador y abriendo uno nuevo.")
    public static void reiniciarSesionDesdeCero() {
        try {
            if (driver != null) {
                driver.quit();
            }

            driver = new ChromeDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            driver.manage().window().maximize();
            driver.get("https://logisticav1.gmtransport.co/");

            Iniciosesion();
            BotonIniciosesion();
            MensajeAlerta();

            System.out.println("Sesión reiniciada correctamente.");
        } catch (Exception e) {
            System.out.println("Fallo al reiniciar sesión: " + e.getMessage());
            Assertions.fail("No se pudo reiniciar la sesión.");
        }
    }

    @Description("Intenta regresar a la página de 'Subasta viajes'.")
    public static void regresarAPaginaSubasta() {
        try {
            WebElement botonSubasta = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//h5[contains(text(),'Subasta viajes')]")
            ));
            botonSubasta.click();
            System.out.println("Regresando a 'Subasta viajes'.");
        } catch (Exception e) {
            System.out.println("No se pudo hacer clic en 'Subasta viajes': " + e.getMessage());
            throw e;
        }
    }

    @Description("Llena los campos de inicio de sesión con información fija.")
    public static void Iniciosesion() {
        Cliente[] clientes = {
                new Cliente("IIA040805DZ4", "elisa.logistica@gmtransporterp.com", "123456"),
                new Cliente("LOGI2222224T5", "logistico2@gmail.com", "123456"),
                new Cliente("LOGI3333335T6", "logi3@gmail.com", "123456"),
                new Cliente("LOGI4444445T6", "logi4@gmail.com", "123456"),
                new Cliente("LOGI1111112Q4", "logistico1@gmail.com", "123456")
        };

        Cliente cliente = clientes[4];

        currentRFC = cliente.rfc;

        WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-rfc-login")));
        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-email-login")));
        WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-password-login")));

        inputRFC.clear();
        inputEmail.clear();
        inputContrasena.clear();

        inputRFC.sendKeys(cliente.rfc);
        inputEmail.sendKeys(cliente.email);
        inputContrasena.sendKeys(cliente.contrasena);

        System.out.println("Intentando iniciar sesión con: " + cliente.email);
    }

    @Description("Da clic en el botón de 'Iniciar sesión'.")
    public static void BotonIniciosesion() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='root']/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")
        ));
        submitButton.click();
    }

    @Description("Maneja alerta de sesión previa.")
    public static void MensajeAlerta() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                alert.accept();
                System.out.println("Alerta aceptada.");
            }
        } catch (Exception e) {
            System.out.println("No se encontró alerta o no era necesaria.");
        }
    }

    @Description("Da clic en el botón 'Comprometidos'.")
    public static void ClickBotonComprometidos() {
        try {
            By locator = By.xpath("//button[normalize-space(text())='Comprometidos']");
            WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", boton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boton);
            System.out.println("Botón 'Comprometidos' clickeado.");
        } catch (Exception e) {
            System.out.println("Error en ClickBotonComprometidos: " + e.getMessage());
            Assertions.fail("Fallo en ClickBotonComprometidos: " + e.getMessage());
        }
    }

    public static void Filtros() {
        try {
            // 1) Localiza y abre el dropdown con Actions
            By dropdownBtn = By.xpath("//div[@role='combobox' and @aria-haspopup='listbox']");
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownBtn));
            // mover + clic (evita overlay de toolbar)
            new Actions(driver)
                    .moveToElement(dropdown)
                    .pause(Duration.ofMillis(200))
                    .click()
                    .perform();

            // 2) Espera directamente la opción, no el contenedor
            By optionLocator = By.xpath(
                    "//li[@role='option' and normalize-space(text())='Documentos recibidos']"
            );
            WebElement option = wait.until(
                    ExpectedConditions.elementToBeClickable(optionLocator)
            );
            option.click();

            // 3) Clic en Aplicar
            By applyBtn = By.xpath("//button[normalize-space(text())='Aplicar']");
            WebElement botonAplicar = wait.until(
                    ExpectedConditions.elementToBeClickable(applyBtn)
            );
            botonAplicar.click();

            System.out.println("Filtro 'Documentos recibidos' aplicado correctamente.");
        } catch (Exception e) {
            System.out.println("Error en Filtros: " + e.getMessage());
            throw new RuntimeException("Fallo en Filtros()", e);
        }
    }




    @Description("Abre el menú de opciones del primer registro y selecciona 'Crear viaje'")
    public void abrirMenuYSeleccionarCrearViaje() {
        try {
            // 1) Hacer click en el ícono de opciones en la columna 16 del primer registro
            By iconoOpcionesLocator = By.xpath(
                    "/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[2]/div/div[6]"
                            + "/div/div[1]/table/tbody/tr[1]/td[16]//button"
            );
            WebElement iconoOpciones = wait.until(
                    ExpectedConditions.elementToBeClickable(iconoOpcionesLocator)
            );
            iconoOpciones.click();
            System.out.println("Menú de opciones abierto.");

            // 2) Esperar a que aparezca la opción "Crear viaje" y hacer click en ella
            By crearViajeLocator = By.xpath(
                    "//ul[contains(@class,'list-none')]/li[normalize-space(text())='Crear viaje']"
            );
            WebElement crearViaje = wait.until(
                    ExpectedConditions.elementToBeClickable(crearViajeLocator)
            );
            crearViaje.click();
            System.out.println("Opción 'Crear viaje' seleccionada.");
        } catch (Exception e) {
            System.out.println("Error al abrir menú y seleccionar 'Crear viaje': " + e.getMessage());
            Assertions.fail("Fallo en abrirMenuYSeleccionarCrearViaje: " + e.getMessage());
        }
    }

    @Description("Selecciona la opción ‘INGRESO’ del dropdown ‘Tipo de documento’")
    public void seleccionarTipoDocumentoIngreso() {
        // 1) Full XPath de tu combobox
        By dropdownLocator = By.xpath(
                "/html/body/div[1]/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/div/"
                        + "div[1]/div/div[2]/div[1]/div/div/div[2]/div/div/div/div/div/div[1]/div/div"
        );
        // 2) Localizador de la opción cuyo texto es exactamente “INGRESO”
        By ingresoOptionLocator = By.xpath(
                "//ul[@role='listbox']//li[normalize-space(text())='Ingreso']"
        );

        try {
            // 3) Abrir el dropdown
            WebElement campo = wait.until(
                    ExpectedConditions.elementToBeClickable(dropdownLocator)
            );
            campo.click();

            // 4) Esperar a que la opción “INGRESO” esté presente y clicable
            WebElement ingreso = wait.until(
                    ExpectedConditions.elementToBeClickable(ingresoOptionLocator)
            );

            // 5) Scroll y click sobre “INGRESO”
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", ingreso);
            ingreso.click();

            System.out.println("✅ ‘Tipo de documento’ fijado a: " + ingreso.getText());
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar ‘INGRESO’ en Tipo de documento: "
                    + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarTipoDocumentoIngreso()", e);
        }
    }




    @Description("Hace clic en la pestaña 'Materiales' y espera a que su contenido se muestre")
    public void clickTabMateriales() {
        try {
            // 1) Localizar el botón de la pestaña por su texto (más robusto que el id)
            By tabLocator = By.xpath("//button[@role='tab' and normalize-space(text())='Materiales']");
            WebElement tabMateriales = wait.until(
                    ExpectedConditions.elementToBeClickable(tabLocator)
            );

            // 2) Asegurarse de que esté visible y libre de solapamientos
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", tabMateriales);
            Thread.sleep(200); // pequeño retardo para que se reposicione

            // 3) Clic real con Actions para evitar interceptaciones
            new Actions(driver)
                    .moveToElement(tabMateriales)
                    .click()
                    .perform();

            // 4) Esperar a que el panel de "Materiales" se vuelva visible
            By panelLocator = By.id(tabMateriales.getAttribute("aria-controls"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(panelLocator));

            System.out.println("✅ Pestaña 'Materiales' activada correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al clicar la pestaña 'Materiales': " + e.getMessage());
            throw new RuntimeException("Fallo en clickTabMateriales()", e);
        }
    }

    @Description("Realiza el flujo completo de importación de materiales")
    public void importarMaterialesCompleto() {
        try {
            // 1) Click en el botón inicial "Importar Materiales" (el que tiene el icono)
            By btnInicialLocator = By.xpath(
                    "//button[contains(@class,'MuiButton-textSecondary') and normalize-space(.)='Importar Materiales']"
            );
            WebElement btnInicial = wait.until(
                    ExpectedConditions.elementToBeClickable(btnInicialLocator)
            );
            btnInicial.click();
            System.out.println("▶ Abierto diálogo de importación de materiales.");

            // 2) Cargar el archivo en el dropzone
            By fileInputLocator = By.cssSelector("input[type='file']");
            WebElement fileInput = wait.until(
                    ExpectedConditions.presenceOfElementLocated(fileInputLocator)
            );
            String ruta = "C:\\Users\\LuisSanchez\\IdeaProjects\\GMQA\\src\\test\\java\\GMLogistico\\Materiales_100.xlsx";
            fileInput.sendKeys(ruta);
            System.out.println("✔ Archivo cargado en el input: " + ruta);

            // 3) Click en "Importar Archivo"
            By btnImportarArchivoLocator = By.xpath("//button[normalize-space(.)='Importar Archivo']");
            WebElement btnImportarArchivo = wait.until(
                    ExpectedConditions.elementToBeClickable(btnImportarArchivoLocator)
            );
            btnImportarArchivo.click();
            System.out.println("✔ Pulsado 'Importar Archivo'.");

            // 4) Click en "Importar Materiales" dentro del diálogo (segundo botón)
            By btnImportarMaterialesLocator = By.xpath(
                    "//div[contains(@class,'MuiDialogActions-root')]//button[normalize-space(.)='Importar Materiales']"
            );
            WebElement btnImportarMateriales = wait.until(
                    ExpectedConditions.elementToBeClickable(btnImportarMaterialesLocator)
            );
            btnImportarMateriales.click();
            System.out.println("✔ Pulsado 'Importar Materiales' en el diálogo.");

            // 5) Click en "Aceptar" para cerrar confirmación
            By btnAceptarLocator = By.xpath("//button[normalize-space(.)='Aceptar']");
            WebElement btnAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(btnAceptarLocator)
            );
            btnAceptar.click();
            System.out.println("✅ Importación de materiales completada y confirmada.");
        } catch (Exception e) {
            System.out.println("❌ Error en importarMaterialesCompleto: " + e.getMessage());
            throw new RuntimeException("Fallo en importarMaterialesCompleto()", e);
        }
    }






    @Description("Hace clic en el botón 'Continuar' dentro del flujo de materiales")
    public void clickContinuarMateriales() {
        try {
            // 1) Localizar el botón 'Continuar' por su texto
            By locator = By.xpath("//button[@type='submit' and normalize-space(text())='Continuar']");
            WebElement btnContinuar = wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            );

            // 2) Asegurarnos de que esté visible en pantalla
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", btnContinuar);
            Thread.sleep(200); // pequeño retraso para evitar interceptaciones de UI

            // 3) Hacer clic
            btnContinuar.click();
            System.out.println("✅ Botón 'Continuar' clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al clicar 'Continuar': " + e.getMessage());
            throw new RuntimeException("Fallo en clickContinuarMateriales()", e);
        }
    }

    @Description("Hace clic en el botón 'Continuar' de la sección Especificaciones")
    public void clickContinuarEspecificaciones() {
        try {
            // 1) Localizar el botón 'Continuar' por su texto y tipo
            By locator = By.xpath("//button[@type='submit' and normalize-space(.)='Continuar']");
            WebElement btnContinuar = wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            );

            // 2) Asegurarnos de que esté visible en pantalla
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", btnContinuar);
            Thread.sleep(200); // pequeño retraso para evitar interceptaciones

            // 3) Hacer clic
            btnContinuar.click();
            System.out.println("✅ Botón 'Continuar' en Especificaciones clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al clicar 'Continuar' en Especificaciones: " + e.getMessage());
            throw new RuntimeException("Fallo en clickContinuarEspecificaciones()", e);
        }
    }

    @Description("Selecciona la primera opción del dropdown 'Condición de Pago' con foco")
    public void seleccionarCondicionPagoAleatoria() {
        // 1) Locators por XPath
        By dropdownLocator = By.xpath("//*[@id='condicionPagoSelected']");
        By optionLocator   = By.xpath("//ul[@role='listbox']//li[1]");

        try {
            // 2) Traer ventana al frente, enfocar el campo y abrir el dropdown
            WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            ((JavascriptExecutor) driver).executeScript(
                    ""
                            + "window.focus();"
                            + "arguments[0].scrollIntoView({block:'center'});"
                            + "arguments[0].focus();",
                    campo
            );
            campo.click();

            // 3) Traer al frente, enfocar la primera opción y click
            WebElement opcion = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            ((JavascriptExecutor) driver).executeScript(
                    ""
                            + "arguments[0].scrollIntoView(true);"
                            + "arguments[0].focus();",
                    opcion
            );
            opcion.click();

            System.out.println("✅ Condición de Pago: primera opción seleccionada.");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarCondicionPagoAleatoria(): " + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarCondicionPagoAleatoria()", e);
        }
    }

    @Description("Selecciona la primera opción del dropdown 'Método de Pago' con foco")
    public void seleccionarMetodoPagoAleatorio() {
        // 1) Locators por XPath
        By dropdownLocator = By.xpath("//*[@id='metodoPagoSelected']");
        By optionLocator   = By.xpath("//ul[@role='listbox']//li[1]");

        try {
            // 2) Traer ventana al frente, enfocar el campo y abrir el dropdown
            WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            ((JavascriptExecutor) driver).executeScript(
                    ""
                            + "window.focus();"
                            + "arguments[0].scrollIntoView({block:'center'});"
                            + "arguments[0].focus();",
                    campo
            );
            campo.click();

            // 3) Traer al frente, enfocar la primera opción y click
            WebElement opcion = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            ((JavascriptExecutor) driver).executeScript(
                    ""
                            + "arguments[0].scrollIntoView(true);"
                            + "arguments[0].focus();",
                    opcion
            );
            opcion.click();

            System.out.println("✅ Método de Pago: primera opción seleccionada.");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarMetodoPagoAleatorio(): " + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarMetodoPagoAleatorio()", e);
        }
    }

    @Description("Selecciona la primera opción del dropdown 'Uso CFDI' con foco")
    public void seleccionarUsoCFDIAleatorio() {
        // 1) Locators por XPath
        By dropdownLocator = By.xpath("//*[@id='usoCFDI']");
        By optionLocator   = By.xpath("//ul[@role='listbox']//li[5]");

        try {
            // 2) Traer ventana al frente, enfocar el campo y abrir el dropdown
            WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            ((JavascriptExecutor) driver).executeScript(
                    ""
                            + "window.focus();"
                            + "arguments[0].scrollIntoView({block:'center'});"
                            + "arguments[0].focus();",
                    campo
            );
            campo.click();

            // 3) Traer al frente, enfocar la primera opción y click
            WebElement opcion = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            ((JavascriptExecutor) driver).executeScript(
                    ""
                            + "arguments[0].scrollIntoView(true);"
                            + "arguments[0].focus();",
                    opcion
            );
            opcion.click();

            System.out.println("✅ Uso CFDI: primera opción seleccionada.");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarUsoCFDIAleatorio(): " + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarUsoCFDIAleatorio()", e);
        }
    }

    @Description("Elimina el concepto haciendo click en el ícono de eliminar")
    public void EliminarConcepto() {
        // Localizador del botón de eliminar por su atributo aria-label
        By eliminarBtnLocator = By.cssSelector("button[aria-label='Eliminar']");

        try {
            // 1) Esperar a que el botón sea clicable y desplazarlo al centro
            WebElement eliminarBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(eliminarBtnLocator)
            );
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'}); arguments[0].focus();",
                    eliminarBtn
            );

            // 2) Forzar el click con JavaScript para evitar interceptaciones
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();",
                    eliminarBtn
            );

            System.out.println("✅ Concepto eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar el concepto: " + e.getMessage());
            throw new RuntimeException("Fallo en EliminarConcepto()", e);
        }
    }
    @Description("Selecciona el concepto de facturación ‘FLETE’ usando full XPath y re-localización para evitar StaleElement")
    public void seleccionarConceptoFacturacionFlete() {
        // 1) Full XPath del input
        By inputLocator = By.xpath(
                "/html/body/div[1]/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/div/"
                        + "div[2]/div/div/div/div/div[2]/div/div/div/div/div/div[1]/div/div/div/input"
        );
        // 2) XPath de la opción “FLETE”
        By optionLocator = By.xpath("//ul[@role='listbox']//li[normalize-space(text())='FLETE']");

        try {
            // 3) Abrir el dropdown
            WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});" +
                            "arguments[0].focus();",
                    campo
            );
            campo.click();

            // 4) Esperar a que la opción “FLETE” esté presente
            wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));

            // 5) Re-localizar y click en “FLETE”
            WebElement flete = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true); arguments[0].focus(); arguments[0].click();",
                    flete
            );

            System.out.println("✅ Concepto de facturación: ‘FLETE’ seleccionado.");
        } catch (StaleElementReferenceException sere) {
            // Reintento rápido en caso de stale
            WebElement fleteRetry = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true); arguments[0].focus(); arguments[0].click();",
                    fleteRetry
            );
            System.out.println("✅ Concepto de facturación: ‘FLETE’ seleccionado tras reintento.");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarConceptoFacturacionFlete(): " + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarConceptoFacturacionFlete()", e);
        }
    }



    @Description("Asigna un valor aleatorio entre 1 y 9999 en el campo 'Importe'")
    public void asignarImporteAleatorio() {
        // 1) Localizador del input
        By importeLocator = By.id("importe");
        try {
            // 2) Esperar a que sea clicable, desplazar y enfocar
            WebElement importeInput = wait.until(
                    ExpectedConditions.elementToBeClickable(importeLocator)
            );
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});" +
                            "arguments[0].focus();",
                    importeInput
            );

            // 3) Generar un entero aleatorio entre 1 y 9999
            int valorAleatorio = new Random().nextInt(9999) + 1;
            String texto = String.valueOf(valorAleatorio);

            // 4) Limpiar el campo y escribir el valor
            importeInput.clear();
            importeInput.sendKeys(texto);

            System.out.println("✅ Importe asignado aleatoriamente: " + texto);
        } catch (Exception e) {
            System.out.println("❌ Error en asignarImporteAleatorio(): " + e.getMessage());
            throw new RuntimeException("Fallo en asignarImporteAleatorio()", e);
        }
    }

    @Description("Hace click en el botón 'Agregar' tras esperar 10 segundos, usando CSS selector")
    public void agregarConcepto() {
        try {
            // 1) Esperar 10 segundos
            // Thread.sleep(10_000);

            // 2) Localizar y clickear el botón 'Agregar' por CSS selector
            WebElement btn = driver.findElement(
                    By.cssSelector("button.MuiButton-containedSecondary")
            );
            btn.click();

            System.out.println("✅ Botón 'Agregar' clickeado correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error en agregarConcepto(): " + e.getMessage());
            throw new RuntimeException("Fallo en agregarConcepto()", e);
        }
    }










    @Description("Hace click en el botón 'Continuar' para guardar viaje, trayendo la ventana y el botón al frente")
    public void clickGuardarViaje() {
        // 1) Locator del botón “Continuar”
        By continueLocator = By.xpath("//button[@type='submit' and normalize-space(text())='Continuar']");

        try {
            // 2) Esperar a que el botón esté presente y clicable
            WebElement continuarBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(continueLocator));

            // 3) Asegurarnos de que la ventana y el botón tienen el foco
            ((JavascriptExecutor) driver).executeScript(
                    "window.focus();" +
                            "arguments[0].scrollIntoView({block:'center'});" +
                            "arguments[0].focus();",
                    continuarBtn
            );

            // 4) Intentar el click
            continuarBtn.click();
            System.out.println("✅ Botón 'Continuar' clickeado correctamente.");
        } catch (StaleElementReferenceException sere) {
            // Si el elemento se vuelve stale, volver a localizar y reintentar
            try {
                WebElement continuarBtn = driver.findElement(continueLocator);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'}); arguments[0].focus();",
                        continuarBtn
                );
                continuarBtn.click();
                System.out.println("✅ Botón 'Continuar' clickeado correctamente (reintento tras stale).");
            } catch (Exception retryEx) {
                System.out.println("❌ Reintento fallido en clickGuardarViaje(): " + retryEx.getMessage());
                throw new RuntimeException("Fallo en clickGuardarViaje() tras stale retry", retryEx);
            }
        } catch (WebDriverException wde) {
            // Si la sesión se perdió, informar claramente
            System.out.println("❌ WebDriverException en clickGuardarViaje(): " + wde.getMessage());
            throw new RuntimeException("Fallo crítico en clickGuardarViaje(): sesión de WebDriver inválida", wde);
        } catch (Exception e) {
            System.out.println("❌ Error en clickGuardarViaje(): " + e.getMessage());
            throw new RuntimeException("Fallo en clickGuardarViaje()", e);
        }
    }


    @Description("Hace clic en el botón 'Enviar' para confirmar el envío del viaje")
    public void clickEnviarConfirmacion() {
        try {
            // 1) Localizar el botón 'Enviar' por su texto y tipo
            By locator = By.xpath("//button[@type='submit' and normalize-space(text())='Enviar']");
            WebElement btnEnviar = wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            );

            // 2) Asegurarnos de que esté visible en pantalla
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", btnEnviar);
            Thread.sleep(200); // breve pausa para evitar interceptaciones de UI

            // 3) Hacer clic
            btnEnviar.click();
            System.out.println("✅ Botón 'Enviar' clickeado correctamente para confirmar envío.");
        } catch (Exception e) {
            System.out.println("❌ Error al clicar el botón 'Enviar': " + e.getMessage());
            throw new RuntimeException("Fallo en clickEnviarConfirmacion()", e);
        }
    }

    @Description("Hace click en el botón 'Aceptar' para confirmar la acción")
    public void clickBotonAceptar() {
        // Locator del botón 'Aceptar' por tipo y texto
        By aceptarBtnLocator = By.xpath("//button[@type='submit' and normalize-space(text())='Aceptar']");

        try {
            // 1) Esperar a que el botón sea clicable
            WebElement aceptarBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(aceptarBtnLocator)
            );

            // 2) Desplazar al centro, enfocar y ejecutar click via JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});" +
                            "arguments[0].focus();" +
                            "arguments[0].click();",
                    aceptarBtn
            );

            System.out.println("✅ Botón 'Aceptar' clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error en clickBotonAceptar(): " + e.getMessage());
            throw new RuntimeException("Fallo en clickBotonAceptar()", e);
        }
    }



    @Description("Hace click en la opción 'Subasta viajes' del menú lateral localizando el botón padre")
    public void clickSubastaViajes() {
        // 1) Localizar el contenedor de texto y luego el elemento clicable padre
        By menuItemBtnLocator = By.xpath(
                "//div[contains(@class,'MuiListItemText-root') and .//p[normalize-space(text())='Subasta viajes']]" +
                        "/ancestor::div[@role='button'][1]"
        );

        try {
            // 2) Esperar a que el botón del menú esté presente y clicable
            WebElement menuItemBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(menuItemBtnLocator)
            );

            // 3) Forzar el click mediante JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});" +
                            "arguments[0].click();",
                    menuItemBtn
            );

            System.out.println("✅ 'Subasta viajes' clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error en clickSubastaViajes(): " + e.getMessage());
            throw new RuntimeException("Fallo en clickSubastaViajes()", e);
        }
    }



    /** Pausa la ejecución 2 segundos sin propagar la InterruptedException. */
    private static void pause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
