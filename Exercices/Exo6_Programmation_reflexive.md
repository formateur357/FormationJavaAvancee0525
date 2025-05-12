# 🧪 Exercice de cas d’usage complet – Réflexion et Annotations

## 🎯 Objectif

Mettre en œuvre un système de gestion dynamique de tâches utilisant la **réflexion** et les **annotations** :

* **Tâches système** : Définir des méthodes annotées et les exécuter via réflexion.
* **Exécution dynamique** : Instancier et exécuter des tâches en fonction de leur priorité.

---

## 📘 Contexte

Vous êtes chargé de concevoir un **système de gestion de tâches** dans une application de gestion de projet. Ce système doit :

1. **Définir des tâches** : Utiliser des annotations pour marquer des méthodes représentant des tâches dans des classes spécifiques.
2. **Prioriser les tâches** : Permettre de trier les tâches par priorité et les exécuter dans l’ordre via la réflexion.
3. **Exécution dynamique** : Instancier des objets de classe dynamiquement et exécuter les méthodes annotées en fonction des priorités.

---

## 📋 Spécifications techniques

### 🧱 Architecture

* **Tâches à exécuter** : Différentes tâches sont définies dans des classes marquées par des annotations.
* **Gestion des priorités** : Les tâches doivent être triées par priorité avant d’être exécutées.
* **Exécution dynamique** : Les méthodes annotées seront exécutées dynamiquement à l’aide de la réflexion.

### 📦 Classes à implémenter

1. **Annotation `@Tâche`** :

   * Cette annotation doit inclure des attributs comme `priorité` (int) et `description` (String).
   * Utilisez la rétention `RUNTIME` pour qu’elle soit accessible à l'exécution.

2. **Classe `TâchesSystème`** :

   * Définir plusieurs méthodes comme `réinitialiserBaseDeDonnées()` et `nettoyerCache()` annotées avec `@Tâche`.
   * Chaque méthode doit avoir une priorité et une description.

3. **Classe `GestionnaireTâches`** :

   * Utiliser la réflexion pour scanner les classes et trouver les méthodes annotées.
   * Trier les tâches par priorité (en utilisant un `Comparator`).
   * Exécuter les tâches dans l’ordre de priorité, afficher des logs de chaque tâche exécutée.

4. **Classe `Main`** :

   * Instancier des objets de classe de manière dynamique via réflexion.
   * Utiliser le `GestionnaireTâches` pour exécuter les tâches.

---

## 💡 Aide / Structure recommandée

### Exemple de structure de projet

```text
gestion-taches/
├── src/
│ ├── modele/Tache.java
│ ├── modele/TachesSysteme.java
│ ├── gestionnaire/GestionnaireTaches.java
│ ├── principal/Main.java
```

### Exemple de lancement

1. Lancer l'application en appelant la méthode Main.main().

2. Le GestionnaireTâches doit automatiquement découvrir et exécuter les tâches annotées, affichant les messages avec la priorité et la description.

## ✅ Critères de validation

- L’annotation @Tâche est bien utilisée et permet de marquer les méthodes.

- Les tâches sont triées par priorité et exécutées dans le bon ordre.

- L’exécution dynamique des tâches fonctionne correctement via la réflexion.

- Le système affiche les messages d’exécution des tâches avec leur priorité et description.

## 🏁 Bonus (facultatif)

- Implémenter une gestion des exceptions pour capturer et afficher les erreurs d’exécution des tâches.

- Ajouter des paramètres dynamiques aux méthodes annotées et les passer via la réflexion.

- Créer un système de gestion de tâches récurrentes en utilisant un ScheduledExecutorService.