# 🧪 Exercice de cas d’usage complet – JMX

## 🎯 Objectif

Mettre en œuvre un **système de gestion d’entrepôt** avec **JMX** pour la gestion des stocks, permettant :

- La gestion des stocks via des **MBeans**.
- La surveillance du stock à distance avec **JConsole**.
- L’utilisation de **RMI** pour accéder au MBean à distance.

---

## 📘 Contexte

Vous êtes responsable de la création d'un **système de gestion d’entrepôt** dans lequel :

1. Le stock des produits est mis à jour par des **agents de commande**.
2. Un **agent de réapprovisionnement** met à jour les stocks toutes les 10 secondes.
3. Vous devez surveiller l’état du stock à distance à l’aide de **JMX** et de **JConsole**.

---

## 📋 Spécifications techniques

### 🧱 Architecture

- **StockManager** : expose un MBean pour gérer le stock des produits.
  - Méthodes : `getStock()`, `addProduct(String name, int quantity)`, `replenishStock(String name, int quantity)`.
  
- **AgentDeCommande** : une classe qui simule un agent de commande qui retire des produits du stock.
  - Méthode : `placeOrder(String product, int quantity)`.
  
- **AgentDeReapprovisionnement** : une classe qui réapprovisionne régulièrement le stock.
  - Méthode : `replenish()`.
  
- **JMX Server** : expose les MBeans pour la gestion du stock.
  - Utilise **RMI** pour permettre l’accès à distance via **JConsole**.
  
- **JConsole** : utilisé pour se connecter au serveur JMX et interagir avec les MBeans.

### 📦 Classes à implémenter

1. `StockManagerMBean` :
   - Interface qui expose les méthodes pour accéder et modifier le stock.

2. `StockManager` :
   - Classe qui implémente l’interface `StockManagerMBean` et gère l’état du stock (ajouter/retrait de produits).

3. `AgentDeCommande` :
   - Classe qui effectue des commandes sur le stock.
   - Utilise les méthodes de `StockManager` pour retirer des produits.

4. `AgentDeReapprovisionnement` :
   - Classe qui réapprovisionne le stock régulièrement.
   - Crée des produits aléatoires et les ajoute au stock.

5. `Main` :
   - Classe qui configure et démarre le serveur JMX, les agents de commande, et l’agent de réapprovisionnement.

### 🎯 Points clés de l’exercice

1. **Exposez un MBean** pour le **StockManager** permettant de vérifier et de mettre à jour le stock via **JMX**.
2. Utilisez **RMI** pour permettre à **JConsole** de se connecter au serveur et interagir avec votre MBean.
3. Implémentez un **réapprovisionnement automatique** via un thread.
4. **Surveillez** le stock et interagissez avec le MBean via **JConsole**.

---

## 💡 Aide / Structure recommandée

### Exemple de structure de projet

```text
entrepot-jmx/
├── src/
│ ├── jmx/StockManagerMBean.java
│ ├── jmx/StockManager.java
│ ├── agent/AgentDeCommande.java
│ ├── agent/AgentDeReapprovisionnement.java
│ ├── Main.java
```

### Exemple de lancement

1. Créez un serveur JMX pour exposer le StockManager via RMI.

2. Lancez l’AgentDeReapprovisionnement pour ajouter des produits au stock toutes les 10 secondes.

3. Lancez plusieurs agents de commande pour effectuer des commandes sur le stock.

4. Ouvrez JConsole, connectez-vous à votre serveur JMX via RMI, et surveillez/modifiez l’état du stock.

## ✅ Critères de validation

- Le stock est correctement exposé via un MBean.

- L’accès à distance via RMI fonctionne dans JConsole.

- Les produits sont réapprovisionnés automatiquement toutes les 10 secondes.

- L’état du stock peut être mis à jour à distance via JConsole.

## 🏁 Bonus (facultatif)

- Ajouter un mécanisme de sécurité pour sécuriser l’accès au MBean (authentification RMI).

- Ajouter une journalisation des actions effectuées via JMX.

- Ajouter des fonctionnalités de surveillance étendues (par exemple, alerter lorsque le stock est faible).