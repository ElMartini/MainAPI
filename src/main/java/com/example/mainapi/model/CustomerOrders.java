package com.example.mainapi.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerOrders {
    private int oNumber;
    private String cID;
    private String oIDs;
    private Double oValue;

    public int getoNumber() {
        return oNumber;
    }

    public void setoNumber(int oNumber) {
        this.oNumber = oNumber;
    }

    public String getcID() {
        return cID;
    }

    public void setcID(String cID) {
        this.cID = cID;
    }

    public String getoIDs() {
        return oIDs;
    }

    public void setoIDs(String oIDs) {
        this.oIDs = oIDs;
    }

    public Double getoValue() {
        return oValue;
    }

    public void setoValue(Double oValue) {
        this.oValue = oValue;
    }

    @Override
    public String toString() {
        return "CustomerOrders{" +
                "oNumber=" + oNumber +
                ", cID='" + cID + '\'' +
                ", oIDs='" + oIDs + '\'' +
                ", oValue=" + oValue +
                '}';
    }
}
