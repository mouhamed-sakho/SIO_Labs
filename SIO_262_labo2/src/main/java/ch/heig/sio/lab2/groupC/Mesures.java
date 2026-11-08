package ch.heig.sio.lab2.groupC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;
import ch.heig.sio.lab2.graph.GraphReader;

public final class Mesures {

    /** Nombre d'exécutions d'échauffement, non comptabilisées. */
    static final int WARMUP = 3;

    /** Nombre d'exécutions mesurées, dont la moyenne est reportée. */
    static final int REPS = 10;

    private Mesures() {
    }

    /**
     * Point d'entrée.
     *
     * @param args {@code args[0]} (optionnel) : répertoire contenant les fichiers {@code .txt}
     *             des graphes ; {@code data} par défaut
     * @throws IOException si le répertoire ne peut pas être parcouru
     */
    public static void main(String[] args) throws IOException {
        Path dataDir = Path.of(args.length > 0 ? args[0] : "data");
        List<ColoringAlgorithm> algos = List.of(new LargestFirst(), new Dsatur());

        List<Path> files;
        try (Stream<Path> s = Files.list(dataDir)) {
            files = s.filter(p -> p.toString().endsWith(".txt")).sorted().toList();
        }

        System.out.println("instance,n,m,maxDeg,density,colorsLF,colorsDSATUR,usLF,usDSATUR,warmup,reps,valid");
        for (Path file : files) {
            Graph g;
            try {
                g = GraphReader.read(file);
            } catch (IOException e) {
                // Fichier mal formé : on le signale et on passe au suivant.
                System.err.println(file.getFileName() + " : " + e.getMessage());
                continue;
            }
            int n = g.vertexCount();
            long m = g.edgeCount();
            double density = n >= 2 ? 2.0 * m / ((double) n * (n - 1)) : 0;

            int[] colors = new int[2];
            double[] micros = new double[2];
            boolean valid = true;
            for (int a = 0; a < 2; a++) {
                ColoringAlgorithm algo = algos.get(a);
                Coloring c = algo.color(g);
                for (int i = 0; i < WARMUP; i++) {
                    c = algo.color(g);
                }
                long total = 0;
                for (int i = 0; i < REPS; i++) {
                    long t0 = System.nanoTime();
                    c = algo.color(g);
                    total += System.nanoTime() - t0;
                }
                micros[a] = total / (double) REPS / 1_000.0;
                colors[a] = c.colorCount();
                valid &= c.isProperFor(g);   // vérification hors chronomètre
            }

            String name = file.getFileName().toString().replaceAll("\\.txt$", "");
            System.out.println(String.format(Locale.ROOT, "%s,%d,%d,%d,%.6f,%d,%d,%.1f,%.1f,%d,%d,%s",
                    name, n, m, g.maxDegree(), density, colors[0], colors[1],
                    micros[0], micros[1], WARMUP, REPS, valid));
            System.out.flush();
        }
    }
}