import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class Consumer extends Agent {
    private List<Double> inf = new ArrayList<>();
    private double id1 = 1367;
    private double id2 = 2367;
    private int timeout;
    private int request1 = 0;
    private int request2 = 0;
    private double storage;
    private boolean flag = false;
    private double currentS = 50;

    @Override
    protected void setup() {
        Agentcfg Agentinf = WorkWithCfgs.unMarshalAny(Agentcfg.class, getLocalName() + ".xml");
        inf = Agentinf.getLoadPower();
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
                    if (msg.getSender().getLocalName().equals("Distributor1") && parseZ(msg.getContent()).equals("Success")) {
                        request1--;
                        setStorage(1,0);
                    }
                    if (msg.getSender().getLocalName().equals("Distributor2") && parseZ(msg.getContent()).equals("Success")) request2--;
                    if (parseZ(msg.getContent()).equals("Сделка")) setStorage(0,1);
                    if (msg.getSender().getLocalName().equals("Agent1") && parseZ(msg.getContent()).equals("Псс")) {
                        flag = true; // сообщение от крысы, значит можно купить у него
                    }
                    /*if (msg.getSender().getLocalName().equals("Agent2") && parseZ(msg.getContent()).equals("Покупаю")) {
                        System.out.println("Agent 2 купил 1 МВт...");
                        setStorage(0,1);
                    }*/
                }
                block();
            }
        });
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                if (getLocalName().equals("Agent1")) {
                    //System.out.println(getLocalName() + " хочет купить " + inf.get(Time.getCurrentHour()) + " МВт");
                    timeout = 50;
                    for (double i = 1; i <= 50; i++) {
                        request1++;
                        addBehaviour(new SendMessageW(getAgent(), timeout, "Distributor1", getLocalName() + " " + id1));
                        id1++;
                        timeout += 200;
                    }
                }
                if (getLocalName().equals("Agent2")) {
                    //System.out.println(getLocalName() + " хочет купить " + inf.get(Time.getCurrentHour()) + " МВт");
                    timeout = 150;
                    for (double i = 1; i <= inf.get(Time.getCurrentHour()); i++) {
                        request2++;
                        addBehaviour(new SendMessageW(getAgent(), timeout, "Distributor2", getLocalName() + " " + id2));
                        id2++;
                        timeout += 200;
                    }
                }
            }
        });
        addBehaviour(new TickerBehaviour(this, Time.hourDuration) {
            @Override
            protected void onTick() {
                if (getLocalName().equals("Agent1")) {
                    if (getStorage()<50) System.out.println("Текущий объём накопителя крысы: " + getStorage()); //
                    //if (request1!=0) System.out.println("\033[31m" + getLocalName() + ": не получилось купить " + request1 + " МВт за предыдущий час!" + "\u001B[0m");
                    //System.out.println(getLocalName() + " хочет купить " + inf.get(Time.getCurrentHour()) + " МВт");
                    timeout = 50;
                    //double size = inf.get(Time.getCurrentHour()) + request1;
                    request1 = 0;
                    if (getStorage()<50 && !flag) {
                        for (double i = 1; i <= (50); i++) {
                            request1++;
                            addBehaviour(new SendMessageW(getAgent(), timeout, "Distributor1", getLocalName() + " " + id1));
                            id1++;
                            timeout += 200;
                        }
                    }else {
                        addBehaviour(new SendMessageW(getAgent(),0,"Agent2","Псс")); // Накопитель крысы заполнен, можно начинать продавать, сообщение о вмешательстве в торги
                    }
                }
                if (getLocalName().equals("Agent2")) {
                    //if (request2 != 0) System.out.println("\033[31m" + getLocalName() + ": не получилось купить " + request1 + " МВт за предыдущий час!" + "\u001B[0m");
                    //System.out.println(getLocalName() + " хочет купить " + inf.get(Time.getCurrentHour()) + " МВт");
                    //double size = inf.get(Time.getCurrentHour()) + request2;
                    timeout = 150;
                    request2 = 0;
                    if (flag) addBehaviour(new SendMessageW(getAgent(),0,"Distributor2","Псс"));
                    for (double i = 1; i <= (inf.get(Time.getCurrentHour())); i++) {
                        addBehaviour(new SendMessageW(getAgent(), timeout, "Distributor2", getLocalName() + " " + id2));
                        request2++;
                        id2++;
                        timeout += 200;
                    }
                }
            }
        });

    }

    private double parseID(String message) {
        String massID[] = message.split(" ");
        return Double.parseDouble(massID[1]);

    }

    private String parseZ(String message) {
        String massZ[] = message.split(" ");
        return massZ[0];
    }

    public void setStorage(double increase, double decrease) {
        double currentStorage = storage + increase;
        if (currentStorage >= 50) {
            storage = 50;
        } else {
            storage += increase;
            storage -= decrease;
        }
    }

    public double getStorage() {
        return storage;
    }

}

