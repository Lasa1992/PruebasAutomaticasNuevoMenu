package Utilidades;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class UtilidadesAllure {

    // Función para manejar excepciones y registrar información en Allure
    public static void manejoError(WebDriver driver, Exception e, String mensajeAdicional) {
        // Toma una captura de pantalla
        byte[] captura = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Adjunta la captura de pantalla al reporte de Allure
        Allure.addAttachment("Evidencia", new ByteArrayInputStream(captura));

        // Adjunta el mensaje adicional al reporte de Allure
        if (mensajeAdicional != null && !mensajeAdicional.isEmpty()) {
            Allure.addAttachment("Mensaje Obtenido de la Ventana", new ByteArrayInputStream(mensajeAdicional.getBytes()));
        }

        // Marca la prueba como fallida
        Assertions.fail("La prueba falló debido a: " + e.getMessage());
    }

    // Función para capturar y adjuntar una imagen al reporte de Allure
    public static void capturaImagen(WebDriver driver) {
        byte[] captura = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Captura de Pantalla", new ByteArrayInputStream(captura));
    }

    // Función para limpiar los resultados anteriores de Allure
    public static void limpiarResultadosAllure() {
        try {
            String command = "cmd /c rmdir /s /q target\\allure-results"; // Comando para Windows
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Resultados antiguos de Allure eliminados.");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al limpiar los resultados de Allure: " + e.getMessage());
        }
    }

    // Función para generar el reporte de Allure
    public static void generarReporteAllure() {
        try {
            String command = "cmd /c allure generate target/allure-results -o target/allure-report --clean";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Reporte de Allure generado exitosamente.");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al generar el reporte de Allure: " + e.getMessage());
        }
    }

    // Función para iniciar el servidor de Allure
    public static void iniciarAllureServer() {
        try {
            String command = "cmd /c allure serve target/allure-results";
            Process process = Runtime.getRuntime().exec(command);
            System.out.println("Servidor Allure iniciado. Visítalo en: http://localhost:4040");
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor Allure: " + e.getMessage());
        }
    }
}
