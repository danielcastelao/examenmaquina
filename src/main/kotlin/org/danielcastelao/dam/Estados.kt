package org.danielcastelao.dam

/** Valores posibles de `nivel_actual`. */
enum class Niveles(val valor: Int) {
    /** Avenida G. Barbón */
    NIVEL_0(0),
    /** Plaza de Compostela */
    NIVEL_1(1),
    /** En movimiento, sin estar en ningún nivel */
    MOVIENDO(2),
    /** Fallo del mecanismo */
    ERROR(-1)
}

/** Botonera interior del ascensor. */
enum class Botones {
    nivel_0,
    nivel_1,
    /** Sin pulsación */
    ninguno
}

/** Estados de la máquina; cada uno define su acción de entrada. */
sealed class Estados {
    /** Se ejecuta al entrar en el estado: deja `nivel_actual` coherente. */
    abstract fun onEnter()

    /** Reposo en nivel 0 o 1 (si no está en ninguno, asume nivel 0). */
    object Idle : Estados() {
        override fun onEnter() {
            // sólo corrige el nivel si venía de un valor que no es de reposo
            if (nivel_actual != Niveles.NIVEL_0.valor && nivel_actual != Niveles.NIVEL_1.valor) {
                nivel_actual = Niveles.NIVEL_0.valor
            }
        }
    }

    /** Subiendo: no está en ningún nivel (2). */
    object MovingUP : Estados() {
        override fun onEnter() {
            nivel_actual = Niveles.MOVIENDO.valor
        }
    }

    /** Bajando: no está en ningún nivel (2). */
    object MovingDOWN : Estados() {
        override fun onEnter() {
            nivel_actual = Niveles.MOVIENDO.valor
        }
    }

    /** Fallo: nivel -1. */
    object Error : Estados() {
        override fun onEnter() {
            nivel_actual = Niveles.ERROR.valor
        }
    }
}
