package ch.heig.sio.lab2.groupC;

import java.util.Arrays;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;

/**
 * Heuristique LF (Largest First) de Welsh et Powell.
 * <p>
 * Les sommets sont traités par ordre décroissant de degré (sans départage particulier des
 * égalités) et chacun reçoit la plus petite couleur non utilisée par ses voisins déjà
 * coloriés.
 * <p>
 * Complexités : O(n + m) en temps et en mémoire. Le tri par degré est un tri par comptage
 * en O(n + Δ), avec Δ &lt; n, et la coloration parcourt chaque liste d'adjacence une fois.
 */
public final class LargestFirst implements ColoringAlgorithm {

  /**
   * Colore le graphe avec l'heuristique LF.
   *
   * @param graph le graphe à colorer
   * @return une coloration propre et complète du graphe, dont les couleurs sont les entiers
   *         consécutifs à partir de 0
   */
  @Override
  public Coloring color(Graph graph) {
    int n = graph.vertexCount();

    // Étape 1 : tri des sommets par degré décroissant (tri par comptage, O(n + maxDegree)).
    // Le degré d occupe la case maxDegree - d, ce qui place les plus grands degrés en premier.
    int maxDegree = graph.maxDegree();
    int[] bucketStart = new int[maxDegree + 2];
    for (int v = 0; v < n; v++) {
      // Comptage : nombre de sommets par case (décalé d'un cran pour le cumul ci-dessous).
      bucketStart[maxDegree - graph.degree(v) + 1]++;
    }
    for (int i = 1; i < bucketStart.length; i++) {
      // Sommes cumulées : bucketStart[k] devient l'indice où débute la case k dans 'order'.
      bucketStart[i] += bucketStart[i - 1];
    }
    int[] order = new int[n];
    for (int v = 0; v < n; v++) {
      // Placement stable de chaque sommet dans sa case, puis avancée du curseur.
      order[bucketStart[maxDegree - graph.degree(v)]++] = v;
    }

    // Étape 2 : coloration séquentielle dans l'ordre obtenu.
    int[] colors = new int[n];
    Arrays.fill(colors, Coloring.UNCOLORED);

    // forbiddenFor[c] = dernier sommet pour lequel la couleur c est interdite (portée par
    // un voisin). Évite de remettre le tableau à zéro pour chaque sommet.
    // Un sommet de degré d reçoit une couleur au plus égale à d <= maxDegree : un tableau de
    // taille maxDegree + 1 suffit et la recherche ci-dessous termine toujours.
    int[] forbiddenFor = new int[maxDegree + 1];
    Arrays.fill(forbiddenFor, -1);

    for (int vertex : order) {
      // Marque les couleurs des voisins déjà coloriés comme interdites pour ce sommet.
      for (int i = 0; i < graph.degree(vertex); i++) {
        int neighborColor = colors[graph.neighbor(vertex, i)];
        if (neighborColor != Coloring.UNCOLORED) {
          forbiddenFor[neighborColor] = vertex;
        }
      }
      // Plus petite couleur non interdite.
      int color = 0;
      while (forbiddenFor[color] == vertex) {
        color++;
      }
      colors[vertex] = color;
    }

    return new Coloring(colors);
  }

  /**
   * @return le nom court de l'heuristique, utilisé pour l'affichage des résultats
   */
  @Override
  public String name() {
    return "LF";
  }
}
