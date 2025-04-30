package GMLogistico;


import io.qameta.allure.Description;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnvioDocTrans {

    public static WebDriver driver;
    public static WebDriverWait wait;


    // Guardamos aquí el RFC elegido en cada iteración
    public static String currentRFCTRAN;


    // Clase interna Cliente
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

    @BeforeEach
    public void setup() {
        // 1) Crear y configurar las prefs para desactivar el gestor de contraseñas
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        ChromeOptions options = new ChromeOptions();
        // Aplicar las prefs
        options.setExperimentalOption("prefs", prefs);
        // Forzar modo incógnito (si lo necesitas)
        options.addArguments("--incognito");
        // Desactivar cualquier infobar residual
        options.addArguments("disable-infobars");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-blink-features=PasswordManagerPasswordLeakDetection");

        // 2) Al pasar estas opciones, Chrome al arrancar ya no mostrará el aviso
        driver = new ChromeDriver(options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        driver.manage().window().maximize();
        driver.get("https://logisticav1.gmtransport.co/");
    }


    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


    @RepeatedTest(2)
    @Order(1)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AgregarSubasta() throws Exception {

        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();

        ListadoComprometidas();

        BuscarSubasta();
        BotonAplicar();
        BotonOpcionesYEnviarDocumentos();
        CargarDocumento();
        EnvioDocumento();
        AceptarMensaje();



    }


    @Description("Llena los campos de inicio de sesion con informacion.")
    public static void Iniciosesion() {
        Cliente[] clientes = {
                new Cliente("GMTHCDEMO018", "jose.calidad@gmtransporterp.com", "123456"),
                new Cliente("TEGR820530HTC", "trans2@gmail.com", "123456"),
                new Cliente("CAMR951214MKL", "trans3@gmail.com", "123456"),
                new Cliente("XIA190128J61", "xenon@gmail.com", "123456"),
                new Cliente("VARG001010PLQ", "trans1@gmail.com", "123456")



        };

        Random random = new Random();
        Cliente cliente = clientes[random.nextInt(clientes.length)];

        currentRFCTRAN = cliente.rfc;


        WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"outlined-adornment-rfc-login\"]")));
        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"outlined-adornment-email-login\"]")));
        WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"outlined-adornment-password-login\"]")));


        inputRFC.clear();
        inputEmail.clear();
        inputContrasena.clear();

        inputRFC.sendKeys(cliente.rfc);
        inputEmail.sendKeys(cliente.email);
        inputContrasena.sendKeys(cliente.contrasena);

        System.out.println("Intentando iniciar sesión con: " + cliente.email);
    }

    @Description("Da clic en el botón de 'Iniciar sesión' para ingresar al sistema.")
    public static void BotonIniciosesion() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")
        ));
        submitButton.click();
    }

    @Description("Maneja la alerta de inicio de sesion en caso de tener una sesion ya abierta con el usuario.")
    public static void MensajeAlerta() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                alert.accept();
                System.out.println("Alerta aceptada.");
            }
        } catch (Exception e) {
            System.out.println("No se encontró una alerta o ocurrió un error.");
        }
    }

    @Description("Hace clic en el botón Listado Comprometidas (solo clic normal)")
    public void ListadoComprometidas() {
        try {
            WebElement boton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[1]/div/div/button[3]")
            ));
            boton.click();
            System.out.println("✅ ListadoComprometidas clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar ListadoComprometidas: " + e.getMessage());
        }
    }

    @Description("Busca en el Excel el siguiente folio para el RFC actual y lo marca como procesado")
    public void BuscarSubasta() throws Exception {
        // 1) Activar el campo de búsqueda en la UI (para luego reutilizarlo)
        WebElement buscarInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[2]/div/div[3]/div/div[3]/div/div/input")
        ));
        buscarInput.click();

        // 2) Leer el Excel y preparar variables
        String ruta = "C:\\Users\\LuisSanchez\\IdeaProjects\\GMQA\\src\\test\\resources\\BD Logistico.xlsx";
        String folioParaBuscar = null;

        // 3) Abrir el archivo y recorrer filas
        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sheet = wb.getSheetAt(0);

            // 4) Iterar desde la fila 1 (fila 0 = encabezado)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 4.a) Obtener el RFC de la columna 3 (índice 2)
                Cell rfcCell = row.getCell(2);
                String rfc = (rfcCell != null)
                        ? rfcCell.getStringCellValue().trim()
                        : "";

                // 4.b) Comprobar si ya está procesada la columna 6 (índice 5)
                Cell procCell = row.getCell(5);
                boolean yaProcesado = procCell != null
                        && procCell.getCellType() == CellType.STRING
                        && !procCell.getStringCellValue().trim().isEmpty();

                // 4.c) Si coincide el RFC y no está procesado, capturar folio y marcar
                if (currentRFCTRAN.equals(rfc) && !yaProcesado) {
                    folioParaBuscar = row.getCell(0).getStringCellValue().trim();
                    if (procCell == null) procCell = row.createCell(5);
                    procCell.setCellValue("X");
                    System.out.println("✅ Fila " + i + " marcada como procesada para RFC " + rfc);
                    break;
                }
            }

            // 5) Guardar los cambios en el Excel
            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb.write(fos);
            }
        }

        // 6) Si no hay folio para buscar, informar y salir
        if (folioParaBuscar == null) {
            System.out.println("ℹ️ No hay subastas pendientes para RFC " + currentRFCTRAN);
            return;
        }

        // Ahora folioParaBuscar se pasa a otro método para realizar la búsqueda en la UI
        System.out.println("🔍 Folio listo para buscar: " + folioParaBuscar);
    }

    @Description("Hace clic en el botón Aplicar dentro de la sección de postulaciones")
    public void BotonAplicar() {
        try {
            WebElement botonAplicar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[2]/div/div[3]/div/div[4]/button")
            ));
            botonAplicar.click();
            System.out.println("✅ Botón Aplicar clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aplicar: " + e.getMessage());
        }
    }

    @Description("Abre el menú de opciones y selecciona 'Enviar documentos'")
    public void BotonOpcionesYEnviarDocumentos() {
        try {
            // 1) Hacer clic en el botón de opciones (columna 15)
            WebElement btnOpciones = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[2]/div/div[3]/div/div[5]/div[2]/div/table/tbody/tr/td[15]/div/button")
            ));
            btnOpciones.click();
            System.out.println("✅ Menú de opciones abierto.");

            // 2) Esperar a que aparezca el primer ítem del menú y hacer clic
            WebElement enviarDocs = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@class='list-none p-0 m-0']/li[1]")
            ));
            enviarDocs.click();
            System.out.println("✅ 'Enviar documentos' seleccionado.");
        } catch (Exception e) {
            System.out.println("❌ Error al seleccionar 'Enviar documentos': " + e.getMessage());
        }
    }

    @Description("Selecciona el checkbox de documento y carga el archivo especificado")
    public void CargarDocumento() {
        // Ruta absoluta al archivo que se va a cargar
        String rutaArchivo = "C:\\Users\\LuisSanchez\\IdeaProjects\\GMQA\\src\\test\\resources\\DocLogistico.docx";

        try {
            // 1) Hacer clic en el checkbox para activar el campo de carga
            WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div/div/div/div/div/div[2]/div/div/div/div/div/table/tbody/tr/td[4]/div/label/span/span[1]")
            ));
            checkbox.click();
            System.out.println("✅ Checkbox de documento activado.");

            // 2) Localizar el <input type='file'> que aparece tras activar el checkbox
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@type='file']")
            ));

            // 3) Enviar la ruta del archivo al input para cargarlo
            fileInput.sendKeys(rutaArchivo);
            System.out.println("✅ Archivo cargado desde: " + rutaArchivo);
        } catch (Exception e) {
            System.out.println("❌ Error al cargar el documento: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Envío Documento para confirmar la carga")
    public void EnvioDocumento() {
        try {
            WebElement btnEnvio = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[4]/div/div[2]/button")
            ));
            btnEnvio.click();
            System.out.println("✅ Botón Envío Documento clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Envío Documento: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Aceptar del mensaje emergente")
    public void AceptarMensaje() {
        try {
            WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div[3]/div/div/div[3]/div/button")
            ));
            btnAceptar.click();
            System.out.println("✅ Mensaje aceptado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en AceptarMensaje: " + e.getMessage());
        }
    }

}