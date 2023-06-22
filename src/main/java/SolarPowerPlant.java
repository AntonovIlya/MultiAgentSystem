import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SolarPowerPlant extends Agent {
    private double storageS = 0;
    private Agent agentName = this;
    private MessageTemplate mt;

    @Override
    protected void setup() {
        AID topic = subscribeTopic("Test");
        mt = MessageTemplate.MatchTopic(topic);
        Runnable Solar = new Runnable() {
            @Override
            public void run() {
                addBehaviour(new CyclicBehaviour() {
                    @Override
                    public void action() {
                        ACLMessage msg = myAgent.receive(mt);
                        ACLMessage msg1 = agentName.receive();
                        if (msg != null) addBehaviour(new SendMessageW(getAgent(),0, msg.getSender().getLocalName(),"CalcWin " + parseID(msg.getContent()) + " " + calcPrice()));
                        if (msg1 != null) setStorageS(0,1);
                        block();
                    }
                });
                addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        setStorageS(generate(Time.getCurrentHour()), 0);
                        //System.out.println("Solar current generation [" + Time.getCurrentHour() + "] : " + generate(Time.getCurrentHour()));
                        System.out.println("\u001B[35m" + "Solar current storage [" + Time.getCurrentHour() + "] : " + getStorageS() + "\u001B[0m");
                    }
                });
                addBehaviour(new TickerBehaviour(agentName, Time.hourDuration) {
                    @Override
                    protected void onTick() {
                        setStorageS(generate(Time.getCurrentHour()), 0);
                        //System.out.println("Solar current generation [" + Time.getCurrentHour() + "] : " + generate(Time.getCurrentHour()));
                        System.out.println("\u001B[35m" + "Solar current storage [" + Time.getCurrentHour() + "] : " + getStorageS() + "\u001B[0m");
                    }
                });

            }
        };
        Thread thread1 = new Thread(Solar);
        thread1.start();


    }

    private double generate(double time) {
        double nominalPower = 30;
        double currentPower;
        double solarPower;
        if (time < 5 || time > 14.5) solarPower = 0;
        else {
            solarPower = Math.abs(-0.0002 * Math.pow(time, 6) + 0.0129 * Math.pow(time, 5) - 0.3359 * Math.pow(time, 4) + 3.7926 * Math.pow(time, 3) - 17.057 * Math.pow(time, 2) + 25.199 * time - 4.2164);
        }
        currentPower = nominalPower * solarPower / 100;
        return currentPower;
    }

    public void setStorageS(double increase, double decrease) {
        double currentStorage = storageS + increase;
        if (currentStorage >= 20) {
            //System.out.println("Storage SolarPP full");
            storageS = 20;
        } else {
            storageS += increase;
            storageS -= decrease;
        }
    }

    public double getStorageS() {
        return storageS;
    }

    private double calcPrice() {
        double price;
        if (getStorageS() < 1) price = 1000;
        else price = (100 / (getStorageS() + 0.01) + 15 / (generate(Time.getCurrentHour()) + 1));
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
