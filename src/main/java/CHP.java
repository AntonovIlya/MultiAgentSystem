import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CHP extends Agent {
    private double storageC = 0;
    private Agent agentName = this;
    private MessageTemplate mt;

    @Override
    protected void setup() {
        AID topic = subscribeTopic("Test");
        mt = MessageTemplate.MatchTopic(topic);
        Runnable CHP = new Runnable() {
            @Override
            public void run() {
                addBehaviour(new CyclicBehaviour() {
                    @Override
                    public void action() {
                        ACLMessage msg = agentName.receive(mt);
                        ACLMessage msg1 = agentName.receive();
                        if (msg != null)
                            addBehaviour(new SendMessageW(getAgent(), 70, msg.getSender().getLocalName(), "CalcWin " + parseID(msg.getContent()) + " " + calcPrice()));
                        if (msg1 != null) setStorageC(0, 1);
                        block();
                    }
                });
                addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        setStorageC(10, 0);
                        System.out.println("\u001B[35m" + "CHP current storage [" + Time.getCurrentHour() + "] : " + getStorageC() + "\u001B[0m");
                    }
                });
                addBehaviour(new TickerBehaviour(agentName, Time.hourDuration) {
                    @Override
                    protected void onTick() {
                        setStorageC(10, 0);
                        System.out.println("\u001B[35m" + "CHP current storage [" + Time.getCurrentHour() + "] : " + getStorageC() + "\u001B[0m");

                    }
                });

            }
        };
        Thread thread3 = new Thread(CHP);
        thread3.start();

    }

    public double getStorageC() {
        return storageC;
    }

    public void setStorageC(double increase, double decrease) {
        double currentStorage = storageC + increase;
        if (currentStorage >= 20) {
            //System.out.println("Storage CHP full");
            storageC = 20;
        } else {
            storageC += increase;
            storageC -= decrease;
        }
    }

    private double calcPrice() {
        if (getStorageC() <= 0) return 1000;
        else return 10;
    }

    private AID subscribeTopic(String name) {
        TopicManagementHelper topicHelper = null;
        AID jadeTopic = null;
        try {
            topicHelper = (TopicManagementHelper)
                    getHelper(TopicManagementHelper.SERVICE_NAME);
            jadeTopic = topicHelper.createTopic(name);
            topicHelper.register(jadeTopic);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return jadeTopic;
    }

    private double parseID(String message) {
        String massID[] = message.split(" ");
        return Double.parseDouble(massID[1]);

    }

}
