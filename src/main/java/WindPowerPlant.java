import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WindPowerPlant extends Agent {
    private double storageW = 0;
    private Agent agentName = this;
    private MessageTemplate mt;

    @Override
    protected void setup() {
        AID topic = subscribeTopic("Test");
        mt = MessageTemplate.MatchTopic(topic);
        Runnable Wind = new Runnable() {
            @Override
            public void run() {
                addBehaviour(new CyclicBehaviour() {
                    @Override
                    public void action() {
                        ACLMessage msg = myAgent.receive(mt);
                        ACLMessage msg1 = agentName.receive();
                        if (msg != null) addBehaviour(new SendMessageW(getAgent(),35, msg.getSender().getLocalName(),"CalcWin " + parseID(msg.getContent()) + " " + calcPrice()));
                        if (msg1 != null) setStorageW(0,1);
                        block();
                    }
                });
                addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        setStorageW(generate(Time.getCurrentHour()), 0);
                        //System.out.println("Wind current generation [" + Time.getCurrentHour() + "] : " + generate(Time.getCurrentHour()));
                        System.out.println("\u001B[35m" + "Wind current storage [" + Time.getCurrentHour() + "] : " + getStorageW()+ "\u001B[0m");
                    }
                });
                addBehaviour(new TickerBehaviour(agentName, Time.hourDuration) {
                    @Override
                    protected void onTick() {
                        setStorageW(generate(Time.getCurrentHour()), 0);
                        //System.out.println("Wind current generation [" + Time.getCurrentHour() + "] : " + generate(Time.getCurrentHour()));
                        System.out.println("\u001B[35m" + "Wind current storage [" + Time.getCurrentHour() + "] : " + getStorageW()+ "\u001B[0m");
                    }
                });
            }
        };
        Thread thread2 = new Thread(Wind);
        thread2.start();

    }

    private double generate(double time) {
        double windSpeed;
        double nominalPower = 20;
        double currentPower;
        windSpeed = 1000 * (1 / (5 * Math.sqrt(2 * Math.PI))) * (Math.pow(Math.E, (-((Math.pow(time - 6, 2)) / (50)))));
        currentPower = nominalPower * windSpeed / 100;
        return currentPower;
    }

    public void setStorageW(double increase, double decrease) {
        double currentStorage = storageW + increase;
        if (currentStorage  >= 20) {
            //System.out.println("Storage WindPP full");
            storageW = 20;
        } else {
            storageW += increase;
            storageW -= decrease;
        }
    }

    public double getStorageW() {
        return storageW;
    }

    private double calcPrice() {
        double price;
        if (getStorageW() < 1) price = 1000;
        else price = (100 / (getStorageW()+0.01) + 16 / (generate(Time.getCurrentHour()) + 1));
        return price;

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
