package ch.heig.sio.lab2.groupC;

import java.util.Arrays;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;

public class Dsatur implements ColoringAlgorithm {
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
        int[] dsat = new int[n];
        int[] degree = new int[n];
        for (int v = 0; v < n; v++) {
            degree[v] = graph.degree(v);
        }
        // estVoisinColorie[v][c] : v possède (au moins) un voisin colorié avec la couleur c.
        // Java initialise le tableau à false, ce qui correspond aux lignes 7 et 8 du pseudo-code.
        // Les couleurs sont inférieures à n, donc n colonnes suffisent.
        boolean[][] estVoisinColorie = new boolean[n][n];
        int nbColories = 0;
 
        // Phase 2 : coloration du premier sommet (plus grand degré, plus petit indice si égalité)
        int first = 0;
        for (int v = 1; v < n; v++) {
            if (degree[v] > degree[first]) {
                first = v;
            }
        }
        colors[first] = 0;
        nbColories++;
        for (int i = 0; i < degree[first]; i++) {
            int w = graph.neighbor(first, i);
            estVoisinColorie[w][0] = true;
            dsat[w]++;
        }
 
        // Phase 3 : boucle principale, n - 1 itérations
        while (nbColories < n) {
            // 3.a : sélection du sommet non colorié de saturation max, puis de degré max
            // Les comparaisons strictes départagent une double égalité par le plus petit numéro.
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (colors[v] == Coloring.UNCOLORED
                        && (u == -1
                        || dsat[v] > dsat[u]
                        || (dsat[v] == dsat[u] && degree[v] > degree[u]))) {
                    u = v;
                }
            }
 
            // 3.b : plus petite couleur disponible, au plus degree[u] couleurs sont interdites,
            // donc la recherche s'arrête avant n.
            int c = 0;
            while (estVoisinColorie[u][c]) {
                c++;
            }
            colors[u] = c;
            nbColories++;
 
            // 3.c : mise à jour de la saturation des voisins non coloriés, elle n'augmente que si
            // la couleur c est nouvelle pour ce voisin.
            for (int i = 0; i < degree[u]; i++) {
                int w = graph.neighbor(u, i);
                if (colors[w] == Coloring.UNCOLORED && !estVoisinColorie[w][c]) {
                    estVoisinColorie[w][c] = true;
                    dsat[w]++;
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
