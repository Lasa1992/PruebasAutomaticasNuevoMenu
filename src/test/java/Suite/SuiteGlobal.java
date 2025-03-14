package Suite;

import Bancos.Cheques;
import Bancos.ChequesModElim;
import Bancos.MovimientoBancario;
import Bancos.MovimientoBancarioModElim;
import Cobranza.PagoFacturaConcepto;
import Cobranza.PagoFacturaViaje;
import CuentasPorPagar.PagoPasivos;
import CuentasPorPagar.PasivoManual;
import Facturacion.*;
import Indicadores.IndicadoresTest;
import Indicadores.ParametrosGenerales;
import Trafico.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SuiteGlobal {

    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_GREEN  = "\u001B[32m";
    private static final String ANSI_RED    = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private final List<String> pruebasExitosas = new ArrayList<>();
    private final List<String> pruebasFallidas = new ArrayList<>();

    @Test
    @Order(1)
    public void ejecutarPruebasEnCadena() {
        System.out.println("======================================");
        System.out.println("       INICIANDO SUITE DE PRUEBAS     ");
        System.out.println("======================================");

        Class<?>[] pruebas = {
                // GENERALES
                ParametrosGenerales.class,
                IndicadoresTest.class,

                // TRAFICO
                //CreacionViajeTimbreTraslado.class,
                CartaPorteSustitucion.class,
                CartaPorteComercioExterior.class,
                CartaPorteImpresionDescarga.class,
                CopiarCartaPorte.class,
                LiquidacionFiscal.class,
                LiquidacionOperativa.class,

                // FACTURACION
                FacturacionListadoViajes.class,
                //FacturacionViajeComplemento.class,
                FacturacionViajeSustitucion.class,
                //FacturaConceptoTimbrada.class,
                FacturacionConceptoSustitucion.class,
                FacturacionConceptoDescImpr.class,
                //FacturacionGeneral.class,
                FacturacionGeneralSustitucion.class,
                FacturacionConceptoDescImpr.class,

                // COBRANZA
                PagoFacturaConcepto.class,
                PagoFacturaViaje.class,

                //Cuentas por pagar
                //PasivoManual.class,
                PagoPasivos.class,


                //Bancos

                //MovimientoBancario.class,
                MovimientoBancarioModElim.class,
                //Cheques.class,
                ChequesModElim.class
        };

        for (Class<?> testClass : pruebas) {
            try {
                System.out.println("\n" + ANSI_YELLOW
                        + "=== EJECUTANDO CLASE: " + testClass.getSimpleName()
                        + ANSI_RESET);

                boolean resultado = ejecutarClaseJUnit(testClass);

                if (resultado) {
                    pruebasExitosas.add(testClass.getSimpleName());
                    System.out.println(ANSI_GREEN
                            + "=== FIN: " + testClass.getSimpleName() + " => ÉXITO"
                            + ANSI_RESET);
                } else {
                    pruebasFallidas.add(testClass.getSimpleName());
                    System.err.println(ANSI_RED
                            + "=== FIN: " + testClass.getSimpleName() + " => FALLÓ"
                            + ANSI_RESET);
                }

            } catch (Exception e) {
                pruebasFallidas.add(testClass.getSimpleName());
                System.err.println(ANSI_RED
                        + "❌ ERROR en " + testClass.getSimpleName() + ": " + e.getMessage()
                        + ANSI_RESET);
            }

            esperarSiguientePrueba();
        }

        mostrarResumenFinal();
    }

    private boolean ejecutarClaseJUnit(Class<?> testClass) {
        try {
            Launcher launcher = LauncherFactory.create();
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();

            launcher.execute(request);
            TestExecutionSummary summary = listener.getSummary();

            if (summary.getFailures().isEmpty()) {
                return true;
            } else {
                summary.getFailures().forEach(failure -> {
                    System.err.println("   ❌ "
                            + failure.getTestIdentifier().getDisplayName()
                            + " - " + failure.getException().getMessage());
                });
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void esperarSiguientePrueba() {
        try {
            System.out.println(ANSI_YELLOW
                    + "⏳ Esperando 3 segundos antes de la siguiente clase de pruebas..."
                    + ANSI_RESET);
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            System.err.println("⚠ Error en la espera entre pruebas: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void mostrarResumenFinal() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("       RESUMEN FINAL DE PRUEBAS       ");
        System.out.println("======================================");

        if (!pruebasExitosas.isEmpty()) {
            System.out.println(ANSI_GREEN + "✅ PRUEBAS EXITOSAS (" + pruebasExitosas.size() + "):" + ANSI_RESET);
            pruebasExitosas.forEach(prueba -> System.out.println("   - " + ANSI_GREEN + prueba + ANSI_RESET));
        } else {
            System.out.println(ANSI_RED + "❌ NO HUBO PRUEBAS EXITOSAS" + ANSI_RESET);
        }

        if (!pruebasFallidas.isEmpty()) {
            System.out.println(ANSI_RED + "\n❌ PRUEBAS FALLIDAS (" + pruebasFallidas.size() + "):" + ANSI_RESET);
            pruebasFallidas.forEach(prueba -> System.out.println("   - " + ANSI_RED + prueba + ANSI_RESET));
        } else {
            System.out.println(ANSI_GREEN + "\n✅ TODAS LAS PRUEBAS PASARON EXITOSAMENTE" + ANSI_RESET);
        }

        System.out.println("\n======================================");
        System.out.println("TOTAL EJECUTADAS: " + (pruebasExitosas.size() + pruebasFallidas.size()));
        System.out.println("✅ EXITOSAS: " + pruebasExitosas.size());
        System.out.println("❌ FALLIDAS: " + pruebasFallidas.size());
        System.out.println("======================================");
    }
}