package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatbotController extends BaseController implements Initializable {

    @FXML private VBox chatBox;
    @FXML private TextField messageField;
    @FXML private ScrollPane scrollPane;
    @FXML private Button sendButton;

    // Expanded knowledge base for recycling topics
    private final Map<String, String> knowledgeBase = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the chat UI
        scrollPane.setFitToWidth(true);
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));

        // Initialize knowledge base
        initializeKnowledgeBase();

        // Add welcome message
        addBotMessage("🌿 Bonjour! Je suis EcoBot, votre assistant pour tout savoir sur le recyclage. Je peux répondre à vos questions sur les matériaux, les processus de recyclage, ou vous donner des conseils pour réduire vos déchets. Essayez de me demander : 'Comment recycler le plastique ?' ou 'Pourquoi recycler est important ?'");

        // Set up event handlers
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSendMessage();
            }
        });

        sendButton.setOnAction(event -> handleSendMessage());
    }

    private void handleSendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            addUserMessage(message);
            messageField.clear();

            // Process the user's message and generate a response
            String response = processMessage(message);
            addBotMessage(response);
        }
    }

    private void addUserMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
        messageContainer.setPadding(new Insets(5, 5, 5, 10));

        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add("user-message");

        Text text = new Text(message);
        text.setStyle("-fx-fill: white;");
        textFlow.getChildren().add(text);

        messageContainer.getChildren().add(textFlow);
        chatBox.getChildren().add(messageContainer);

        // Auto-scroll to the bottom
        scrollPane.setVvalue(1.0);
    }

    private void addBotMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        messageContainer.setPadding(new Insets(5, 10, 5, 5));

        // Add bot avatar
        try {
            ImageView botAvatar = new ImageView(new Image(getClass().getResourceAsStream("/images/ecobot_avatar.png")));
            botAvatar.setFitHeight(30);
            botAvatar.setFitWidth(30);
            botAvatar.setPreserveRatio(true);
            messageContainer.getChildren().add(botAvatar);
        } catch (Exception e) {
            // If avatar image is not found, use a placeholder
            VBox avatarPlaceholder = new VBox();
            avatarPlaceholder.setStyle("-fx-background-color: #2E7D32; -fx-background-radius: 15px;");
            avatarPlaceholder.setPrefWidth(30);
            avatarPlaceholder.setPrefHeight(30);
            messageContainer.getChildren().add(avatarPlaceholder);
        }

        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add("bot-message");

        Text text = new Text(message);
        text.setStyle("-fx-fill: #333333;");
        textFlow.getChildren().add(text);

        messageContainer.getChildren().add(textFlow);
        chatBox.getChildren().add(messageContainer);

        // Auto-scroll to the bottom
        scrollPane.setVvalue(1.0);
    }

    private void initializeKnowledgeBase() {
        // General Recycling
        knowledgeBase.put("recyclage", "Le recyclage est le processus de transformation des déchets en nouveaux produits. Il permet de réduire la quantité de déchets envoyés aux décharges, de préserver les ressources naturelles et de diminuer les émissions de gaz à effet de serre. Que voulez-vous savoir de plus sur le recyclage ?");
        knowledgeBase.put("pourquoi recycler", "Recycler est essentiel car cela réduit la pollution, économise les ressources naturelles comme le bois, le pétrole et les minéraux, et diminue la consommation d'énergie. Par exemple, recycler une tonne de papier peut sauver 17 arbres et 380 gallons de pétrole ! Saviez-vous que le recyclage réduit aussi les émissions de CO2 ?");
        knowledgeBase.put("comment recycler", "Pour recycler, triez vos déchets selon les catégories : plastique, papier, verre, métal, et organique. Déposez-les dans les bacs de recyclage appropriés ou apportez-les à un centre de recyclage. Vous pouvez me demander comment recycler un matériau spécifique, comme 'Comment recycler le plastique ?'");

        // Materials
        knowledgeBase.put("plastique", "Le plastique est recyclable, mais il doit être trié par type (PET, HDPE, etc.). Nettoyez les contenants plastiques avant de les mettre dans le bac de recyclage bleu. Saviez-vous que le plastique peut prendre jusqu'à 1000 ans pour se décomposer ? Recycler réduit la pollution marine ! Conseil éducatif : Essayez d'utiliser des bouteilles réutilisables pour réduire votre consommation de plastique.");
        knowledgeBase.put("papier", "Le papier est facile à recycler ! Placez les journaux, magazines, et cartons dans le bac de recyclage. Évitez les papiers souillés (comme les serviettes usagées). Recycler une tonne de papier sauve 17 arbres et 7000 gallons d'eau. Conseil éducatif : Privilégiez le papier recyclé pour vos impressions !");
        knowledgeBase.put("verre", "Le verre est 100% recyclable et peut être recyclé à l'infini sans perte de qualité. Déposez les bouteilles et bocaux en verre dans le bac approprié (souvent vert). Recycler le verre réduit la consommation d'énergie et les émissions de CO2. Conseil éducatif : Réutilisez les bocaux en verre pour stocker des aliments !");
        knowledgeBase.put("métal", "Les métaux comme l'aluminium et l'acier sont très recyclables. Les canettes, boîtes de conserve et aluminium doivent aller dans le bac de recyclage. Recycler l'aluminium utilise 95% moins d'énergie que produire du neuf ! Conseil éducatif : Écrasez les canettes pour économiser de l'espace dans le bac.");
        knowledgeBase.put("organique", "Les déchets organiques, comme les épluchures de fruits et légumes, peuvent être compostés. Le compostage transforme ces déchets en un engrais naturel riche pour le sol. Conseil éducatif : Créez un compost à la maison pour réduire vos déchets et enrichir votre jardin !");
        knowledgeBase.put("électronique", "Les déchets électroniques (e-déchets) contiennent des matériaux précieux mais aussi des substances toxiques. Apportez vos vieux appareils à un point de collecte spécialisé. Recycler les e-déchets permet de récupérer des métaux rares et d'éviter la pollution. Conseil éducatif : Donnez ou réparez les appareils encore fonctionnels avant de les recycler !");

        // Recycling Processes
        knowledgeBase.put("processus", "Le processus de recyclage comprend plusieurs étapes : la collecte des déchets, le tri par type de matériau, le nettoyage, la transformation (par exemple, fondre le plastique ou le verre), et enfin la fabrication de nouveaux produits. Chaque matériau suit un processus spécifique. Voulez-vous en savoir plus sur un matériau particulier ?");
        knowledgeBase.put("tri", "Le tri est une étape clé du recyclage. Les déchets sont séparés par type (plastique, papier, verre, etc.) dans des centres de tri. Certains centres utilisent des machines automatisées, tandis que d'autres font appel à des travailleurs manuels. Un bon tri à la maison facilite ce processus !");

        // Benefits and Statistics
        knowledgeBase.put("avantages", "Les avantages du recyclage incluent la réduction de la pollution, la conservation des ressources naturelles, et des économies d'énergie. Par exemple, recycler une tonne de plastique peut économiser jusqu'à 5774 kWh d'énergie. Recycler aide aussi à protéger la biodiversité en réduisant les décharges !");
        knowledgeBase.put("statistiques", "Voici quelques statistiques sur le recyclage : environ 75% du papier et carton est recyclé en Europe. Recycler une seule canette en aluminium économise assez d'énergie pour alimenter une télévision pendant 3 heures ! Seulement 9% du plastique mondial est recyclé, il y a donc beaucoup de progrès à faire.");

        // Educational Tips
        knowledgeBase.put("conseils", "Voici quelques conseils pour mieux recycler : 1) Rincez les contenants avant de les recycler. 2) Vérifiez les règles de tri locales, car elles varient. 3) Réduisez votre consommation en utilisant des produits réutilisables. 4) Apprenez à composter vos déchets organiques. Voulez-vous un conseil spécifique ?");
        knowledgeBase.put("éducation", "Le recyclage est un excellent sujet éducatif ! Enseigner le recyclage aux enfants peut les sensibiliser dès le plus jeune âge. Par exemple, organisez une activité où ils trient des déchets factices en catégories. Vous pouvez aussi leur montrer comment fabriquer des objets à partir de matériaux recyclés, comme des pots de fleurs avec des bouteilles en plastique !");
        knowledgeBase.put("réduire", "Réduire est la première étape pour minimiser les déchets. Utilisez des sacs réutilisables, évitez les produits à usage unique, et achetez en vrac pour réduire les emballages. Réduire est souvent plus efficace que recycler !");
        knowledgeBase.put("réutiliser", "Réutiliser signifie donner une seconde vie aux objets. Par exemple, transformez une vieille bouteille en plastique en un pot de fleurs, ou donnez vos vêtements usagés à des associations. Réutiliser réduit la quantité de déchets à recycler.");

        // Tunisia specific
        knowledgeBase.put("tunisie", "La Tunisie fait face à des défis importants en matière de gestion des déchets. Le pays produit environ 2,8 millions de tonnes de déchets solides par an. Des initiatives comme 'Tunisie Recyclage' et 'Green Tunisia' travaillent à améliorer les infrastructures de recyclage.");
    }

    private String processMessage(String message) {
        String lowerMessage = message.toLowerCase();

        // Check for specific question patterns
        if (lowerMessage.contains("comment recycler") || lowerMessage.contains("recycler")) {
            if (lowerMessage.contains("plastique")) return knowledgeBase.get("plastique");
            if (lowerMessage.contains("papier")) return knowledgeBase.get("papier");
            if (lowerMessage.contains("verre")) return knowledgeBase.get("verre");
            if (lowerMessage.contains("métal")) return knowledgeBase.get("métal");
            if (lowerMessage.contains("organique")) return knowledgeBase.get("organique");
            if (lowerMessage.contains("électronique")) return knowledgeBase.get("électronique");
            return knowledgeBase.get("comment recycler");
        }

        // Check for keywords in the knowledge base
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Default responses for greetings, farewells, and unknown queries
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
}
