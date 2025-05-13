package gestionnaire;

import modele.Tache;
import modele.TachesSysteme;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireTaches {

    public void executerTaches() {
        TachesSysteme tachesSysteme = new TachesSysteme();
        List<Method> taches = new ArrayList<>();
        Method[] methods = TachesSysteme.class.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Tache.class)) {
                taches.add(method);
            }
        }

        taches.sort((m1, m2) -> Integer.compare(
            m1.getAnnotation(Tache.class).priorite(),
            m2.getAnnotation(Tache.class).priorite()
        ));

        for (Method tache : taches) {
            try {
                System.out.println("Exécution de la tâche : " + tache.getAnnotation(Tache.class).description());
                tache.invoke(tachesSysteme);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}