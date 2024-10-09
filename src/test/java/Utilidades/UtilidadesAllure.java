package Utilidades;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class UtilidadesAllure {

    // Función para manejar excepciones y registrar información en Allure
    public static void manejoError(WebDriver driver, Exception e, String mensajeAdicional) {
        // Toma una captura de pantalla
        byte[] captura = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Adjunta el mensaje de error al reporte de Allure
        //String mensajeError = "Error encontrado: " + e.getMessage();
        //Allure.addAttachment("Mensaje de Error", new ByteArrayInputStream(mensajeError.getBytes()));

        // Adjunta la captura de pantalla al reporte de Allure
        Allure.addAttachment("Evidencia", new ByteArrayInputStream(captura));

        // Adjunta el mensaje adicional (ej. texto de una ventana emergente o elemento) al reporte de Allure
        if (mensajeAdicional != null && !mensajeAdicional.isEmpty()) {
            Allure.addAttachment("Mensaje Obtenido de la Ventana", new ByteArrayInputStream(mensajeAdicional.getBytes()));
        }

        // Marca la prueba como fallida
        Assertions.fail("La prueba falló debido a: " + e.getMessage());
    }

    public static void capturaImagen(WebDriver driver) {
        // Toma una captura de pantalla
        byte[] captura = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        // Adjunta la captura de pantalla al reporte de Allure
        Allure.addAttachment("Captura de Pantalla", new ByteArrayInputStream(captura));
    }

}
