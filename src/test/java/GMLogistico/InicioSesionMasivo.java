package GMLogistico;

import io.qameta.allure.Description;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InicioSesionMasivo {

    private static final String RFC_CONSTANT = "WERX631016S30";
    public static WebDriver driver;
    public static WebDriverWait wait;



    @Test
    @Order(1)
    @Description("Prueba de inicio de sesión con diferentes usuarios desde un archivo Excel")
    public void testInicioSesionDesdeExcel() {
        String archivoExcel = "C:\\Users\\UsuarioY\\Desktop\\Pruebas Automaticas\\XLSXPruebas\\Usuarios.xlsx"; // Reemplazar con la ruta correcta del archivo Excel
        try (FileInputStream file = new FileInputStream(new File(archivoExcel));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Omitir la primera fila (cabecera)

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell emailCell = row.getCell(7); // Columna Correo Electrónico
                Cell passwordCell = row.getCell(10); // Columna Contraseña
                Cell procesadoCell = row.getCell(11); // Columna Procesado

                if (emailCell != null && passwordCell != null && procesadoCell != null) {
                    String email = emailCell.getStringCellValue(); // Valor del Correo Electrónico
                    String password = passwordCell.getStringCellValue(); // Valor de la Contraseña
                    String procesado = procesadoCell.getStringCellValue(); // Valor de la columna Procesado

                    // Solo procesar si el campo "procesado" está marcado como "sí"
                    if ("Si".equalsIgnoreCase(procesado)) {
                        System.out.println("Procesando usuario: " + email);

                        // Iniciar el navegador para cada usuario
                        driver = new ChromeDriver();
                        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                        driver.get("http://190.9.53.4:9093/");

                        try {
                            iniciarSesion(email, password);
                            manejarAlertaOCerrarNavegador();
                        } catch (Exception e) {
                            UtilidadesAllureLog.manejoError(driver, e, "Error durante el proceso de inicio de sesión");
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo Excel: " + e.getMessage());
        }
    }

    // Metodo inicio de sesion
    public void iniciarSesion(String email, String password) {
        try {
            WebElement inputRFC = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("outlined-adornment-rfc-login")));
            WebElement inputEmail = driver.findElement(By.id("outlined-adornment-email-login"));
            WebElement inputContrasena = driver.findElement(By.id("outlined-adornment-password-login"));

            inputRFC.clear();
            inputRFC.sendKeys(RFC_CONSTANT);

            inputEmail.clear();
            inputEmail.sendKeys(email);

            inputContrasena.clear();
            inputContrasena.sendKeys(password);

            // Hacer clic en el botón de inicio de sesión
            WebElement botonIniciarSesion = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Iniciar sesión')]")
            ));
            botonIniciarSesion.click();
            UtilidadesAllureLog.capturaImagen(driver);
        } catch (Exception e) {
            UtilidadesAllureLog.manejoError(driver, e, "Error durante el intento de inicio de sesión");
        }
    }

    //Metodo para manejar la posible alerta de usuario ya dentro del sistema y cierre del navegador para prosegui con otro usuario.
    public void manejarAlertaOCerrarNavegador() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            System.out.println("Alerta aceptada.");
        } catch (Exception e) {
            System.out.println("No se encontró una alerta. Cerrando el navegador y continuando con el siguiente usuario.");
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
