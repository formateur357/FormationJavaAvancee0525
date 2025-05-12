# 🧪 Exercice de cas d’usage complet – JMS

## 🎯 Objectif

Mettre en œuvre les deux modes de communication de JMS :

- **Point-à-point (PTP)** : pour le traitement unitaire des commandes clients.
- **Publish/Subscribe (Pub/Sub)** : pour la diffusion d’alertes aux superviseurs.

---

## 📘 Contexte

Vous êtes chargé de concevoir un **système de gestion d’un entrepôt** connecté à une boutique e-commerce. Ce système doit :

1. Recevoir des **commandes clients** (via JMS en mode **point-à-point**).
2. Déclencher une **alerte stock faible** quand le stock d’un produit descend sous un seuil critique (via JMS en mode **publish/subscribe**).
3. Afficher ces alertes sur les **interfaces des superviseurs** (abonnés au topic).

---

## 📋 Spécifications techniques

### 🧱 Architecture

- **Producer 1** : génère et envoie des commandes sur une `Queue` nommée `COMMANDES`.
- **Consumer 1** : traite les commandes et met à jour le stock.
- **Producer 2** : envoie une alerte sur un `Topic` nommé `ALERTES_STOCK` si le stock d’un produit est inférieur à 5 unités.
- **Consumers 2+** : superviseurs connectés à `ALERTES_STOCK` reçoivent les alertes.

### 📦 Classes à implémenter

1. `Commande` : une classe sérialisable contenant :
   - `id` (String), `produit` (String), `quantite` (int)

2. `ProducteurCommande` :
   - Envoie aléatoirement des commandes dans la queue `COMMANDES`.

3. `ConsommateurCommande` :
   - Consomme les commandes depuis la queue.
   - Met à jour un stock local simulé (ex: `Map<String, Integer>`).
   - Envoie une alerte dans le topic `ALERTES_STOCK` si stock < 5.

4. `Superviseur` :
   - S’abonne au topic `ALERTES_STOCK`.
   - Affiche toute alerte reçue.

---

## 💡 Aide / Structure recommandée

### Exemple de structure de projet

```text
entrepot-jms/
├── src/
│ ├── modele/Commande.java
│ ├── producteur/ProducteurCommande.java
│ ├── consommateur/ConsommateurCommande.java
│ ├── superviseur/Superviseur.java
```


### Exemple de lancement

- Démarrer ActiveMQ ou Artemis localement.
- Lancer `ProducteurCommande` dans un terminal.
- Lancer `ConsommateurCommande` dans un autre.
- Lancer un ou plusieurs `Superviseur` pour visualiser les alertes.

---

## ✅ Critères de validation

- [ ] Les commandes sont envoyées et traitées via une Queue JMS.
- [ ] Le stock est correctement mis à jour.
- [ ] Des alertes sont bien publiées sur un Topic si seuil atteint.
- [ ] Tous les superviseurs abonnés reçoivent les messages d’alerte.

---

## 🏁 Bonus (facultatif)

- Mettre en place des **abonnements durables** pour les superviseurs.
- Persister les commandes et l’historique des alertes dans un fichier texte ou base de données.
- Utiliser un `ScheduledExecutorService` pour simuler un flux régulier de commandes.