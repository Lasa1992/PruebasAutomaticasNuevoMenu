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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostulacionTranportista {

    public static WebDriver driver;
    public static WebDriverWait wait;
    public static String currentRFCTRAN; // RFC elegido en esta iteración

    static class Cliente {
        String rfc, email, contrasena;
        Cliente(String rfc, String email, String contrasena) {
            this.rfc = rfc; this.email = email; this.contrasena = contrasena;
        }
    }

    @BeforeEach
    public void setup() {
        Map<String,Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--incognito", "disable-infobars",
                "--disable-save-password-bubble",
                "--disable-blink-features=PasswordManagerPasswordLeakDetection");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        driver.manage().window().maximize();
        driver.get("https://logisticav1.gmtransport.co/");

        // pausa de 1s
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @RepeatedTest(5)
    @Order(1)
    @Description("Generación de Cheque con Datos Aleatorios")
    public void AgregarSubasta() throws Exception {
        Iniciosesion();
        BotonIniciosesion();
        MensajeAlerta();
        BuscarSubasta();
        BotonAplicar();
        BotonSolicitar();
        Comentario();
        CheckAutorizacion();
        AceptarPostualacion();
        BotonEnviarPostulacion();
        guardarRFCPostulacionEnExcel();

        // pausa de 1s
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Llena los campos de inicio de sesion con informacion.")
    public static void Iniciosesion() {
        Cliente[] clientes = {
                new Cliente("GMTHCDEMO018","jose.calidad@gmtransporterp.com","123456"),
                new Cliente("TEGR820530HTC","trans2@gmail.com","123456"),
                new Cliente("CAMR951214MKL","trans3@gmail.com","123456"),
        };
        Cliente cliente = clientes[new Random().nextInt(clientes.length)];
        currentRFCTRAN = cliente.rfc;

        WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"outlined-adornment-rfc-login\"]")));
        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"outlined-adornment-email-login\"]")));
        WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"outlined-adornment-password-login\"]")));

        inputRFC.clear(); inputEmail.clear(); inputContrasena.clear();
        inputRFC.sendKeys(cliente.rfc);
        inputEmail.sendKeys(cliente.email);
        inputContrasena.sendKeys(cliente.contrasena);

        System.out.println("Intentando iniciar sesión con: " + cliente.email);
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Da clic en el botón de 'Iniciar sesión'")
    public static void BotonIniciosesion() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div[2]/div/div[2]/form/div[4]/button")
        ));
        btn.click();
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Maneja alerta JS de sesión ya iniciada")
    public static void MensajeAlerta() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            System.out.println("Alerta aceptada.");
        } catch (Exception e) {
            System.out.println("No se encontró alerta de sesión.");
        }
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Busca la subasta mediante el primer folio no procesado y marca 'postulacion'")
    public void BuscarSubasta() throws Exception {
        // 1) Hacer clic en el icono de búsqueda
        WebElement buscarIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[2]/div/div[1]/div/div[1]/div/div[4]/div/div/input")
        ));
        buscarIcon.click();

        // 2) Leer el primer folio no procesado del Excel y marcar la columna 4 ("postulacion")
        String ruta = "C:\\Users\\LuisSanchez\\Desktop\\Excel Logistico\\BD Logistico.xlsx";
        String folio = "";
        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheetAt(0);
            for (int i = 1; i <= sh.getLastRowNum(); i++) {
                Row row = sh.getRow(i);
                if (row == null) continue;

                Cell folioCell = row.getCell(0);
                Cell postCell  = row.getCell(3); // columna 4

                boolean yaPostulado = postCell != null
                        && postCell.getCellType() != CellType.BLANK
                        && !postCell.getStringCellValue().trim().isEmpty();

                if (folioCell != null
                        && !folioCell.getStringCellValue().trim().isEmpty()
                        && !yaPostulado) {

                    // 2.a) Capturamos el folio
                    folio = folioCell.getStringCellValue();

                    // 2.b) Marcamos "X" en la columna 4 para indicar postulación
                    if (postCell == null) postCell = row.createCell(3);
                    postCell.setCellValue("X");
                    break;
                }
            }

            // 2.c) Guardar cambios en el fichero
            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb.write(fos);
            }
        }

        if (folio.isEmpty()) {
            System.out.println("ℹ️  No hay folios pendientes de postulación.");
            return;
        }

        System.out.println("✅ Folio seleccionado para búsqueda: " + folio);

        // 3) Ingresar el folio en el cuadro de búsqueda y ejecutar
        //    Ajusta el XPath según tu input de búsqueda real
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Buscar']")));
        searchInput.clear();
        searchInput.sendKeys(folio, Keys.ENTER);
    }

    @Description("Hace clic en el botón Aplicar")
    public void BotonAplicar() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[2]/div/div[1]/div/div[1]/div/div[5]/button")
            ));
            btn.click();
        } catch (Exception e) {
            System.out.println("Error en BotonAplicar: " + e.getMessage());
        }
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Hace clic en el botón Solicitar")
    public void BotonSolicitar() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[1]/div[2]/div/div[1]/div/div[2]/div[1]/div/table/tbody/tr/td[2]/div/span[1]")
            ));
            btn.click();
        } catch (Exception e) {
            System.out.println("Error en BotonSolicitar: " + e.getMessage());
        }
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Escribe el comentario predefinido")
    public void Comentario() {
        try {
            WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"comentarioSolicitud\"]")
            ));
            campo.click(); campo.clear();
            campo.sendKeys("Hola Buen Dia Me Interesa El Viaje");
        } catch (Exception e) {
            System.out.println("Error en Comentario: " + e.getMessage());
        }
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Marca el checkbox de autorización vía JS")
    public void CheckAutorizacion() {
        By by = By.xpath("/html/body/div[2]/div[3]/div/div/div/div[1]/div[1]/div/div[6]/div/div[1]/span/input");
        try {
            WebElement cb = driver.findElement(by);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true); arguments[0].click();", cb);
        } catch (Exception e) {
            System.out.println("Error en CheckAutorizacion: " + e.getMessage());
        }
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Hace clic en Aceptar Postulación")
    public void AceptarPostualacion() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div[3]/div/div/div/div[1]/div[2]/div/div/div/div[1]/button")
            ));
            btn.click();
        } catch (Exception e) {
            System.out.println("Error en AceptarPostualacion: " + e.getMessage());
        }
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Hace clic en el diálogo final de aceptación")
    public void AceptarDialogo() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[3]/div[3]/div/div/div[3]/div/button")
            ));
            btn.click();
        } catch (Exception e) {
            System.out.println("Error en AceptarDialogo: " + e.getMessage());
        }
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    @Description("Hace clic en el botón Enviar Postulación al final del flujo")
    public void BotonEnviarPostulacion() {
        try {
            WebElement btnEnviar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[3]/div[3]/div/div/div[3]/div/button")
            ));
            btnEnviar.click();
            System.out.println("✅ Botón Enviar Postulación clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Enviar Postulación: " + e.getMessage());
        }
        // pausa de 1 segundo
        try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }


    @Description("Anota el RFC de la postulación en la tercera columna del Excel sin sobrescribir los registros ya procesados")
    public void guardarRFCPostulacionEnExcel() throws Exception {
        String ruta = "C:\\Users\\LuisSanchez\\Desktop\\Excel Logistico\\BD Logistico.xlsx";

        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheetAt(0);
            boolean escrito = false;

            // Recorremos todas las filas buscando una postulación marcada ("X")
            // pero que aún no tenga RFC en la columna 3
            for (int i = 1; i <= sh.getLastRowNum(); i++) {
                Row row = sh.getRow(i);
                if (row == null) continue;

                Cell postCell = row.getCell(3); // columna 4: postulacion
                if (postCell == null ||
                        postCell.getCellType() != CellType.STRING ||
                        !"X".equalsIgnoreCase(postCell.getStringCellValue().trim())) {
                    // No es una fila marcada para postulación, saltar
                    continue;
                }

                Cell rfcCell = row.getCell(2); // columna 3
                boolean tieneRFC = rfcCell != null
                        && rfcCell.getCellType() == CellType.STRING
                        && !rfcCell.getStringCellValue().trim().isEmpty();

                if (!tieneRFC) {
                    // Si aún no hay RFC, lo escribimos aquí
                    if (rfcCell == null) {
                        rfcCell = row.createCell(2);
                    }
                    rfcCell.setCellValue(currentRFCTRAN);
                    System.out.println("✅ RFC '" + currentRFCTRAN +
                            "' anotado en fila " + i + ", columna 3.");
                    escrito = true;
                    break;
                }
                // Si ya tiene RFC, saltamos a la siguiente fila marcada
            }

            if (!escrito) {
                System.out.println("ℹ️  No se encontró fila marcada sin RFC para anotar.");
            }

            // Guardar cambios
            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb.write(fos);
            }
        }
    }
}
