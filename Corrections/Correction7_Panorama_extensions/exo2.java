import java.util.Scanner;

// Déclaration d'une interface fonctionnelle pour représenter une opération
@FunctionalInterface
interface Operation {
    // Méthode abstraite pour appliquer une opération sur deux entiers
    int appliquer(int a, int b);
}

public class Calculatrice {
    public static void main(String[] args) {
        // Déclaration des opérations via expressions lambda
        Operation addition = (a, b) -> a + b;               // Addition
        Operation soustraction = (a, b) -> a - b;             // Soustraction
        Operation multiplication = (a, b) -> a * b;           // Multiplication
        // Division avec vérification pour éviter la division par zéro
        Operation division = (a, b) -> {
            if (b == 0) {
                System.out.println("Erreur : division par zéro !");
                return 0;
            }
            return a / b;
        };

        // Création d'un Scanner pour lire l'entrée depuis la console
        Scanner scanner = new Scanner(System.in);

        // Demander le premier nombre à l'utilisateur
        System.out.print("Entrez le premier nombre: ");
        int num1 = scanner.nextInt();

        // Demander le deuxième nombre à l'utilisateur
        System.out.print("Entrez le deuxième nombre: ");
        int num2 = scanner.nextInt();

        // Demander à l'utilisateur de choisir une opération
        System.out.print("Choisissez l'opération (+, -, *, /): ");
        char op = scanner.next().charAt(0);

        int resultat = 0;  // Variable pour stocker le résultat de l'opération
        boolean valide = true;  // Indique si l'opération sélectionnée est valide

        // Sélection de l'opération en fonction du caractère saisi
        switch (op) {
            case '+':
                resultat = addition.appliquer(num1, num2);
                break;
            case '-':
                resultat = soustraction.appliquer(num1, num2);
                break;
            case '*':
                resultat = multiplication.appliquer(num1, num2);
                break;
            case '/':
                // Vérification pour éviter une division par zéro
                if (num2 == 0) {
                    valide = false;
                } else {
                    resultat = division.appliquer(num1, num2);
                }
                break;
            default:
                // Le caractère saisi ne correspond à aucune opération
                valide = false;
                System.out.println("Opérateur non reconnu.");
        }

        // Afficher le résultat si l'opération était valide
        if (valide && op != '/' || (op == '/' && num2 != 0)) {
            System.out.println("Résultat: " + resultat);
        }
        
        // Fermeture du scanner pour libérer les ressources
        scanner.close();
    }
}