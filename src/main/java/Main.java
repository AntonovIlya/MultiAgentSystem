import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Agentcfg Agent1 = new Agentcfg();
        Agentcfg Agent2 = new Agentcfg();
        List<Double> loadPowerAgent1 = new ArrayList<>();
        List<Double> loadPowerAgent2 = new ArrayList<>();
        for (long i = 0; i < 24; i++) {
            loadPowerAgent1.add(5.0);
            loadPowerAgent2.add(5.0);
        }
        Agent1.setName("Agent1");
        Agent2.setName("Agent2");
        Agent1.setLoadPower(loadPowerAgent1);
        Agent2.setLoadPower(loadPowerAgent2);
        WorkWithCfgs.marshalAny(Agentcfg.class, Agent1, "Agent1.xml");
        WorkWithCfgs.marshalAny(Agentcfg.class, Agent2, "Agent2.xml");

    }
}
