package GMLogistico;

import io.qameta.allure.Description;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

public class AceptacionPostulacion {

    public static WebDriver driver;
    public static WebDriverWait wait;
    public static String currentRFC;

    public static final String EXCEL_PATH = "C:\\Users\\LuisSanchez\\IdeaProjects\\GMQA\\src\\test\\resources\\BD Logistico.xlsx";

    static class Cliente {
        String rfc, email, contrasena;
        Cliente(String rfc, String email, String contrasena) {
            this.rfc = rfc;
            this.email = email;
            this.contrasena = contrasena;
        }
    }

    private static final Cliente[] clientes = {
            new Cliente("IIA040805DZ4", "elisa.logistica@gmtransporterp.com", "123456"),
            new Cliente("LOGI2222224T5", "logistico2@gmail.com", "123456"),
            new Cliente("LOGI3333335T6", "logi3@gmail.com", "123456"),
            new Cliente("LOGI4444445T6", "logi4@gmail.com", "123456"),
            new Cliente("LOGI1111112Q4", "logistico1@gmail.com", "123456")
    };

    @Test
    @Description("Procesa cada fila del Excel: abre y cierra el navegador por fila, sin detenerse ante fallos")
    public void AgregarSubastaPorFila() throws Exception {
        // 1) Carga el Excel
        Workbook wb;
        try (FileInputStream fis = new FileInputStream(EXCEL_PATH)) {
            wb = WorkbookFactory.create(fis);
        }
        Sheet sheet = wb.getSheetAt(0);

        // 2) Itera cada fila de datos
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String rfcValue = Optional.ofNullable(row.getCell(1))
                    .map(Cell::getStringCellValue)
                    .map(String::trim)
                    .orElse("");
            if (rfcValue.isEmpty()) {
                System.out.println("Fila " + i + ": RFC vacío, saltando.");
                continue;
            }

            // 3) Busca credenciales
            Cliente cliente = Arrays.stream(clientes)
                    .filter(c -> c.rfc.equalsIgnoreCase(rfcValue))
                    .findFirst()
                    .orElse(null);
            if (cliente == null) {
                System.out.println("Fila " + i + ": Sin credenciales para " + rfcValue);
                continue;
            }

            // 4) Configura y abre Chrome en modo headless (o normal si prefieres)
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
            driver = new ChromeDriver(options);
            wait   = new WebDriverWait(driver, Duration.ofSeconds(15));

            try {
                driver.manage().window().maximize();
                driver.get("https://logisticav1.gmtransport.co/");

                // 5) Ejecuta el flujo completo
                currentRFC = cliente.rfc;
                Iniciosesion(cliente);
                BotonIniciosesion();
                MensajeAlerta();
                listadocompeltas();
                BuscarSubasta();
                ClickIconoPostulaciones();
                BotonAceptorPostulacio();
                CheckDocumento();
                BotonEnviar();
                BotonAceptar();

                // Marca la fila como procesada
                Cell procCell = row.getCell(4);
                if (procCell == null) procCell = row.createCell(4);
                procCell.setCellValue("X");

                System.out.println("Fila " + i + ": COMPLETADA para RFC " + rfcValue);
            }
            catch (Exception e) {
                // Si ocurre cualquier fallo, lo logueamos y continuamos
                System.out.println("❌ Error en fila " + i + " (RFC=" + rfcValue + "): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
            finally {
                // 6) Cierra el navegador antes de la siguiente iteración
                driver.quit();
            }
        }

        // 7) Guarda los cambios en el Excel (X para éxitos, sin marca o error si falló)
        try (FileOutputStream fos = new FileOutputStream(EXCEL_PATH)) {
            wb.write(fos);
        }
    }



    @Description("Llena los campos de inicio de sesión con los datos del cliente dado.")
    public void Iniciosesion(Cliente cliente) {
        WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("outlined-adornment-rfc-login")
        ));
        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("outlined-adornment-email-login")
        ));
        WebElement inputContrasena = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("outlined-adornment-password-login")
        ));

        inputRFC.clear();
        inputEmail.clear();
        inputContrasena.clear();

        inputRFC.sendKeys(cliente.rfc);
        inputEmail.sendKeys(cliente.email);
        inputContrasena.sendKeys(cliente.contrasena);
        System.out.println("Iniciando sesión con RFC: " + cliente.rfc);
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

    @Description("Abre el listado de completas haciendo clic en el botón correspondiente")
    public void listadocompeltas() {
        try {
            WebElement btnListado = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/button[2]")
            ));
            btnListado.click();
            System.out.println("✅ Listado de completas abierto correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo abrir el listado de completas: " + e.getMessage());
        }
    }

    @Description("Busca la subasta para el RFC actual y marca la fila procesada")
    public void BuscarSubasta() throws Exception {
        WebElement buscarInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[3]/div/div[4]/div/div/input")
        ));
        buscarInput.click();

        String ruta = EXCEL_PATH;
        String folioParaBuscar = null;

        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String rfc = Optional.ofNullable(row.getCell(1))
                        .map(Cell::getStringCellValue)
                        .map(String::trim)
                        .orElse("");
                Cell procCell = row.getCell(4);
                boolean yaProcesado = procCell != null
                        && procCell.getCellType() == CellType.STRING
                        && !procCell.getStringCellValue().trim().isEmpty();

                if (currentRFC.equals(rfc) && !yaProcesado) {
                    folioParaBuscar = row.getCell(0).getStringCellValue().trim();
                    if (procCell == null) procCell = row.createCell(4);
                    procCell.setCellValue("X");
                    break;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb.write(fos);
            }
        }

        if (folioParaBuscar == null) {
            System.out.println("ℹ️ No hay nuevas subastas para RFC " + currentRFC);
            return;
        }

        System.out.println("🔍 Buscando subasta con folio: " + folioParaBuscar);
        buscarInput.clear();
        buscarInput.sendKeys(folioParaBuscar, Keys.ENTER);
    }

    @Description("Forzar clic en el icono de postulaciones mediante JavaScript")
    public void ClickIconoPostulaciones() {
        By iconoBy = By.xpath("/html/body/div/div/div[1]/main/div/div/div[2]/div[2]/div[1]/div/div[3]/div/div[6]/div/div[1]/table/tbody/tr/td[2]/div/button");
        try {
            WebElement icono = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(iconoBy));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", icono);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", icono);
            System.out.println("✅ Icono de postulaciones clickeado vía JS.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo forzar clic en icono de postulaciones: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Aceptor Postulacio")
    public void BotonAceptorPostulacio() {
        try {
            WebElement boton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div/div/div[1]/main/div/div/div/div[5]/div/div/div/div/div/div[2]/div[3]/button")
            ));
            boton.click();
            System.out.println("✅ Botón Aceptor Postulacio clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aceptor Postulacio: " + e.getMessage());
        }
    }

    @Description("Activa el checkbox de documento en la ventana de postulación")
    public void CheckDocumento() {
        try {
            WebElement checkboxDoc = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div[3]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[3]/span")
            ));
            checkboxDoc.click();
            System.out.println("✅ CheckDocumento activado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo activar CheckDocumento: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Enviar en la ventana de postulación")
    public void BotonEnviar() {
        try {
            WebElement botonEnviar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div[3]/div/div[1]/div[2]/button[2]")
            ));
            botonEnviar.click();
            System.out.println("✅ Botón Enviar clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Enviar: " + e.getMessage());
        }
    }

    @Description("Hace clic en el botón Aceptar en el diálogo emergente")
    public void BotonAceptar() {
        try {
            WebElement botonAceptar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[3]/div[3]/div/div/div[3]/div/button")
            ));
            botonAceptar.click();
            System.out.println("✅ Botón Aceptar clickeado correctamente.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo clicar en Botón Aceptar: " + e.getMessage());
        }
    }

}
