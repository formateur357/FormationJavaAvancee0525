# 🧪 Exercice pratique – Système de gestion de comptes bancaires avec RMI

## Objectif

Mettre en œuvre une application distribuée en Java utilisant **RMI** pour simuler une banque. Les clients peuvent :

- Créer un compte
- Faire un dépôt ou un retrait
- Consulter leur solde

---

## 💡 Enoncé

Vous devez développer deux parties :

1. Un **serveur RMI** qui expose des opérations bancaires.
2. Un **client RMI** capable de se connecter à ce service et d’interagir avec.

---

## ✅ Contraintes techniques

- Utilisez **Java 11 ou supérieur**
- Ne pas utiliser `SecurityManager` (obsolète)
- Pas de `codebase` dynamique : toutes les classes partagées doivent être compilées ensemble
- Utiliser le registre RMI standard (`LocateRegistry.createRegistry(1099)`)

---

## 🏗️ Étapes à réaliser

### 1. Définir l’interface distante

Nom : `BanqueService`

Elle doit contenir :

- `void creerCompte(String numero)`  
- `void depot(String numero, double montant)`  
- `void retrait(String numero, double montant)`  
- `double getSolde(String numero)`  

Chaque méthode doit lever `RemoteException`.

---

### 2. Implémenter l’interface

Créer une classe `BanqueServiceImpl` qui :

- Gère les comptes dans une `Map<String, Double>`
- Lance une exception personnalisée `CompteInexistantException` si un compte n'existe pas

---

### 3. Définir l’exception distante

Créer la classe `CompteInexistantException` héritant de `RemoteException`.

---

### 4. Lancer le serveur RMI

Classe : `ServeurBanqueRMI`

- Crée un objet `BanqueServiceImpl`
- L’enregistre dans le registre RMI avec le nom `"BanqueService"`

---

### 5. Créer un client

Classe : `ClientBanqueRMI`

Le client doit :

- Se connecter au registre
- Appeler les méthodes suivantes :
  - Créer le compte `"C1001"`
  - Faire un dépôt de `150.0`
  - Retirer `30.0`
  - Afficher le solde

---

## 💻 Exemple d'exécution

[Client]
Création du compte : C1001
Dépôt de 150.0
Retrait de 30.0
Solde actuel : 120.0

---

## 📦 Arborescence attendue

banque/
├── BanqueService.java
├── BanqueServiceImpl.java
├── CompteInexistantException.java
├── ServeurBanqueRMI.java
└── ClientBanqueRMI.java

---

## 🔁 Bonus (facultatif)

- Ajouter la gestion de plusieurs clients en parallèle avec des threads
- Implémenter une méthode `virement(String from, String to, double montant)`
- Ajouter un contrôle pour empêcher les soldes négatifs