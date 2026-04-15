package models;

import java.io.Serial;
import java.io.Serializable;

public class Admin extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Admin(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public void printDashboard() {
        System.out.println("\n--- ADMIN TERMINAL: " + getName().toUpperCase() + " ---");
        System.out.println("Status: System Configuration Mode Active");
        System.out.println("Privileges: FULL_CRUD_ACCESS, INFRASTRUCTURE_MGMT");
        System.out.println("-------------------------------------------");
    }

    @Override
    public String toString() {
        return super.toString() + " [Role: Administrator]";
    }
}