package ch.heig.sio.lab2.groupC;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;

public class LargestFirst implements ColoringAlgorithm {
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

        // Étape 1 : tri des sommets par degré décroissant (tri par comptage, O(n + maxDeg)).
        // Le degré d occupe la "case" maxDeg - d, ce qui place les plus grands degrés en premier.
        int maxDeg = graph.maxDegree();
        int[] start = new int[maxDeg + 2];
        for (int v = 0; v < n; v++) {
            // Comptage : nombre de sommets par case (décalé d'un cran pour le cumul ci-dessous).
            start[maxDeg - graph.degree(v) + 1]++;
        }
        for (int i = 1; i < start.length; i++) {
            // Sommes cumulées : start[k] devient l'indice où débute la case k dans 'order'.
            start[i] += start[i - 1];
        }
        int[] order = new int[n];
        for (int v = 0; v < n; v++) {
            // Placement stable de chaque sommet dans sa case, puis avancée du curseur.
            order[start[maxDeg - graph.degree(v)]++] = v;
        }

        // Étape 2 : coloration séquentielle dans l'ordre obtenu.

        int[] colors = new int[n];
        java.util.Arrays.fill(colors, Coloring.UNCOLORED);

        // lastSeen[c] = dernier sommet u pour lequel la couleur c a été vue chez un voisin.
        // La couleur choisie est au plus égale au degré du sommet, donc au plus maxDeg :
        // un tableau de taille maxDeg + 2 suffit et le test d'arrêt ci-dessous termine toujours.
        int[] lastSeen = new int[maxDeg + 2];
        java.util.Arrays.fill(lastSeen, -1);

        for (int u : order) {
            // Marque les couleurs des voisins déjà coloriés comme interdites pour u.
            for (int i = 0; i < graph.degree(u); i++) {
                int c = colors[graph.neighbor(u, i)];
                if (c != Coloring.UNCOLORED && c < lastSeen.length) {
                    lastSeen[c] = u;
                }
            }
            // Plus petite couleur non interdite.
            int c = 0;
            while (lastSeen[c] == u) {
                c++;
            }
            colors[u] = c;
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
