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


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TerminaViajeTrans {

    public static WebDriver driver;
    public static WebDriverWait wait;

    public static String currentRFCTRAN;
    private static boolean loginRealizado = false;

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
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");

            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.manage().window().maximize();
            driver.get("https://logisticav1.gmtransport.co/");
        } catch (Exception e) {
            System.out.println("Error en setup: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Error en tearDown: " + e.getMessage());
        }
    }

    @BeforeEach
    public void volverAMisViajes() {
        // Si aún no iniciamos sesión, no navegues a /viajes
        if (!loginRealizado) {
            return;
        }
        driver.get("https://logisticav1.gmtransport.co/viajes");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/viajes' and contains(., 'Mis Viajes')]")
        ));
    }

    @Test
    @Order(1)
    public void InicioSeSionLog() {
        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();
        // Aquí ya entramos, activamos la bandera
        loginRealizado = true;
    }

    @RepeatedTest(164)
    @Order(2)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AceptarSolicitud() {

        irAListadoDeViajes();
        pause();
        Filtros();
        pause();
        abrirMenuYSeleccionarDocumentar();
       // seleccionarYDocumentarRegistroPorDocumentarTransportista();
        pause();
        BotonInformacion();
        pause();
        Unidad();
        pause();
        Operador();
        pause();
        BotonInformacionTransportista();
        BotonSeguro();
        BotonDocumentarViaje();
        BotonAceptar();


    }

    @Description("Llena los campos de inicio de sesión con información fija.")
    public static void Iniciosesion() {
        try {
            Cliente[] clientes = {
                    new Cliente("GMTHCDEMO018", "jose.calidad@gmtransporterp.com", "123456"),
                    new Cliente("TEGR820530HTC", "trans2@gmail.com", "123456"),
                    new Cliente("CAMR951214MKL", "trans3@gmail.com", "123456"),
                    new Cliente("XIA190128J61", "xenon@gmail.com", "123456"),
                    new Cliente("VARG001010PLQ", "trans1@gmail.com", "123456")
            };
            Cliente cliente = clientes[0];
            currentRFCTRAN = cliente.rfc;

            WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-rfc-login")));
            WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-email-login")));
            WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-password-login")));

            inputRFC.clear();
            inputRFC.sendKeys(cliente.rfc);
            inputEmail.clear();
            inputEmail.sendKeys(cliente.email);
            inputContrasena.clear();
            inputContrasena.sendKeys(cliente.contrasena);

            System.out.println("Intentando iniciar sesión con: " + cliente.email);
        } catch (Exception e) {
            System.out.println("Error en Iniciosesion: " + e.getMessage());
        }
    }

    @Description("Da clic en el botón de 'Iniciar sesión'.")
    public static void BotonIniciosesion() {
        try {
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='root']/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")
            ));
            submitButton.click();
        } catch (Exception e) {
            System.out.println("Error en BotonIniciosesion: " + e.getMessage());
        }
    }

    @Description("Maneja alerta de sesión previa.")
    public static void MensajeAlerta() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            System.out.println("Alerta aceptada.");
        } catch (TimeoutException e) {
            System.out.println("No se encontró alerta o no era necesaria.");
        } catch (Exception e) {
            System.out.println("Error en MensajeAlerta: " + e.getMessage());
        }
    }

    @Description("Navega al listado de 'Mis Viajes'")
    public static void irAListadoDeViajes() {
        try {
            WebElement botonViajes = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/viajes' and contains(., 'Mis Viajes')]")
            ));
            botonViajes.click();
            System.out.println("✅ Se hizo clic en 'Mis Viajes'.");
        } catch (Exception e) {
            System.out.println("❌ Error al intentar ir a 'Mis Viajes': " + e.getMessage());
        }
    }


    @Description("Aplica el filtro 'Por documentar Transportista' en el listado de viajes")
    public static void Filtros() {
        try {
            // 1) Localiza y abre el combobox de estatus
            By combobox = By.xpath("//div[@role='combobox' and @aria-labelledby='idEstatus']");
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(combobox));
            new Actions(driver)
                    .moveToElement(dropdown)
                    .pause(Duration.ofMillis(200))
                    .click()
                    .perform();

            // 2) Espera la opción 'Por documentar Transportista' y haz clic
            By optionLocator = By.xpath(
                    "//li[@role='option' and normalize-space(text())='Por documentar Transportista']"
            );
            WebElement option = wait.until(
                    ExpectedConditions.elementToBeClickable(optionLocator)
            );
            option.click();

            // 3) Haz clic en el botón 'Aplicar'
            By applyBtn = By.xpath("//button[normalize-space(text())='Aplicar']");
            WebElement botonAplicar = wait.until(
                    ExpectedConditions.elementToBeClickable(applyBtn)
            );
            botonAplicar.click();

            System.out.println("✅ Filtro 'Por documentar Transportista' aplicado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error en Filtros: " + e.getMessage());
        }
    }


    @Description("Abre el menú de opciones del primer registro y selecciona 'Documentar' vía JS usando el menú contextual correcto")
    public static void abrirMenuYSeleccionarDocumentar() {
        try {
            // 1) Abre el menú “more” con JS
            By menuBtnLocator = By.cssSelector("tbody.MuiTableBody-root tr:first-child button[aria-label='more']");
            WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(menuBtnLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", menuBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);
            System.out.println("✅ Menú de opciones abierto (JS click).");

            // 2) Espera el UL del menú contextual
            By contextMenuLocator = By.xpath("//ul[contains(@class,'list-none')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(contextMenuLocator));

            // 3) Localiza el LI “Documentar” dentro de ese menú
            By documentarLocator = By.xpath(
                    "//ul[contains(@class,'list-none')]//li[normalize-space(text())='Documentar']"
            );
            WebElement documentar = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(documentarLocator)
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", documentar);

            // 4) Fuerza el click en “Documentar”
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", documentar);
            System.out.println("✅ Opción 'Documentar' seleccionada (JS click).");

        } catch (Exception e) {
            System.out.println("❌ Error al abrir menú y seleccionar 'Documentar': " + e.getMessage());
        }
    }


    @Description("Selecciona el primer registro con estado 'Por documentar Transportista' recorriendo la paginación mediante el botón siguiente")
    public static void seleccionarYDocumentarRegistroPorDocumentarTransportista() {
        try {
            int pagina = 1;
            while (true) {

                // 1) Obtener todas las filas de la tabla
                List<WebElement> filas = wait.until(
                        ExpectedConditions.visibilityOfAllElementsLocatedBy(
                                By.cssSelector("tbody.MuiTableBody-root tr")
                        )
                );

                // 2) Buscar en cada fila el estado “Por documentar Transportista”
                for (WebElement fila : filas) {
                    String estado = fila.findElement(By.cssSelector("td:first-child p")).getText().trim();
                    if ("Por documentar Transportista".equals(estado)) {
                        // 2.1) Abrir menú “more” de esa fila
                        WebElement moreBtn = fila.findElement(By.cssSelector("button[aria-label='more']"));
                        ((JavascriptExecutor) driver)
                                .executeScript("arguments[0].scrollIntoView({block:'center'});", moreBtn);
                        ((JavascriptExecutor) driver)
                                .executeScript("arguments[0].click();", moreBtn);
                        System.out.println("✅ Menú abierto en fila con estado 'Por documentar Transportista'.");

                        // 2.2) Clicar “Documentar”
                        By documentarLocator = By.xpath(
                                "//ul[contains(@class,'list-none')]//li[normalize-space(text())='Documentar']"
                        );
                        WebElement documentar = wait.until(
                                ExpectedConditions.elementToBeClickable(documentarLocator)
                        );
                        ((JavascriptExecutor) driver)
                                .executeScript("arguments[0].click();", documentar);
                        System.out.println("✅ Opción 'Documentar' seleccionada.");

                        System.out.println("🔍 Página " + pagina + ": buscando registros...");
                        return;
                    }
                }

                // 3) Intentar avanzar de página haciendo clic en el botón siguiente
                try {
                    By nextBtnLocator = By.xpath("/html/body/div[1]/div/div[1]/main/div/div/div[2]/div/div/div[6]/div[2]/div/div/div[3]/button[2]");
                    WebElement nextBtn = wait.until(
                            ExpectedConditions.elementToBeClickable(nextBtnLocator)
                    );
                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollIntoView({block:'center'});", nextBtn);
                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click();", nextBtn);
                    pagina++;
                    // pequeña pausa para que cargue la nueva página
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("ℹ️ No hay más páginas o no se encontró control de paginación → saliendo.");
                    break;
                }
            }

            System.out.println("ℹ️ No se encontró ningún registro 'Por documentar Transportista'.");
        } catch (Exception e) {
            System.out.println("❌ Error en seleccionarYDocumentarRegistroPorDocumentarTransportista: " + e.getMessage());
        }
    }



    @Description("Hace clic en el botón 'Continuar' después de llenar la información")
    public static void BotonInformacion() {
        try {
            // Localiza y espera a que el botón 'Continuar' esté clickable
            By continuarLocator = By.xpath("//button[normalize-space(text())='Continuar']");
            WebElement botonContinuar = wait.until(ExpectedConditions.elementToBeClickable(continuarLocator));

            // Asegura visibilidad
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonContinuar);

            // Clic con JS para evitar posibles interceptaciones
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonContinuar);
            System.out.println("✅ Botón 'Continuar' clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al hacer clic en 'Continuar': " + e.getMessage());
        }
    }

    @Description("Selecciona la primera opción del campo 'Unidad', desplazándose si está fuera de pantalla")
    public static void Unidad() {
        try {
            // 1) Localiza el input de Unidad
            WebElement inputUnidad = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("idUnidad"))
            );
            // 2) Desplaza la ventana hasta el input para que quede centrado en pantalla
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", inputUnidad);
            // 3) Clickea el input para abrir el dropdown
            wait.until(ExpectedConditions.elementToBeClickable(inputUnidad)).click();

            // 4) Espera a que aparezca la lista de opciones
            By optionsLocator = By.cssSelector("ul[role='listbox'] li[role='option']");
            List<WebElement> opciones = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator)
            );

            // 5) Selecciona la primera opción si existe
            if (!opciones.isEmpty()) {
                WebElement primeraOpcion = opciones.get(0);
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", primeraOpcion);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", primeraOpcion);
                System.out.println("✅ Unidad seleccionada: " + primeraOpcion.getText());
            } else {
                System.out.println("❌ No se encontraron opciones para 'Unidad'.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en Unidad: " + e.getMessage());
        }
    }


    @Description("Selecciona la primera opción del campo 'Operador', desplazándose si está fuera de pantalla")
    public static void Operador() {
        try {
            // 1) Localiza el input de Operador
            WebElement inputOperador = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("idOperador"))
            );
            // 2) Desplaza la ventana hasta el input para que quede centrado en pantalla
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", inputOperador);
            // 3) Clickea el input para abrir el dropdown
            wait.until(ExpectedConditions.elementToBeClickable(inputOperador)).click();

            // 4) Espera a que aparezca la lista de opciones
            By optionsLocator = By.cssSelector("ul[role='listbox'] li[role='option']");
            List<WebElement> opciones = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator)
            );

            // 5) Selecciona la primera opción si existe
            if (!opciones.isEmpty()) {
                WebElement primeraOpcion = opciones.get(0);
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", primeraOpcion);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", primeraOpcion);
                System.out.println("✅ Operador seleccionado: " + primeraOpcion.getText());
            } else {
                System.out.println("❌ No se encontraron opciones para 'Operador'.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en Operador: " + e.getMessage());
        }
    }


    @Description("Hace clic en el botón 'Continuar' del flujo de transportista")
    public static void BotonInformacionTransportista() {
        try {
            // 1) Localiza el botón 'Continuar' exacto para transportista
            By continuarLocator = By.xpath("//button[normalize-space(text())='Continuar']");
            WebElement botonContinuar = wait.until(ExpectedConditions.elementToBeClickable(continuarLocator));

            // 2) Asegura que esté en pantalla
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonContinuar);

            // 3) Fuerza el click por JS para evitar interceptaciones
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonContinuar);
            System.out.println("✅ Botón 'Continuar' pulsado para transportista.");
        } catch (Exception e) {
            System.out.println("❌ Error al pulsar 'Continuar' para transportista: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón 'Continuar' de la sección de seguro")
    public static void BotonSeguro() {
        try {
            // 1) Localiza el botón 'Continuar' cuyo texto es Seguro (Continuar)
            By seguroLocator = By.xpath("//button[normalize-space(text())='Continuar']");
            WebElement botonSeguro = wait.until(
                    ExpectedConditions.elementToBeClickable(seguroLocator)
            );

            // 2) Asegura que esté en pantalla
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonSeguro);

            // 3) Fuerza el clic vía JS para evitar interceptaciones
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonSeguro);
            System.out.println("✅ Botón 'Continuar' (Seguro) pulsado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al pulsar 'Continuar' (Seguro): " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón 'Documentar viaje'")
    public static void BotonDocumentarViaje() {
        try {
            // 1) Localiza el botón 'Documentar viaje' por su texto
            By docViajeLocator = By.xpath("//button[normalize-space(text())='Documentar viaje']");
            WebElement botonDocViaje = wait.until(
                    ExpectedConditions.elementToBeClickable(docViajeLocator)
            );

            // 2) Asegura que esté en pantalla
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonDocViaje);

            // 3) Fuerza el clic vía JS para evitar interceptaciones
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonDocViaje);
            System.out.println("✅ Botón 'Documentar viaje' pulsado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al pulsar 'Documentar viaje': " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón 'Aceptar'")
    public static void BotonAceptar() {
        try {
            // 1) Localiza el botón 'Aceptar' por su texto
            By aceptarLocator = By.xpath("//button[normalize-space(text())='Aceptar']");
            WebElement botonAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(aceptarLocator)
            );

            // 2) Asegura que esté en pantalla
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonAceptar);

            // 3) Fuerza el clic vía JS para evitar interceptaciones
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonAceptar);
            System.out.println("✅ Botón 'Aceptar' pulsado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al pulsar 'Aceptar': " + e.getMessage());
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
