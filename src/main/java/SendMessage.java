import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMessage extends OneShotBehaviour {
    private String receiver;
    private String content;

    public SendMessage(String receiver, String content) {
        this.receiver = receiver;
        this.content = content;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID aid1 = new AID(receiver, false);
        msg.addReceiver(aid1);
        msg.setContent(content);
        getAgent().send(msg);
        //System.out.println("Message \"" + content + "\" send to " + receiver + " from " + getAgent().getLocalName());
    }
}
