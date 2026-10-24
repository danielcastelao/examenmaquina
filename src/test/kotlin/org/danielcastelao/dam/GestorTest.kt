package org.danielcastelao.dam

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GestorTest {
    @AfterTest
    fun restaurarEstado() {
        estado_acual = Estados.Idle
        nivel_actual = 0
        boton = Botones.ninguno
        alarma = 0
        llegada = false
    }

    @Test
    fun `gestor reinicia el nivel cuando el estado es Idle`() {
        estado_acual = Estados.Idle
        nivel_actual = 5

        assertEquals(0, gestor())
        assertEquals(0, nivel_actual)
    }

    @Test
    fun `gestor establece el nivel cuando el estado es MovingUP`() {
        estado_acual = Estados.MovingUP
        nivel_actual = 5

        assertEquals(2, gestor())
        assertEquals(2, nivel_actual)
    }

    @Test
    fun `gestor establece el nivel cuando el estado es MovingDOWN`() {
        estado_acual = Estados.MovingDOWN
        nivel_actual = 5

        assertEquals(2, gestor())
        assertEquals(2, nivel_actual)
    }

    @Test
    fun `gestor establece el nivel de error cuando el estado es Error`() {
        estado_acual = Estados.Error
        nivel_actual = 5

        assertEquals(-1, gestor())
        assertEquals(-1, nivel_actual)
    }

    @Test
    fun `Idle en nivel 0 con nivel_1 sube y llega al nivel 1`() {
        boton = Botones.nivel_1

        assertEquals(2, gestor())
        assertEquals(Estados.MovingUP, estado_acual)
        assertEquals(2, gestor())
        llegada = true
        assertEquals(1, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `Idle en nivel 1 con nivel_0 baja y llega al nivel 0`() {
        nivel_actual = 1
        boton = Botones.nivel_0

        assertEquals(2, gestor())
        assertEquals(Estados.MovingDOWN, estado_acual)
        assertEquals(2, gestor())
        llegada = true
        assertEquals(0, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `pulsar el boton del nivel actual se ignora`() {
        boton = Botones.nivel_0

        assertEquals(0, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `una alarma mayor que cero lleva a Error desde cualquier estado`() {
        listOf(Estados.Idle, Estados.MovingUP, Estados.MovingDOWN).forEach { estado ->
            (1..3).forEach { codigo ->
                estado_acual = estado
                alarma = codigo

                assertEquals(-1, gestor())
                assertEquals(Estados.Error, estado_acual)
            }
        }
    }

    @Test
    fun `cada alarma muestra su mensaje una sola vez`() {
        val esperados = mapOf(
            1 to "Error 1: fallo mecanico",
            2 to "Error 2: fallo software",
            3 to "Error 3: no identificado",
            9 to "Error 3: no identificado"
        )
        esperados.forEach { (codigo, mensaje) ->
            estado_acual = Estados.Idle
            alarma = codigo
            val salida = ByteArrayOutputStream()
            val original = System.out
            System.setOut(PrintStream(salida))
            try {
                gestor()
                gestor()
            } finally {
                System.setOut(original)
            }

            assertEquals(mensaje, salida.toString().trim())
        }
    }

    @Test
    fun `Error se mantiene aunque desaparezca la alarma`() {
        alarma = 2
        gestor()
        alarma = 0

        assertEquals(-1, gestor())
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `Error permanece en Error`() {
        estado_acual = Estados.Error

        assertEquals(-1, gestor())
        assertEquals(Estados.Error, estado_acual)
    }
}
