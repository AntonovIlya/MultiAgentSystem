import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "cfg")
@XmlAccessorType(XmlAccessType.FIELD)
public class Agentcfg {
    @XmlElement
    private  String name;
    @XmlElementWrapper(name="loadPower")
    @XmlElement(name="power")
    private List<Double> loadPower = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getLoadPower() {
        return loadPower;
    }

    public void setLoadPower(List<Double> loadPower) {
        this.loadPower = loadPower;
    }
}
