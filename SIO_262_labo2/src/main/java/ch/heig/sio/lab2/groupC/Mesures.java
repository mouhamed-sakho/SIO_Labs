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

/**
 * Mesures comparatives de LF et DSATUR sur tous les graphes d'un répertoire.
 * <p>
 * Pour chaque graphe et chaque heuristique : {@link #WARMUP} exécutions d'échauffement non
 * comptabilisées, puis moyenne du temps de {@link #REPETITIONS} appels à
 * {@code color(graph)}. Seul cet appel est chronométré ; la lecture du fichier et la
 * vérification de la coloration sont faites hors chronomètre. Avant toute mesure, un
 * échauffement global sur le premier graphe laisse la JVM compiler le code, pour que le
 * premier graphe mesuré ne soit pas pénalisé.
 * <p>
 * Le résultat est écrit au format CSV sur la sortie standard.
 */
public final class Mesures {

  /** Nombre d'exécutions d'échauffement, non comptabilisées. */
  static final int WARMUP = 4;

  /** Nombre d'exécutions mesurées, dont la moyenne est reportée. */
  static final int REPETITIONS = 10;

  /** Nombre d'exécutions de l'échauffement global, sur le premier graphe lisible. */
  static final int GLOBAL_WARMUP = 20;

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
    List<ColoringAlgorithm> algorithms = List.of(new LargestFirst(), new Dsatur());

    List<Path> files;
    try (Stream<Path> stream = Files.list(dataDir)) {
      files = stream.filter(path -> path.toString().endsWith(".txt")).sorted().toList();
    }

    boolean globalWarmupDone = false;
    System.out.println("instance,n,m,maxDeg,density,colorsLF,colorsDSATUR,usLF,usDSATUR,warmup,reps,valid");
    for (Path file : files) {
      Graph graph;
      try {
        graph = GraphReader.read(file);
      } catch (IOException e) {
        // Fichier mal formé : on le signale et on passe au suivant.
        System.err.println(file.getFileName() + " : " + e.getMessage());
        continue;
      }

      if (!globalWarmupDone) {
        for (ColoringAlgorithm algorithm : algorithms) {
          for (int i = 0; i < GLOBAL_WARMUP; i++) {
            algorithm.color(graph);
          }
        }
        globalWarmupDone = true;
      }

      int n = graph.vertexCount();
      long m = graph.edgeCount();
      double density = n >= 2 ? 2.0 * m / ((double) n * (n - 1)) : 0;

      int[] colorCounts = new int[algorithms.size()];
      double[] averageMicros = new double[algorithms.size()];
      boolean valid = true;
      for (int index = 0; index < algorithms.size(); index++) {
        ColoringAlgorithm algorithm = algorithms.get(index);
        for (int i = 0; i < WARMUP; i++) {
          algorithm.color(graph);
        }
        Coloring coloring = null;
        long totalNanos = 0;
        for (int i = 0; i < REPETITIONS; i++) {
          long startNanos = System.nanoTime();
          coloring = algorithm.color(graph);
          totalNanos += System.nanoTime() - startNanos;
        }
        averageMicros[index] = totalNanos / (double) REPETITIONS / 1_000.0;
        colorCounts[index] = coloring.colorCount();
        valid &= coloring.isProperFor(graph);   // vérification hors chronomètre
      }

      String instance = file.getFileName().toString().replaceAll("\\.txt$", "");
      System.out.println(String.format(Locale.ROOT, "%s,%d,%d,%d,%.6f,%d,%d,%.1f,%.1f,%d,%d,%s",
          instance, n, m, graph.maxDegree(), density, colorCounts[0], colorCounts[1],
          averageMicros[0], averageMicros[1], WARMUP, REPETITIONS, valid));
      System.out.flush();
    }
  }
}
