package org.danielcastelao.dam

// nivel en el que se encuentra el ascensor
var nivel_actual: Int = Niveles.NIVEL_0.valor
// estado actual del ascensor
var estado_acual: Estados = Estados.Idle
// último botón pulsado en la botonera interior
var boton: Botones = Botones.ninguno
// código de alarma: 0 = sin alarma, 1 = mecánico, 2 = software, 3 = no identificado
var alarma: Int = 0
// indica que el mecanismo ha terminado el trayecto
var llegada: Boolean = false

/**
 * Gestiona el cambio de estados
 */
fun gestor(): Int {
    val anterior = estado_acual
    // Una alarma (> 0) lleva a Error desde cualquier estado
    estado_acual = if (alarma > 0) {
        Estados.Error
    } else {
        when (estado_acual) {
            // Reposo: sólo se mueve si se pulsa el botón del otro nivel
            is Estados.Idle -> {
                // el botón se consume al leerlo
                val pulsado = boton
                boton = Botones.ninguno
                when {
                    pulsado == Botones.nivel_1 && nivel_actual == Niveles.NIVEL_0.valor -> Estados.MovingUP
                    pulsado == Botones.nivel_0 && nivel_actual == Niveles.NIVEL_1.valor -> Estados.MovingDOWN
                    else -> Estados.Idle
                }
            }
            // Subiendo: al llegar queda en reposo en el nivel 1
            is Estados.MovingUP -> {
                if (llegada) {
                    llegada = false
                    nivel_actual = Niveles.NIVEL_1.valor
                    Estados.Idle
                } else Estados.MovingUP
            }
            // Bajando: al llegar queda en reposo en el nivel 0
            is Estados.MovingDOWN -> {
                if (llegada) {
                    llegada = false
                    nivel_actual = Niveles.NIVEL_0.valor
                    Estados.Idle
                } else Estados.MovingDOWN
            }
            // Error: no se sale de este estado
            is Estados.Error -> Estados.Error
        }
    }
    // El mensaje se muestra sólo al entrar en Error, no en cada llamada
    if (estado_acual is Estados.Error && anterior !is Estados.Error) {
        Estados.Error.mostrarMensaje()
    }
    // Acción común de entrada: deja nivel_actual coherente con el estado
    estado_acual.onEnter()
    return nivel_actual
}
