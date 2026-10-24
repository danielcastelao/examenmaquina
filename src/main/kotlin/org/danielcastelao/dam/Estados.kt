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

/** Tipos de alarma: el valor es el código guardado en `alarma`. */
enum class Alarmas(val valor: Int, val mensaje: String) {
    MECANICO(1, "Error 1: fallo mecanico"),
    SOFTWARE(2, "Error 2: fallo software"),
    NO_IDENTIFICADO(3, "Error 3: no identificado");

    companion object {
        /** Cualquier código sin tipo conocido se trata como no identificado. */
        fun desde(codigo: Int): Alarmas = entries.find { it.valor == codigo } ?: NO_IDENTIFICADO
    }
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

        /** Muestra por consola el mensaje de la alarma activa. */
        fun mostrarMensaje() {
            println(Alarmas.desde(alarma).mensaje)
        }
    }
}
