package ch.heig.sio.lab2.groupC;

import java.util.Arrays;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;

/**
 * Heuristique DSATUR de Brélaz.
 * <p>
 * À chaque itération, on colore le sommet non colorié de plus grand degré de saturation
 * (nombre de couleurs différentes parmi ses voisins déjà coloriés). En cas d'égalité, on
 * prend le sommet de plus grand degré, puis celui de plus petit numéro. Le sommet reçoit la
 * plus petite couleur non utilisée par ses voisins.
 * <p>
 * Complexités : O(n²) en temps (sélection en O(n) à chacune des n itérations, mises à jour
 * en O(m) au total) et O(n²) en mémoire (matrice {@code neighborHasColor}).
 */
public final class Dsatur implements ColoringAlgorithm {

  /**
   * Colore le graphe avec l'heuristique DSATUR.
   *
   * @param graph le graphe à colorer
   * @return une coloration propre et complète du graphe, dont les couleurs sont les entiers
   *         consécutifs à partir de 0
   */
  @Override
  public Coloring color(Graph graph) {
    int n = graph.vertexCount();

    // Phase 1 : initialisation
    int[] colors = new int[n];
    Arrays.fill(colors, Coloring.UNCOLORED);
    int[] saturation = new int[n];
    int[] degree = new int[n];
    for (int v = 0; v < n; v++) {
      degree[v] = graph.degree(v);
    }
    // neighborHasColor[v][c] : v possède (au moins) un voisin colorié avec la couleur c.
    // Java initialise le tableau à false, ce qui correspond aux lignes 7 et 8 du pseudo-code.
    // Les couleurs sont inférieures à n, donc n colonnes suffisent.
    boolean[][] neighborHasColor = new boolean[n][n];
    int coloredCount = 0;

    // Phase 2 : coloration du premier sommet (plus grand degré, plus petit numéro si égalité)
    int first = 0;
    for (int v = 1; v < n; v++) {
      if (degree[v] > degree[first]) {
        first = v;
      }
    }
    colors[first] = 0;
    coloredCount++;
    for (int i = 0; i < degree[first]; i++) {
      int neighbor = graph.neighbor(first, i);
      neighborHasColor[neighbor][0] = true;
      saturation[neighbor]++;
    }

    // Phase 3 : boucle principale, n - 1 itérations
    while (coloredCount < n) {
      // 3.a : sélection du sommet non colorié de saturation max, puis de degré max.
      // Les comparaisons strictes départagent une double égalité par le plus petit numéro.
      int selected = -1;
      for (int v = 0; v < n; v++) {
        if (colors[v] == Coloring.UNCOLORED
            && (selected == -1
            || saturation[v] > saturation[selected]
            || (saturation[v] == saturation[selected] && degree[v] > degree[selected]))) {
          selected = v;
        }
      }

      // 3.b : plus petite couleur disponible. Au plus degree[selected] couleurs sont
      // interdites, donc la recherche s'arrête avant n.
      int color = 0;
      while (neighborHasColor[selected][color]) {
        color++;
      }
      colors[selected] = color;
      coloredCount++;

      // 3.c : mise à jour de la saturation des voisins non coloriés. Elle n'augmente que si
      // la couleur est nouvelle pour ce voisin.
      for (int i = 0; i < degree[selected]; i++) {
        int neighbor = graph.neighbor(selected, i);
        if (colors[neighbor] == Coloring.UNCOLORED && !neighborHasColor[neighbor][color]) {
          neighborHasColor[neighbor][color] = true;
          saturation[neighbor]++;
        }
      }
    }

    return new Coloring(colors);
  }

  /**
   * @return le nom court de l'heuristique, utilisé pour l'affichage des résultats
   */
  @Override
  public String name() {
    return "DSATUR";
  }
}
