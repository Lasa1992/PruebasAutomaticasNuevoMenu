package GMLogistico;

import org.junit.jupiter.api.*;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SuiteGlobalog {

    private static final Map<String, TestResult> resultados = new LinkedHashMap<>();

    @Test
    @Order(1)
    public void ejecutarSuite() {

        Class<?>[] pruebas = {

                SubastaLog.class,
                PostulacionTranportista.class,
                AceptacionPostulacion.class,
                EnvioDocTrans.class


        };

        int exitosas = 0;
        int fallidas = 0;
        double tiempoTotal = 0;

        for (Class<?> testClass : pruebas) {
            long inicioPrueba = System.nanoTime();
            boolean exito = ejecutarClaseJUnit(testClass);
            double duracionPrueba = (System.nanoTime() - inicioPrueba) / 60_000_000_000.0; // minutos

            resultados.put(testClass.getSimpleName(), new TestResult(exito, duracionPrueba));

            if (exito) exitosas++;
            else fallidas++;

            tiempoTotal += duracionPrueba;
        }

        mostrarResumen(exitosas, fallidas, tiempoTotal);
    }

    private boolean ejecutarClaseJUnit(Class<?> testClass) {
        OverallTestListener listener = new OverallTestListener();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(testClass))
                .build();

        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        return listener.areAllTestsSuccessful();
    }

    private void mostrarResumen(int exitosas, int fallidas, double tiempoTotal) {
        System.out.println("\n======================================");
        System.out.println("📊 RESUMEN FINAL DE PRUEBAS");
        System.out.println("======================================");

        resultados.forEach((nombre, resultado) -> {
            String estado = resultado.exito ? "✅ EXITOSA" : "❌ FALLIDA";
            System.out.printf("%s - %s (⏱ %.2f min)%n", estado, nombre, resultado.duracion);
        });

        System.out.println("\n======================================");
        System.out.println("✅ Total pruebas exitosas: " + exitosas);
        System.out.println("❌ Total pruebas fallidas: " + fallidas);
        System.out.printf("⏳ Tiempo total de ejecución: %.2f min%n", tiempoTotal);
        System.out.println("======================================\n");
    }

    private static class TestResult {
        boolean exito;
        double duracion;

        TestResult(boolean exito, double duracion) {
            this.exito = exito;
            this.duracion = duracion;
        }
    }

    private static class OverallTestListener implements TestExecutionListener {
        private boolean allSuccessful = true;

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            if (testIdentifier.isTest()) {
                if (testExecutionResult.getStatus() != TestExecutionResult.Status.SUCCESSFUL) {
                    allSuccessful = false;
                }
            }
        }

        boolean areAllTestsSuccessful() {
            return allSuccessful;
        }
    }
}

