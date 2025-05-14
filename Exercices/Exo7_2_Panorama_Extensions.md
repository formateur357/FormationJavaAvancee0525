## 🧠 Exercice – Partie 2 : Traitement fonctionnel & concurrence avancée

### 🎯 Objectifs

1. Classer les événements (`Evenement`) par niveau de gravité via un `switch` avec `pattern matching`.
2. Écrire dans des fichiers (`normal.txt`, `suspect.txt`, `critique.txt`) la liste des événements, chacun par catégorie.
3. Générer un `rapport.txt` contenant :
   - Le **nombre d’événements** par gravité.
   - La **moyenne des gravités** des événements critiques.
   - Le **pourcentage** d'événements critiques.
   - La **liste des descriptions** des événements `suspects` en majuscules, triées par ordre alphabétique.
4. Utiliser :
   - `filter`, `map`, `collect`, `groupingBy`, `averagingInt`, etc.
   - `Virtual Threads` pour exécuter l’écriture des fichiers.
   - `StructuredTaskScope.ShutdownOnFailure` pour fiabiliser l’écriture concurrente.
   - `switch` avec `when` pour classifier les événements.

---

### ✅ Tâches détaillées

#### 1. Prétraitement fonctionnel

- Utiliser `Stream<Evenement>` pour :
  - **Filtrer** les événements par catégorie (`filter` + `switch`).
  - **Transformer** (`map`) certaines informations.
  - **Grouper** (`collect(groupingBy())`) les événements par type.
  - Extraire certaines statistiques (`averagingInt`, `counting`, `summarizingInt`).

#### 2. Classification via `switch`

```java
String typeGravite = switch (e.gravite()) {
    case int g when g >= 80 -> "critique";
    case int g when g >= 50 -> "suspect";
    default -> "normal";
};
```

#### 3. Écriture des fichiers (concurrente)

- Créer une tâche par fichier (3 fichiers + 1 rapport + 1 JSON optionnel).
- Lancer ces tâches en Virtual Threads via `StructuredTaskScope`.
- Annuler toutes les tâches en cas d’échec d’une seule.

#### 4. Fichier `rapport.txt`

- Contenu :
  - Nombre d’événements par type.
  - Moyenne de gravité des critiques.
  - % critiques parmi tous.
  - Descriptions des suspects, triées et en majuscules.

#### 5. Bonus : fichier `data.json`

Format simple :

```json
{
  "critique": [ { "gravite": 90, "description": "Erreur critique" }, ... ],
  "suspect": [ ... ],
  "normal": [ ... ]
}
```

---

### 🛠️ Notions revues

|---------------------------------------|-----------------------------------|
| Notion Java Avancé                    | Utilisation                       |
|---------------------------------------|-----------------------------------|
| `record`                              | Pour représenter les événements   |
| `switch` moderne                      | Classification des gravités       |
| `filter`, `map`, `collect`            | Prétraitement des données         |
| `groupingBy`, `averagingInt`          | Statistiques                      |
| `Virtual Threads`                     | Écriture concurrente              |
| `StructuredTaskScope`                 | Contrôle des écritures            |
| `Files.newBufferedWriter`             | Écriture de fichiers              |
| `String.join`, `Collectors.joining`   | Fusion de données textuelles      |
|---------------------------------------|-----------------------------------|