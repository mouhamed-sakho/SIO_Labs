SIMULATION ET OPTIMISATION
ISC - HEIG-VD
J.-F. Hêche, E. Rappos
Automne 2026
Travail pratique
Heuristique DSATUR
## Objectifs
Dans ce travail vous reviendrez sur l’heuristique de coloration séquentielle DSATUR (pour
« degré de saturation ») proposée par D. Brélaz 1 et présentée au cours. Vous développerez
un pseudocode détaillé de l’algorithme permettant une mise en œuvre eﬃcace et en ferez
une analyse complète et rigoureuse de sa complexité dans le pire des cas.
Travail à eﬀectuer et contraintes à respecter
Rappelons que l’heuristique DSATUR utilise la notion de degré de saturation d’un sommet, déﬁnie comme le nombre de couleurs diﬀérentes utilisées par les voisins déjà coloriés
du sommet. Une description précise mais non détaillée de la méthode est donnée en algorithme 1.
Vous devez rédiger un pseudocode détaillé et complet de l’heuristique DSATUR, en préci-
sant notamment les structures de données utilisées, de manière à obtenir une implémentation
dont les complexités temporelle et spatiale soient toutes deux en O(n2) dans le pire des cas.
Votre algorithme prendra en entrée un graphe simple et non orienté $G = (V, E)$ comptant
n sommets (n ≥1) et m arêtes (m ≥0) et retournera un tableau contenant la couleur
aﬀectée à chaque sommet, les couleurs étant représentées par des entiers consécutifs en
commençant à 0. Vous partirez du principe que le graphe G, dont les n sommets sont
numérotés de 0 à n $-1$, est stocké sous forme d’un tableau de listes d’adjacence.
Vous utiliserez uniquement des structures simples (tableaux, listes) à l’exclusion de structures complexes (tables de hachage, dictionnaires, tas indexés, . . .).
Vous eﬀectuerez également une analyse rigoureuse des complexités temporelle et spatiale
de votre pseudocode aﬁn de vériﬁer qu’elles sont bien toutes les deux en O(n2) dans le pire
des cas.
## Modalités et délais
- Le travail est à eﬀectuer par groupe de deux.
- Vous devez rendre un document au format pdf contenant votre pseudocode et l’analyse de
complexité associée. Vous rédigerez votre document en français, au format A4, en mode
portrait et en recto verso. Il devra être paginé et rédigé avec un corps de police de 11 ou
12 points. Vous prêterez une attention toute particulière à la clarté et à la précision de
vos développements ainsi qu’à la rigueur scientiﬁque de votre rédaction.
- Vous devez rendre votre travail sur Cyberlearn au plus tard le dimanche 11 octobre
2026 (avant minuit).
1. D. Brélaz, « New methods to color the vertices of a graph », Communications of the ACM, vol. 22,
n° 4, 1979, p. 251–256.
1

---

## Algorithme 1 — Heuristique DSATUR
Données :
Un graphe simple et non orienté $G = (V, E)$ comptant $n = |V|$ sommets
(numérotés de 0 à n $-1$) et $m = |E|$ arêtes, stocké dans un tableau de listes
d’adjacence.
Résultat :
Une coloration compatible des sommets de G (les couleurs correspondant à
des entiers consécutifs à partir de 0).
1: Procédure DSATUR(G)
2:
Initialiser la couleur des sommets à $-1$ (« pas de couleur »)
3:
Initialiser le degré de saturation des sommets à 0
4:
Déterminer le sommet u de plus grand degré et lui aﬀecter la couleur 0
- Départager arbitrairement en cas d’égalité
5:
Mettre à jour le degré de saturation des voisins de u
6:
Tant que tous les sommets ne sont pas coloriés faire
7:
Déterminer le sommet u, non colorié, de plus grand degré de saturation et départager en choisissant le sommet de plus grand degré initial en cas d’égalité
- Départager arbitrairement en cas de double égalité
8:
Colorier le sommet u avec la plus petite couleur disponible
9:
Mettre à jour le degré de saturation des voisins (non coloriés) de u
10:
Fin tant que
11:
Retourner la coloration calculée
12: Fin Procédure
2