package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service pour le chatbot qui fournit des réponses sur le recyclage.
 * Peut fonctionner en mode local (base de connaissances intégrée) ou
 * se connecter à une API externe pour des réponses plus avancées.
 */
public class ChatbotService {

    // Singleton instance
    private static ChatbotService instance;

    // Base de connaissances locale
    private final Map<String, String> knowledgeBase = new HashMap<>();

    // Service d'exécution pour les appels asynchrones
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // Configuration pour l'API externe (optionnelle)
    private static final String API_URL = "https://api.example.com/chatbot"; // Remplacer par l'URL de votre API
    private static final String API_KEY = ""; // Votre clé API si nécessaire

    // Mode de fonctionnement
    private boolean useLocalKnowledge = true;

    /**
     * Constructeur privé pour le singleton
     */
    private ChatbotService() {
        initializeKnowledgeBase();
    }

    /**
     * Obtenir l'instance unique du service
     */
    public static synchronized ChatbotService getInstance() {
        if (instance == null) {
            instance = new ChatbotService();
        }
        return instance;
    }

    /**
     * Définir le mode de fonctionnement
     * @param useLocal true pour utiliser la base de connaissances locale, false pour l'API externe
     */
    public void setUseLocalKnowledge(boolean useLocal) {
        this.useLocalKnowledge = useLocal;
    }

    /**
     * Obtenir une réponse du chatbot de manière asynchrone
     * @param userMessage Le message de l'utilisateur
     * @return Un CompletableFuture contenant la réponse
     */
    public CompletableFuture<String> getResponseAsync(String userMessage) {
        return CompletableFuture.supplyAsync(() -> {
            if (useLocalKnowledge || API_KEY.isEmpty()) {
                return getLocalResponse(userMessage);
            } else {
                try {
                    return getExternalAPIResponse(userMessage);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'appel à l'API externe: " + e.getMessage());
                    // En cas d'erreur, utiliser la réponse locale comme fallback
                    return "Désolé, je ne peux pas me connecter à mon cerveau en ce moment. " +
                            "Voici ce que je peux vous dire: \n\n" + getLocalResponse(userMessage);
                }
            }
        }, executorService);
    }

    /**
     * Obtenir une réponse de la base de connaissances locale
     */
    private String getLocalResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        // Vérifier les requêtes liées à la carte
        if (lowerMessage.contains("carte") || lowerMessage.contains("map") ||
                lowerMessage.contains("localiser") || lowerMessage.contains("trouver")) {
            return "Vous pouvez accéder à notre carte interactive des sociétés de recyclage en Tunisie en cliquant sur 'Carte des Sociétés' dans le menu de navigation. Cette carte vous permettra de localiser facilement les sociétés de recyclage près de chez vous.";
        }

        // Vérifier les modèles de questions spécifiques
        if (lowerMessage.contains("comment recycler") || lowerMessage.contains("recycler")) {
            if (lowerMessage.contains("plastique")) return knowledgeBase.get("plastique");
            if (lowerMessage.contains("papier")) return knowledgeBase.get("papier");
            if (lowerMessage.contains("verre")) return knowledgeBase.get("verre");
            if (lowerMessage.contains("métal")) return knowledgeBase.get("métal");
            if (lowerMessage.contains("organique")) return knowledgeBase.get("organique");
            if (lowerMessage.contains("électronique")) return knowledgeBase.get("électronique");
            return knowledgeBase.get("comment recycler");
        }

        // Vérifier les mots-clés dans la base de connaissances
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Réponses par défaut pour les salutations, adieux et requêtes inconnues
        if (lowerMessage.contains("bonjour") || lowerMessage.contains("salut") || lowerMessage.contains("hello")) {
            return "Bonjour! Je suis EcoBot, votre assistant de recyclage. Comment puis-je vous aider aujourd'hui ? Posez-moi une question sur le recyclage, comme 'Pourquoi recycler ?' ou 'Comment recycler le plastique ?'";
        } else if (lowerMessage.contains("merci")) {
            return "De rien ! Si vous avez d'autres questions sur le recyclage, je suis là pour vous aider.";
        } else if (lowerMessage.contains("au revoir") || lowerMessage.contains("bye")) {
            return "Au revoir ! N'oubliez pas de trier vos déchets et de recycler pour protéger notre planète ! 🌍";
        } else {
            return "Je ne suis pas sûr de comprendre votre question. Essayez de me demander quelque chose sur le recyclage, les matériaux, les processus, ou des conseils éducatifs. Par exemple : 'Comment recycler le papier ?' ou 'Quels sont les avantages du recyclage ?'";
        }
    }

    /**
     * Obtenir une réponse d'une API externe
     */
    private String getExternalAPIResponse(String userMessage) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        if (!API_KEY.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        }
        connection.setDoOutput(true);

        // Créer le corps de la requête JSON
        String jsonInputString = String.format(
                "{\"query\": \"%s\", \"context\": \"recyclage, environnement, Tunisie\"}",
                userMessage.replace("\"", "\\\"")
        );

        // Envoyer la requête
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Lire la réponse
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Extraire le texte de la réponse JSON (ceci est un exemple simplifié)
        String jsonResponse = response.toString();
        // Dans un cas réel, utilisez une bibliothèque JSON pour l'extraction
        if (jsonResponse.contains("\"response\":\"")) {
            int start = jsonResponse.indexOf("\"response\":\"") + 12;
            int end = jsonResponse.indexOf("\"", start);
            if (start > 0 && end > start) {
                return jsonResponse.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            }
        }

        return "Je n'ai pas pu comprendre la réponse de l'API. Veuillez réessayer.";
    }

    /**
     * Initialiser la base de connaissances locale
     */
    private void initializeKnowledgeBase() {
        // Recyclage général
        knowledgeBase.put("recyclage", "Le recyclage est le processus de transformation des déchets en nouveaux produits. Il permet de réduire la quantité de déchets envoyés aux décharges, de préserver les ressources naturelles et de diminuer les émissions de gaz à effet de serre. Que voulez-vous savoir de plus sur le recyclage ?");
        knowledgeBase.put("pourquoi recycler", "Recycler est essentiel car cela réduit la pollution, économise les ressources naturelles comme le bois, le pétrole et les minéraux, et diminue la consommation d'énergie. Par exemple, recycler une tonne de papier peut sauver 17 arbres et 380 gallons de pétrole ! Saviez-vous que le recyclage réduit aussi les émissions de CO2 ?");
        knowledgeBase.put("comment recycler", "Pour recycler, triez vos déchets selon les catégories : plastique, papier, verre, métal, et organique. Déposez-les dans les bacs de recyclage appropriés ou apportez-les à un centre de recyclage. Vous pouvez me demander comment recycler un matériau spécifique, comme 'Comment recycler le plastique ?'");

        // Matériaux
        knowledgeBase.put("plastique", "Le plastique est recyclable, mais il doit être trié par type (PET, HDPE, etc.). Nettoyez les contenants plastiques avant de les mettre dans le bac de recyclage bleu. Saviez-vous que le plastique peut prendre jusqu'à 1000 ans pour se décomposer ? Recycler réduit la pollution marine ! Conseil éducatif : Essayez d'utiliser des bouteilles réutilisables pour réduire votre consommation de plastique.");
        knowledgeBase.put("papier", "Le papier est facile à recycler ! Placez les journaux, magazines, et cartons dans le bac de recyclage. Évitez les papiers souillés (comme les serviettes usagées). Recycler une tonne de papier sauve 17 arbres et 7000 gallons d'eau. Conseil éducatif : Privilégiez le papier recyclé pour vos impressions !");
        knowledgeBase.put("verre", "Le verre est 100% recyclable et peut être recyclé à l'infini sans perte de qualité. Déposez les bouteilles et bocaux en verre dans le bac approprié (souvent vert). Recycler le verre réduit la consommation d'énergie et les émissions de CO2. Conseil éducatif : Réutilisez les bocaux en verre pour stocker des aliments !");
        knowledgeBase.put("métal", "Les métaux comme l'aluminium et l'acier sont très recyclables. Les canettes, boîtes de conserve et aluminium doivent aller dans le bac de recyclage. Recycler l'aluminium utilise 95% moins d'énergie que produire du neuf ! Conseil éducatif : Écrasez les canettes pour économiser de l'espace dans le bac.");
        knowledgeBase.put("organique", "Les déchets organiques, comme les épluchures de fruits et légumes, peuvent être compostés. Le compostage transforme ces déchets en un engrais naturel riche pour le sol. Conseil éducatif : Créez un compost à la maison pour réduire vos déchets et enrichir votre jardin !");
        knowledgeBase.put("électronique", "Les déchets électroniques (e-déchets) contiennent des matériaux précieux mais aussi des substances toxiques. Apportez vos vieux appareils à un point de collecte spécialisé. Recycler les e-déchets permet de récupérer des métaux rares et d'éviter la pollution. Conseil éducatif : Donnez ou réparez les appareils encore fonctionnels avant de les recycler !");

        // Processus de recyclage
        knowledgeBase.put("processus", "Le processus de recyclage comprend plusieurs étapes : la collecte des déchets, le tri par type de matériau, le nettoyage, la transformation (par exemple, fondre le plastique ou le verre), et enfin la fabrication de nouveaux produits. Chaque matériau suit un processus spécifique. Voulez-vous en savoir plus sur un matériau particulier ?");
        knowledgeBase.put("tri", "Le tri est une étape clé du recyclage. Les déchets sont séparés par type (plastique, papier, verre, etc.) dans des centres de tri. Certains centres utilisent des machines automatisées, tandis que d'autres font appel à des travailleurs manuels. Un bon tri à la maison facilite ce processus !");

        // Avantages et statistiques
        knowledgeBase.put("avantages", "Les avantages du recyclage incluent la réduction de la pollution, la conservation des ressources naturelles, et des économies d'énergie. Par exemple, recycler une tonne de plastique peut économiser jusqu'à 5774 kWh d'énergie. Recycler aide aussi à protéger la biodiversité en réduisant les décharges !");
        knowledgeBase.put("statistiques", "Voici quelques statistiques sur le recyclage : environ 75% du papier et carton est recyclé en Europe. Recycler une seule canette en aluminium économise assez d'énergie pour alimenter une télévision pendant 3 heures ! Seulement 9% du plastique mondial est recyclé, il y a donc beaucoup de progrès à faire.");

        // Conseils éducatifs
        knowledgeBase.put("conseils", "Voici quelques conseils pour mieux recycler : 1) Rincez les contenants avant de les recycler. 2) Vérifiez les règles de tri locales, car elles varient. 3) Réduisez votre consommation en utilisant des produits réutilisables. 4) Apprenez à composter vos déchets organiques. Voulez-vous un conseil spécifique ?");
        knowledgeBase.put("éducation", "Le recyclage est un excellent sujet éducatif ! Enseigner le recyclage aux enfants peut les sensibiliser dès le plus jeune âge. Par exemple, organisez une activité où ils trient des déchets factices en catégories. Vous pouvez aussi leur montrer comment fabriquer des objets à partir de matériaux recyclés, comme des pots de fleurs avec des bouteilles en plastique !");
        knowledgeBase.put("réduire", "Réduire est la première étape pour minimiser les déchets. Utilisez des sacs réutilisables, évitez les produits à usage unique, et achetez en vrac pour réduire les emballages. Réduire est souvent plus efficace que recycler !");
        knowledgeBase.put("réutiliser", "Réutiliser signifie donner une seconde vie aux objets. Par exemple, transformez une vieille bouteille en plastique en un pot de fleurs, ou donnez vos vêtements usagés à des associations. Réutiliser réduit la quantité de déchets à recycler.");

        // Spécifique à la Tunisie
        knowledgeBase.put("tunisie", "La Tunisie fait face à des défis importants en matière de gestion des déchets. Le pays produit environ 2,8 millions de tonnes de déchets solides par an. Des initiatives comme 'Tunisie Recyclage' et 'Green Tunisia' travaillent à améliorer les infrastructures de recyclage.");

        // Lié à la carte
        knowledgeBase.put("carte", "Vous pouvez consulter notre carte interactive des sociétés de recyclage en Tunisie. Allez dans la section 'Carte des Sociétés' dans le menu de navigation.");
        knowledgeBase.put("map", "Notre application dispose d'une carte interactive qui vous permet de localiser les sociétés de recyclage en Tunisie. Accédez-y via le menu 'Carte des Sociétés'.");
    }

    /**
     * Arrêter le service d'exécution lors de la fermeture de l'application
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
