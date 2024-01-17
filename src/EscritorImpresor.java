import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class EscritorImpresor {
    public static String FRASE = "";
    private static final int TOKENS = 0;
    private static final int NUM_HILOS = 5;
    private static String a_Modo = "<NORMAL>";


    static class Escritor implements Runnable
    {
        private Buzon a_Buzon;


        public Escritor(Buzon p_Buzon)
        {
            this.a_Buzon = p_Buzon;
        }

        @Override
        public void run()
        {
            while (!a_Modo.equals("<STOP>")) {
                Scanner sc = new Scanner(System.in);
                System.out.println("Introduce una palabra (<STOP> para finalizar)");

                a_Buzon.a_Palabra = sc.next();
                esComando();
                if (!a_Buzon.a_Palabra.equals(a_Modo)) {   // No se añade si es un comando
                    a_Buzon.a_Frase += a_Buzon.a_Palabra + " ";
                }
                a_Buzon.a_Semaforo.release(1);
            }
        }   // run()

        public void esComando() {
            if (a_Buzon.a_Palabra.equals("<NORMAL>") || a_Buzon.a_Palabra.equals("<SUMAR>") || a_Buzon.a_Palabra.equals("<STOP>")) {
                a_Modo = a_Buzon.a_Palabra;
            }
        }   // esComando()
    }   // Escritor()


    static class Impresor implements Runnable
    {   // TODO problema más grande, no termina al escribir <STOP>
        private Buzon a_Buzon;

        public Impresor(Buzon p_Buzon)
        {
            this.a_Buzon = p_Buzon;
        }

        @Override
        public void run()
        {
            // COMENTARIO
            while (!a_Modo.equals("<STOP>"))
            {
                if (!a_Buzon.a_Palabra.equals(a_Modo)) {  // Solo seran iguales si se ha cambiado el modo este mismo turno
                    switch (a_Modo) {
                        case "<NORMAL>" -> {
                            System.out.println(a_Buzon.a_Palabra);
                        }
                        case "<SUMAR>" -> {
                            System.out.println(a_Buzon.a_Frase);
                        }
                        default -> {/*No hace nada*/}
                    }
                }

                try {
                    a_Buzon.a_Semaforo.acquire(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }   // run()
    }   // Impresor


    static class Buzon
    {
        private String a_Palabra = "";
        private String a_Frase = "";
        private Semaphore a_Semaforo = new Semaphore(TOKENS);
    }


    public static void main(String[] args)
    {
        ExecutorService l_Executor = (ExecutorService) Executors.newFixedThreadPool(NUM_HILOS);
        Buzon l_Buzon = new Buzon();

        Escritor l_Tarea1 = new Escritor(l_Buzon);
        Impresor l_Tarea2 = new Impresor(l_Buzon);

        l_Executor.submit(l_Tarea1);
        l_Executor.submit(l_Tarea2);
    }   // main()
}