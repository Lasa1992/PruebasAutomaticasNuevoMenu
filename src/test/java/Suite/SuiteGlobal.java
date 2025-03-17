package Suite;

import Facturacion.*;
import Indicadores.InicioSesion;
import org.junit.jupiter.api.*;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SuiteGlobal {

    // Lista de navegadores en los que se ejecutarán las pruebas
    private static final String[] NAVEGADORES = {"chrome", "firefox", "edge"};

    // Número de hilos para la ejecución en paralelo
    private static final int NUMERO_HILOS = 4;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NUMERO_HILOS);

    @Test
    @Order(1)
    public void ejecutarPruebasEnTodosLosNavegadores() {
        for (String navegador : NAVEGADORES) {
            System.out.println("\n======================================");
            System.out.println("🚀 Ejecutando pruebas en: " + navegador.toUpperCase());
            System.out.println("======================================");

            ejecutarPruebasEnNavegador(navegador);
        }
    }

    /**
     * Ejecuta todas las pruebas en un navegador específico.
     */
    private void ejecutarPruebasEnNavegador(String navegador) {
        Class<?>[] pruebas = {
                FacturacionGeneral.class,
                FacturacionGeneralDescImpr.class,
                FacturacionGeneralSustitucion.class,
                FacturaConceptoTimbrada.class,
                FacturacionConceptoSustitucion.class,
                FacturacionConceptoDescImpr.class
        };

        List<Callable<Boolean>> tareas = new ArrayList<>();
        for (Class<?> testClass : pruebas) {
            tareas.add(() -> ejecutarClaseJUnit(testClass, navegador));
        }

        try {
            List<Future<Boolean>> resultados = executorService.invokeAll(tareas);
            for (int i = 0; i < pruebas.length; i++) {
                boolean resultado = resultados.get(i).get();
                String nombrePrueba = pruebas[i].getSimpleName();

                if (resultado) {
                    System.out.println("✅ PRUEBA EXITOSA en " + navegador.toUpperCase() + ": " + nombrePrueba);
                } else {
                    System.err.println("❌ PRUEBA FALLIDA en " + navegador.toUpperCase() + ": " + nombrePrueba);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR en ejecución en " + navegador.toUpperCase() + ": " + e.getMessage());
        }
    }

    /**
     * Ejecuta una prueba en el navegador especificado.
     */
    private boolean ejecutarClaseJUnit(Class<?> testClass, String navegador) {
        try {
            System.out.println("🚀 Iniciando prueba: " + testClass.getSimpleName() + " en " + navegador.toUpperCase());

            // 🛠️ Pasa el navegador como variable del sistema para que las pruebas lo reciban
            System.setProperty("navegador", navegador);

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();

            Launcher launcher = LauncherFactory.create();
            launcher.execute(request);

            // Cierra la sesión después de cada prueba
            InicioSesion.cerrarSesion();
            return true;
        } catch (Exception e) {
            System.err.println("⚠ ERROR en " + testClass.getSimpleName() + " en " + navegador.toUpperCase() + ": " + e.getMessage());
            return false;
        }
    }
}
