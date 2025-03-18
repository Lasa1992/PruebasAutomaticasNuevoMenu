package Suite;

import Bancos.ChequesModElim;
import Cobranza.PagoFacturaConcepto;
import Cobranza.PagoFacturaViaje;
import CuentasPorPagar.PagoPasivos;
import Facturacion.*;
import Indicadores.InicioSesion;
import Indicadores.ParametrosGenerales;
import Trafico.*;
import org.junit.jupiter.api.*;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.util.*;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SuiteGlobal {

    private static final String[] NAVEGADORES = {"chrome", "firefox", "edge"};
    private static final int NUMERO_HILOS = 3; // Asegurar que el número de hilos sea suficiente
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NUMERO_HILOS);

    private static final Map<String, Map<String, TestResult>> resultadosGlobales = new ConcurrentHashMap<>();

    @Test
    @Order(1)
    public void ejecutarPruebasEnTodosLosNavegadores() throws InterruptedException {
        List<Callable<Void>> tareasNavegador = new ArrayList<>();

        for (String navegador : NAVEGADORES) {
            tareasNavegador.add(() -> {
                ejecutarPruebasEnNavegador(navegador);
                return null;
            });
        }

        // Ejecutamos todas las tareas en paralelo
        executorService.invokeAll(tareasNavegador);
        executorService.shutdown(); // Cerramos los hilos después de todas las pruebas
        mostrarResumenGlobal();
    }

    private void ejecutarPruebasEnNavegador(String navegador) {
        System.out.println("\n======================================");
        System.out.println("🚀 Ejecutando pruebas en: " + navegador.toUpperCase());
        System.out.println("======================================");

        long inicioNavegador = System.nanoTime();

        Class<?>[] pruebas = {

                // Indicadores

                InicioSesion.class,
                ParametrosGenerales.class,

                // Bancos

                ChequesModElim.class,

                // Cobranza

                PagoFacturaConcepto.class,
                PagoFacturaViaje.class,

                // Cuentas por pagar

                PagoPasivos.class,

                // Contabilidad



                //Facturacion

                FacturacionGeneral.class,
                FacturacionGeneralDescImpr.class,
                FacturacionGeneralSustitucion.class,
                FacturaConceptoTimbrada.class,
                FacturacionConceptoSustitucion.class,
                FacturacionConceptoDescImpr.class,
                FacturacionListadoViajes.class,
                FacturacionViajeSustitucion.class,

                // Trafico

                CartaPorteComercioExterior.class,
                CartaPorteImpresionDescarga.class,
                CartaPorteSustitucion.class,
                CopiarCartaPorte.class,
                ViajeACartaPorte.class,
                LiquidacionFiscal.class,
                LiquidacionOperativa.class


        };

        Map<String, TestResult> resultadosPorPrueba = new ConcurrentHashMap<>();
        List<Callable<TestResult>> tareas = new ArrayList<>();

        for (Class<?> testClass : pruebas) {
            tareas.add(() -> ejecutarClaseJUnit(testClass, navegador));
        }

        try {
            List<Future<TestResult>> futuros = executorService.invokeAll(tareas);
            for (int i = 0; i < pruebas.length; i++) {
                TestResult resultado = futuros.get(i).get();
                resultadosPorPrueba.put(pruebas[i].getSimpleName(), resultado);
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR en ejecución en " + navegador.toUpperCase() + ": " + e.getMessage());
        }

        double duracionNavegador = (System.nanoTime() - inicioNavegador) / 1_000_000_000.0; // Convertir a segundos
        resultadosPorPrueba.put("TOTAL", new TestResult(true, duracionNavegador));
        resultadosGlobales.put(navegador, resultadosPorPrueba);
    }

    private TestResult ejecutarClaseJUnit(Class<?> testClass, String navegador) {
        long inicioPrueba = System.nanoTime();
        try {
            System.out.println("🚀 Iniciando prueba: " + testClass.getSimpleName() + " en " + navegador.toUpperCase());

            System.setProperty("navegador", navegador);

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();

            Launcher launcher = LauncherFactory.create();
            launcher.execute(request);

            InicioSesion.cerrarSesion();
            double duracionPrueba = (System.nanoTime() - inicioPrueba) / 1_000_000_000.0; // Convertir a segundos
            return new TestResult(true, duracionPrueba);
        } catch (Exception e) {
            System.err.println("⚠ ERROR en " + testClass.getSimpleName() + " en " + navegador.toUpperCase() + ": " + e.getMessage());
            double duracionPrueba = (System.nanoTime() - inicioPrueba) / 1_000_000_000.0; // Convertir a segundos
            return new TestResult(false, duracionPrueba);
        }
    }

    private void mostrarResumenGlobal() {
        System.out.println("\n======================================");
        System.out.println("📊 RESUMEN GLOBAL DE PRUEBAS");
        System.out.println("======================================");

        double tiempoTotal = 0.0;

        for (String navegador : resultadosGlobales.keySet()) {
            Map<String, TestResult> resultadosPorNavegador = resultadosGlobales.get(navegador);
            System.out.println("\n🌐 Navegador: " + navegador.toUpperCase());

            for (Map.Entry<String, TestResult> entry : resultadosPorNavegador.entrySet()) {
                if (entry.getKey().equals("TOTAL")) continue;

                TestResult result = entry.getValue();
                String estado = result.exito ? "✅ EXITOSA" : "❌ FALLIDA";
                System.out.printf("%s - %s (⏱ %.2f s)%n", estado, entry.getKey(), result.duracion);
            }

            double tiempoNavegador = resultadosPorNavegador.get("TOTAL").duracion;
            tiempoTotal += tiempoNavegador;
            System.out.printf("⏳ Tiempo total en %s: %.2f s%n", navegador.toUpperCase(), tiempoNavegador);
        }

        System.out.println("\n======================================");
        System.out.printf("⏳ Tiempo total de ejecución: %.2f s%n", tiempoTotal);
        System.out.println("======================================\n");
    }

    private static class TestResult {
        boolean exito;
        double duracion;

        public TestResult(boolean exito, double duracion) {
            this.exito = exito;
            this.duracion = duracion;
        }
    }
}
