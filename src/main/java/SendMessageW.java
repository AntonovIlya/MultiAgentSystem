import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMessageW extends WakerBehaviour {
    private String receiver;
    private String content;

    public SendMessageW(Agent a, long timeout, String receiver, String content) {
        super(a, timeout);
        this.receiver = receiver;
        this.content = content;
    }

    @Override
    protected void onWake() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID aid1 = new AID(receiver, false);
        msg.addReceiver(aid1);
        msg.setContent(content);
        getAgent().send(msg);
        //System.out.println("Message \"" + content + "\" send to " + receiver + " from " + getAgent().getLocalName());
    }
}
