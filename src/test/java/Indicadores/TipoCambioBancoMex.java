package Indicadores;

import Utilidades.UtilidadesAllure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.io.File;
import java.time.Duration;
import java.util.List;

public class TipoCambioBancoMex {

    private WebDriver driver;
    private WebDriverWait wait;

    // Si quieres almacenar el tipo de cambio en una variable de clase
    // private String tipoCambMexico;

    public TipoCambioBancoMex(WebDriver driver) {
        this.driver = driver;
        // Ajusta el tiempo de espera a tus necesidades
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Step("Obtener último valor de tipo de cambio en Banxico")
    public void obtenerTipoCambioBanxico() {
        try {
            // 1. Ir a la página principal de tipo de cambio en Banxico
            driver.get("https://www.banxico.org.mx/tipcamb/main.do?page=tip&idioma=sp");

            // 2. Esperar y hacer clic en el link “Más información”
            WebElement linkMasInfo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Más información')]")));
            linkMasInfo.click();

            // 3. Esperar a que cargue la nueva página con la tabla de datos
            //    Ajusta el localizador (By...) según el HTML real.
            WebElement tablaDatos = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[contains(@class, 'table') or @id='tableTipCam']")));

            // 4. Buscar todas las filas (que tengan clase 'renglonNon' o 'renglonPar')
            List<WebElement> filas = tablaDatos.findElements(
                    By.xpath(".//tr[contains(@class, 'renglonNon') or contains(@class, 'renglonPar')]"));

            // Toma la última fila (la más reciente suele estar al final)
            WebElement ultimaFila = filas.get(filas.size() - 1);

            // 5. Dentro de esa última fila, obtener todas las celdas <td>
            List<WebElement> celdas = ultimaFila.findElements(By.tagName("td"));

            // 6. Obtener la última celda (el último valor) => celdas.size() - 1
            WebElement celdaUltimoValor = celdas.get(celdas.size() - 1);

            // 7. Extraer el texto con el tipo de cambio
            String TipoCambMexico = celdaUltimoValor.getText().trim();
            System.out.println("El último valor de tipo de cambio es: " + TipoCambMexico);

            // Si lo quieres almacenar en la clase:
            // this.tipoCambMexico = TipoCambMexico;

        } catch (Exception e) {
            System.err.println("Error al obtener el tipo de cambio en Banxico: " + e.getMessage());
            UtilidadesAllure.manejoError(driver, e,
                    "Error al obtener el tipo de cambio en Banxico");
        }
    }
}
