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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreacionViajeLogi {

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
        //options.addArguments("--headless=new"); // Modo headless usando el nuevo modo de Chrome
        options.addArguments("--window-size=1920,1080"); // Definir tamaño para evitar problemas de render
        options.addArguments("--disable-gpu"); // Opcional, para compatibilidad total
        options.addArguments("--no-sandbox"); // Opcional, para ambientes Linux o CI

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
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

    @RepeatedTest(2)
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
            importarMaterialesCompleto();
            pause();
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
            configurarConceptoFacturacion();
            pause();
            clickGuardarViaje();
            pause();
            clickEnviarConfirmacion();



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
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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

        Cliente cliente = clientes[1];

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

    @Description("Escribe ‘INGRESO’ en el campo ‘Tipo de documento’ y lo selecciona")
    public void seleccionarTipoDocumentoIngreso() {
        // full-XPath de tu combobox
        By dropdownLocator = By.xpath(
                "/html/body/div[1]/div/div[1]/main/div/div/div/div[2]/div[1]/div[3]/div/div/"
                        + "div[1]/div/div[2]/div[1]/div/div/div[2]/div/div/div/div/div/div[1]/div/div"
        );

        try {
            // 1) Abrir el combobox
            WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            campo.click();

            // 2) Asegurar que está desplegado
            wait.until(ExpectedConditions.attributeToBe(dropdownLocator, "aria-expanded", "true"));

            // 3) Teclear “INGRESO” y confirmar con ENTER
            campo.sendKeys("INGRESO");
            campo.sendKeys(Keys.ENTER);

            System.out.println("✅ ‘Tipo de documento’ fijado a INGRESO.");
        } catch (Exception e) {
            System.out.println("❌ Error al fijar ‘Ingreso’ en Tipo de documento: " + e.getMessage());
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

    @Description("Selecciona aleatoriamente una opción del dropdown 'Condición de Pago'")
    public void seleccionarCondicionPagoAleatoria() {
        try {
            // 1) Abrir el dropdown
            WebElement campo = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("condicionPagoSelected"))
            );
            campo.click();

            // 2) Seleccionar la primera opción
            By primeraOpcion = By.xpath("//ul[@role='listbox']//li[@role='option'][1]");
            WebElement opcion = wait.until(
                    ExpectedConditions.elementToBeClickable(primeraOpcion)
            );
            opcion.click();

            System.out.println("✅ Condición de Pago seleccionada: \"" + opcion.getText() + "\"");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarCondicionPagoAleatoria(): " + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarCondicionPagoAleatoria()", e);
        }
    }

    @Description("Selecciona aleatoriamente una opción del dropdown 'Método de Pago'")
    public void seleccionarMetodoPagoAleatorio() {
        try {
            // 1) Abrir el dropdown
            WebElement campo = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("metodoPagoSelected"))
            );
            campo.click();

            // 2) Seleccionar la primera opción
            By primeraOpcion = By.xpath("//ul[@role='listbox']//li[@role='option'][1]");
            WebElement opcion = wait.until(
                    ExpectedConditions.elementToBeClickable(primeraOpcion)
            );
            opcion.click();

            System.out.println("✅ Método de Pago seleccionado: \"" + opcion.getText() + "\"");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarMetodoPagoAleatorio(): " + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarMetodoPagoAleatorio()", e);
        }
    }

    @Description("Selecciona aleatoriamente una opción del dropdown 'Uso CFDI'")
    public void seleccionarUsoCFDIAleatorio() {
        try {
            // 1) Abrir el dropdown
            WebElement campo = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("usoCFDI"))
            );
            campo.click();

            // 2) Seleccionar la primera opción
            By primeraOpcion = By.xpath("//ul[@role='listbox']//li[@role='option'][1]");
            WebElement opcion = wait.until(
                    ExpectedConditions.elementToBeClickable(primeraOpcion)
            );
            opcion.click();

            System.out.println("✅ Uso CFDI seleccionado: \"" + opcion.getText() + "\"");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarUsoCFDIAleatorio(): " + e.getMessage());
            throw new RuntimeException("Fallo en seleccionarUsoCFDIAleatorio()", e);
        }
    }

    @Description("Configura el concepto de facturación y guarda los cambios")
    public void configurarConceptoFacturacion() {
        try {
            // 1) Hacer clic en el ícono de edición para "ConceptoFacturacion"
            By editIconLocator = By.cssSelector("button.MuiIconButton-root");
            WebElement editIcon = wait.until(
                    ExpectedConditions.elementToBeClickable(editIconLocator)
            );
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", editIcon);

            // 2) Hacer clic en el input autocomplete
            By conceptoInputLocator = By.id("conceptoFacturacionId");
            WebElement conceptoInput = wait.until(
                    ExpectedConditions.elementToBeClickable(conceptoInputLocator)
            );
            conceptoInput.click();

            // 3) Esperar a que aparezcan las opciones y seleccionar la primera
            By opcionesLocator = By.xpath("//ul[@role='listbox']//li[@role='option']");
            List<WebElement> opciones = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(opcionesLocator)
            );
            if (!opciones.isEmpty()) {
                WebElement primera = opciones.get(0);
                primera.click();
                System.out.println("✅ Concepto de facturación seleccionado: \""
                        + primera.getText() + "\"");
            } else {
                System.out.println("⚠️ No se encontraron opciones para concepto de facturación.");
            }

            // 4) Hacer clic en "Guardar cambios"
            By guardarBtnLocator = By.xpath("//button[normalize-space(.)='Guardar cambios']");
            WebElement guardarBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(guardarBtnLocator)
            );
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", guardarBtn);
            guardarBtn.click();

            System.out.println("✅ Cambios de concepto de facturación guardados correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error en configurarConceptoFacturacion: " + e.getMessage());
            throw new RuntimeException("Fallo en configurarConceptoFacturacion()", e);
        }
    }

    @Description("Hace clic en el botón 'Continuar' (Guardar viaje)")
    public void clickGuardarViaje() {
        try {
            // 1) Localizar el botón de tipo submit con texto 'Continuar'
            By locator = By.xpath("//button[@type='submit' and normalize-space(text())='Continuar']");
            WebElement btnGuardarViaje = wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            );

            // 2) Asegurarnos de que esté visible en pantalla
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", btnGuardarViaje);
            Thread.sleep(200); // breve pausa para asegurar reposicionamiento de UI

            // 3) Clic para guardar el viaje
            btnGuardarViaje.click();
            System.out.println("✅ Botón 'Continuar' (Guardar viaje) clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al clicar el botón 'Continuar' para guardar viaje: " + e.getMessage());
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

    /** Pausa la ejecución 2 segundos sin propagar la InterruptedException. */
    private static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }










}
