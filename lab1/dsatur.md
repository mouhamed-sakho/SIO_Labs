# Heuristique DSATUR : pseudocode et analyse de complexité


## 1. Structures de données

Le graphe G = (V, E) est simple et non orienté. Il compte `nbr_sommets` ≥ 1 sommets numérotés de 0 à `nbr_sommets`−1 et `nbr_aretes` arêtes. Il est donné sous forme d'un tableau de listes d'adjacence `adj[0..nbr_sommets-1]`.

Dans les formules de complexité (sections 4 à 6), on note **n = `nbr_sommets`** et **m = `nbr_aretes`**, comme dans l'énoncé.

| Structure | Type et taille | Rôle |
|---|---|---|
| `couleur_sommet[0..nbr_sommets-1]` | tableau d'entiers | couleur du sommet, −1 s'il n'est pas colorié |
| `saturation[0..nbr_sommets-1]` | tableau d'entiers | nombre de couleurs différentes parmi les voisins déjà coloriés |
| `degre[0..nbr_sommets-1]` | tableau d'entiers | degré initial du sommet, calculé une fois |
| `couleur_voisine_presente[0..nbr_sommets-1][0..nbr_sommets-1]` | matrice de booléens `nbr_sommets` × `nbr_sommets` | `couleur_voisine_presente[sommet][couleur]` vaut vrai si **au moins un** voisin colorié de `sommet` a la couleur `couleur` (un booléen de présence, pas un compteur) |
| `nbr_sommets_colories` | entier | nombre de sommets coloriés |

### Paramètres et variables

| Nom | Type | Rôle |
|---|---|---|
| `nbr_sommets` | entier ≥ 1 | paramètre : nombre de sommets du graphe |
| `adj` | tableau de `nbr_sommets` listes | paramètre : `adj[sommet]` contient les voisins de `sommet` |
| `sommet` | entier dans `0..nbr_sommets-1` | indice de boucle : sommet parcouru (initialisation, puis recherche du sommet à colorier) |
| `voisin` | entier dans `0..nbr_sommets-1` | indice de boucle : voisin parcouru dans une liste `adj[...]` |
| `indice_couleur` | entier dans `0..nbr_sommets-1` | indice de boucle : colonne de la matrice, utilisé pour l'initialiser à faux |
| `sommet_choisi` | entier dans `-1..nbr_sommets-1` | sommet non colorié retenu à l'itération courante, −1 tant qu'aucun candidat n'a été trouvé |
| `couleur_attribuee` | entier dans `0..nbr_sommets-1` | plus petite couleur disponible pour `sommet_choisi`, donc sa couleur finale |

Les constantes `vrai`, `faux` et `−1` (« pas de couleur », « pas de sommet ») sont utilisées telles quelles dans le pseudocode.

Dans les explications (tableau des structures, sections 2 et 3), `couleur` désigne un indice de couleur quelconque. Ce n'est pas une variable du pseudocode : dans celui-ci, cet indice s'appelle `indice_couleur` ou `couleur_attribuee`.

La matrice `couleur_voisine_presente` permet de tester en O(1) si une couleur est déjà présente chez les voisins d'un sommet. Elle évite une table de hachage ou un ensemble, qui sont interdits.

La distinction « au moins un voisin » est essentielle. Si plusieurs voisins de `sommet` ont la même couleur, cette couleur ne compte qu'une fois dans la saturation. C'est pourquoi, pour un sommet et une couleur donnés, l'entrée de la matrice ne passe à vrai qu'une seule fois, et que `saturation[sommet]` n'augmente qu'à ce moment-là (lignes 43 à 45 du pseudocode).

## 2. Pseudocode

```text
Procédure DSATUR(nbr_sommets, adj)
  Données : nbr_sommets ≥ 1, adj[0..nbr_sommets-1] tableau de listes
            d'adjacence d'un graphe simple non orienté
  Résultat : couleur_sommet[0..nbr_sommets-1], coloration compatible,
             couleurs = entiers consécutifs depuis 0

   1:  // ---------- Initialisation ----------
   2:  Pour sommet ← 0 à nbr_sommets−1 faire
   3:      couleur_sommet[sommet] ← −1
   4:      saturation[sommet] ← 0
   5:      degre[sommet] ← 0
   6:      Pour chaque voisin ∈ adj[sommet] faire
   7:          degre[sommet] ← degre[sommet] + 1
   8:      Fin pour
   9:      Pour indice_couleur ← 0 à nbr_sommets−1 faire
  10:          couleur_voisine_presente[sommet][indice_couleur] ← faux
  11:      Fin pour
  12:  Fin pour
  13:  nbr_sommets_colories ← 0
  14:
  15:  // ---------- Boucle principale ----------
  16:  Tant que nbr_sommets_colories < nbr_sommets faire
  17:
  18:      // (a) Choix du sommet : non colorié, saturation maximale,
  19:      //     puis degré initial maximal, puis plus petit indice
  20:      sommet_choisi ← −1
  21:      Pour sommet ← 0 à nbr_sommets−1 faire
  22:          Si couleur_sommet[sommet] = −1 alors
  23:              Si sommet_choisi = −1
  24:                 ou saturation[sommet] > saturation[sommet_choisi]
  25:                 ou (saturation[sommet] = saturation[sommet_choisi]
  26:                     et degre[sommet] > degre[sommet_choisi]) alors
  27:                  sommet_choisi ← sommet
  28:              Fin si
  29:          Fin si
  30:      Fin pour
  31:
  32:      // (b) Plus petite couleur disponible pour sommet_choisi
  33:      couleur_attribuee ← 0
  34:      Tant que couleur_voisine_presente[sommet_choisi][couleur_attribuee] = vrai faire
  35:          couleur_attribuee ← couleur_attribuee + 1
  36:      Fin tant que
  37:      couleur_sommet[sommet_choisi] ← couleur_attribuee
  38:      nbr_sommets_colories ← nbr_sommets_colories + 1
  39:
  40:      // (c) Mise à jour de la saturation des voisins non coloriés
  41:      Pour chaque voisin ∈ adj[sommet_choisi] faire
  42:          Si couleur_sommet[voisin] = −1
  43:             et couleur_voisine_presente[voisin][couleur_attribuee] = faux alors
  44:              couleur_voisine_presente[voisin][couleur_attribuee] ← vrai
  45:              saturation[voisin] ← saturation[voisin] + 1
  46:          Fin si
  47:      Fin pour
  48:  Fin tant que
  49:
  50:  Retourner couleur_sommet
Fin Procédure
```

### Correspondance avec l'algorithme 1 de l'énoncé

- **Ligne 4 de l'algorithme 1 (premier sommet).** Au début, toutes les saturations valent 0. La première itération choisit donc, par départage sur le degré, un sommet de plus grand degré. La couleur obtenue est 0, puisque la matrice est vide. Le premier sommet n'a donc pas besoin d'un traitement séparé.
- **Ligne 7 (choix).** Le critère est la saturation maximale, puis le degré initial maximal. Les égalités restantes sont tranchées par le plus petit indice, grâce aux comparaisons strictes.
- **Ligne 8 (couleur).** La plus petite couleur `couleur_attribuee` telle que `couleur_voisine_presente[sommet_choisi][couleur_attribuee]` est faux.
- **Ligne 9 (mise à jour).** Seuls les voisins non coloriés sont mis à jour. La saturation d'un voisin augmente seulement si la couleur attribuée est nouvelle pour lui.

## 3. Justification de la correction

Les trois invariants suivants sont valables au début de chaque itération de la boucle principale.

1. **Matrice.** Pour tout sommet non colorié `sommet` et toute couleur `couleur`, `couleur_voisine_presente[sommet][couleur]` est vrai si et seulement si `sommet` a **au moins un** voisin colorié de couleur `couleur`.
2. **Saturation.** Pour tout sommet non colorié `sommet`, `saturation[sommet]` est égal au nombre de couleurs `couleur` telles que `couleur_voisine_presente[sommet][couleur]` est vrai. C'est bien le degré de saturation au sens de l'énoncé.
3. **Coloration propre.** Deux sommets adjacents coloriés n'ont jamais la même couleur.

Ils se maintiennent ainsi.

- Les invariants 1 et 2 sont vrais à l'initialisation, car aucun sommet n'est colorié. Lors de la mise à jour (c), chaque voisin non colorié de `sommet_choisi` voit son entrée `couleur_voisine_presente[voisin][couleur_attribuee]` passer à vrai. Sa saturation augmente seulement si cette entrée était fausse, c'est-à-dire si aucun autre voisin colorié n'avait déjà cette couleur. Les deux invariants restent donc vrais.
- L'invariant 3 découle de l'invariant 1. La couleur attribuée en (b) est absente des voisins coloriés de `sommet_choisi`.

**Absence de dépassement de la matrice.** Au moment de la recherche en (b), la ligne de `sommet_choisi` contient au plus `nbr_sommets`−1 entrées vraies, une par voisin colorié au maximum, pour `nbr_sommets` entrées au total. Elle contient donc au moins une entrée fausse. La boucle s'arrête à la première entrée fausse, donc `couleur_attribuee ≤ nbr_sommets-1`. L'indice reste dans `0..nbr_sommets-1` et la matrice de taille `nbr_sommets` × `nbr_sommets` suffit.

**Couleurs consécutives.** Une couleur `couleur > 0` n'est attribuée que si `couleur`−1 est présente chez un voisin colorié. Les couleurs utilisées forment donc `0, 1, …, nbr_couleurs−1`, où `nbr_couleurs` est le nombre de couleurs utilisées.

## 4. Analyse de la complexité temporelle

On note n = `nbr_sommets` et m = `nbr_aretes`. On utilise `Σ |adj[sommet]| = 2m` (lemme des poignées de mains, somme sur tous les sommets) et `m ≤ n(n−1)/2`, donc `m = O(n²)`.

### Initialisation (lignes 2 à 13)

- Boucle externe : n tours.
- Calcul du degré (lignes 6 à 8) : `|adj[sommet]|` tours pour chaque sommet. Au total, `Σ |adj[sommet]| = 2m`.
- Initialisation de la ligne de la matrice (lignes 9 à 11) : n tours en O(1) par sommet, soit n² au total.
- Reste de l'initialisation (affectations de la couleur et de la saturation) : O(n) au total.

Total de l'initialisation : **Θ(n² + m) = Θ(n²)**.

### Boucle principale (lignes 16 à 48)

Chaque itération colorie exactement un sommet. Il y a donc exactement n itérations, car `nbr_sommets_colories` augmente de 1 à chaque tour.

Coût d'une itération pour le sommet `sommet_choisi` :

- **(a) Choix** : parcours des n sommets, avec O(1) par sommet. Coût Θ(n).
- **(b) Couleur** : au plus `couleur_attribuee + 1 ≤ n` tests en O(1), d'après la borne de la section 3. Coût O(n).
- **(c) Mise à jour** : `|adj[sommet_choisi]|` tours, avec O(1) par voisin (deux tests et deux affectations). Coût O(|adj[sommet_choisi]|).

Chaque sommet est choisi exactement une fois. En sommant sur les n itérations :

```
T_boucle = Σ_sommet_choisi [ Θ(n) + O(n) + O(|adj[sommet_choisi]|) ]
         = n · O(n) + O(Σ |adj[sommet_choisi]|)
         = O(n²) + O(2m)
         = O(n² + m)
         = O(n²)
```

### Total

```
T(n, m) = Θ(n² + m) + O(n² + m) = O(n² + m) = O(n²)   car m ≤ n(n−1)/2
```

La complexité temporelle dans le pire des cas est donc **O(n²)**. Le terme n² est atteint dès l'initialisation de la matrice, donc la borne est aussi Ω(n²) pour cet algorithme et le coût est en fait Θ(n²).

## 5. Analyse de la complexité spatiale

| Élément | Espace |
|---|---|
| `couleur_sommet`, `saturation`, `degre` | 3 × n = Θ(n) |
| `couleur_voisine_presente` | n × n = Θ(n²) |
| variables scalaires (`sommet_choisi`, `couleur_attribuee`, `nbr_sommets_colories`, `sommet`, `voisin`, `indice_couleur`) | O(1) |
| **Espace auxiliaire** | **Θ(n²)** |
| Entrée `adj` (listes d'adjacence) | Θ(n + m) |

L'espace auxiliaire est Θ(n²), dominé par la matrice. L'entrée occupe Θ(n + m), avec `m ≤ n(n−1)/2`, donc O(n²). L'espace total reste donc en **O(n²)** dans le pire des cas.

## 6. Conclusion

L'implémentation ci-dessus utilise uniquement des tableaux et des listes. Dans le pire des cas, elle a une complexité temporelle en **O(n²)** et une complexité spatiale en **O(n²)**, comme demandé.

Deux choix de conception permettent cette borne :

- La matrice booléenne `couleur_voisine_presente` donne un test d'appartenance d'une couleur en O(1) et une mise à jour de la saturation en O(1) par arête. Les mises à jour coûtent O(m) au total.
- La recherche linéaire du sommet à colorier coûte O(n) par itération, soit O(n²) au total. Cela rend inutile un tas, qui est interdit.
