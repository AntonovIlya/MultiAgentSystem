import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class Distributor extends Agent {
    public AID createTopic;
    private int count = 0;
    private double minPrice = 900;
    private double currentID;
    private String winner;
    private String currentConsumer;
    private boolean flag = false;

    @Override
    protected void setup() {
        createTopic = createTopic("Test");
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(createTopic);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    /*System.out.println(" - " +
                            myAgent.getLocalName() + " received a message \"" +
                            msg.getContent() + "\" from " +
                            msg.getSender().getLocalName()
                    );*/
                    switch (parseZ(msg.getContent())) {
                        case ("Agent1"):
                            addBehaviour(new SendMessageT(getAgent(), 0, createTopic, "Buy " + parseID(msg.getContent())));
                            currentID = parseID(msg.getContent());
                            currentConsumer = msg.getSender().getLocalName();
                            break;
                        case ("Псс"):
                            System.out.println("Крыса готова продавать!");
                            flag = true;
                            break;
                        case ("Agent2"):
                            addBehaviour(new SendMessageT(getAgent(), 1, createTopic, "Buy " + parseID(msg.getContent())));
                            currentID = parseID(msg.getContent());
                            currentConsumer = msg.getSender().getLocalName();
                            break;
                        case ("CalcWin"):
                            if (parseID(msg.getContent()) == currentID) {
                                count++;
                                if (parsePrice(msg.getContent()) < minPrice) {
                                    minPrice = parsePrice(msg.getContent());
                                    winner = msg.getSender().getLocalName();
                                }
                                if (count == 3 && minPrice != 900) {
                                    if (flag) { // если крыса вмешалась в торги
                                        System.out.println("\u001B[32m" + "Минимальная цена: " + minPrice + " крыса продала за: " + (minPrice - 1) + "\u001B[0m");
                                        addBehaviour(new SendMessageW(getAgent(), 0, currentConsumer, "Success" + currentID));
                                        addBehaviour(new SendMessageW(getAgent(), 0, "Agent1", "Сделка" + currentID));
                                        minPrice = 900;
                                        count = 0;
                                    } else {
                                        System.out.println("\u001B[32m" + "Minimal price: " + minPrice + ", winner: " + winner + ", ID: " + currentID + "\u001B[0m");
                                        addBehaviour(new SendMessageW(getAgent(), 0, winner, "Win " + currentID));
                                        addBehaviour(new SendMessageW(getAgent(), 0, currentConsumer, "Success " + currentID));
                                        minPrice = 900;
                                        count = 0;
                                    }
                                }
                                if (count == 3 && minPrice == 900) {
                                    //System.out.println("\033[31m" + "Empty! " + "ID: " + currentID + " Fail!" + "\u001B[0m");
                                    addBehaviour(new SendMessageW(getAgent(), 0, currentConsumer, "Fail " + currentID));
                                    count = 0;
                                }
                            }
                            break;
                    }
                }
                block();
            }
        });
    }

    private AID createTopic(String topicName) {
        TopicManagementHelper topicHelper = null;
        AID jadeTopic = null;
        try {
            topicHelper = (TopicManagementHelper)
                    getHelper(TopicManagementHelper.SERVICE_NAME);
            jadeTopic = topicHelper.createTopic(topicName);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return jadeTopic;
    }

    private double parseID(String message) {
        String massID[] = message.split(" ");
        return Double.parseDouble(massID[1]);

    }

    private String parseZ(String message) {
        String massZ[] = message.split(" ");
        return massZ[0];
    }

    private double parsePrice(String message) {
        String massZ[] = message.split(" ");
        return Double.parseDouble(massZ[2]);
    }
}
