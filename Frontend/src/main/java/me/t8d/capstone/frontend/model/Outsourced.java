/**
 * Implements Outsourced as a subtype of Part
 */
package me.t8d.capstone.frontend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Thomas Miller
 */
@JsonSerialize
@JsonDeserialize
public class Outsourced extends Part{
    private String companyName;
    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    @Override
    public String getType() {
        return "Outsourced";
    }

    public Outsourced() {super();}

    /**
     * @param companyName the company name that produced the part
     */
    public void setCompanyName(String companyName) {this.companyName = companyName;}

    /**
     * @return the company name that produced the part
     */
    public String getCompanyName() {return this.companyName;}
    @Override
    public String toJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.valueToTree(this);
        node.put("type", "Outsourced");
        return node.toString();
    }
}
