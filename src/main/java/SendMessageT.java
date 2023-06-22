import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMessageT extends WakerBehaviour {
    private AID topic;
    private String content;

    public SendMessageT(Agent a, long timeout, AID topic, String content) {
        super(a, timeout);
        this.topic = topic;
        this.content = content;
    }

    @Override
    protected void onWake() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(topic);
        msg.setContent(content);
        getAgent().send(msg);
        //System.out.println("Message \"" + content + "\" send to AgentsGenerate from " + getAgent().getLocalName());
    }
}
