package Suite;

import Cobranza.PagoFacturaConcepto;
import Cobranza.PagoFacturaViaje;
import Facturacion.FacturacionConceptoSustitucion;
import Facturacion.FacturacionListadoViajes;
import Facturacion.FacturacionViajeComplemento;
import Facturacion.FacturacionViajeSustitucion;
import Facturacion.FacturaConceptoTimbrada;
import Indicadores.IndicadoresTest;
import Indicadores.ParametrosGenerales;
import Trafico.CartaPorteSustitucion;
import Trafico.CreacionViajeTimbreTraslado;
import Trafico.LiquidacionFiscal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SuiteGlobal {

    @Test
    public void ejecutarPruebasEnCadena() {
        System.out.println("🚀 Iniciando suite de pruebas...");

        // Lista de clases de pruebas a ejecutar en orden
        Class<?>[] pruebas = {
                PagoFacturaConcepto.class,
                PagoFacturaViaje.class,
                FacturacionConceptoSustitucion.class,
                FacturacionListadoViajes.class,
                FacturacionViajeComplemento.class,
                FacturacionViajeSustitucion.class,
                FacturaConceptoTimbrada.class,
                IndicadoresTest.class,
                ParametrosGenerales.class,
                CreacionViajeTimbreTraslado.class,
                CartaPorteSustitucion.class, 
                LiquidacionFiscal.class
        };

        for (Class<?> testClass : pruebas) {
            System.out.println("\n🔍 Ejecutando pruebas de clase: " + testClass.getSimpleName());

            boolean resultado = ejecutarClaseJUnit(testClass);

            if (!resultado) {
                System.err.println("⚠ ERROR en " + testClass.getSimpleName() + ". Saltando a la siguiente clase...");
            }

            esperarSiguientePrueba();
        }

        System.out.println("✅ Suite de pruebas finalizada.");
    }

    private boolean ejecutarClaseJUnit(Class<?> testClass) {
        try {
            // Crear una instancia del lanzador de pruebas
            Launcher launcher = LauncherFactory.create();
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);

            // Crear una solicitud para ejecutar la clase de pruebas
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();

            // Ejecutar la clase de pruebas
            launcher.execute(request);

            // Revisar los resultados
            TestExecutionSummary summary = listener.getSummary();
            if (summary.getFailures().isEmpty()) {
                System.out.println("✅ Todas las pruebas pasaron en: " + testClass.getSimpleName());
                return true;
            } else {
                System.err.println("❌ Fallos detectados en " + testClass.getSimpleName());
                summary.getFailures().forEach(failure ->
                        System.err.println("   ❌ " + failure.getTestIdentifier().getDisplayName() + " - " + failure.getException().getMessage()));
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR al ejecutar " + testClass.getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

    private void esperarSiguientePrueba() {
        try {
            System.out.println("⏳ Esperando 3 segundos antes de la siguiente clase de pruebas...");
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            System.err.println("⚠ Error en la espera entre pruebas: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
