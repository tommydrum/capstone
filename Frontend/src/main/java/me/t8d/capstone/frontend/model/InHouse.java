/**
 * Implements InHouse as a subtype of Part
 */
package me.t8d.capstone.frontend.model;

/**
 * @author Thomas Miller
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonSerialize
@JsonDeserialize
public class InHouse extends Part {
    private int machineId;
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    @Override
    public String getType() {
        return "InHouse";
    }

    public InHouse() {
        super();
    };

    /**
     * @param machineId the machine ID that produced the part
     */
    public void setMachineId(int machineId) {this.machineId = machineId;}

    /**
     * @return the machine ID that produced the part
     */
    public int getMachineId() {return this.machineId;}
    @Override
    public String toJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.valueToTree(this);
        node.put("type", "InHouse");
        return node.toString();
    }
}
