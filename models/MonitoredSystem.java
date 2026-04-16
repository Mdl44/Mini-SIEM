package models;

public class MonitoredSystem {
    private int id;
    private final String name;
    private final String ipAddress;
    private final String osType;
    private final String importance; // LOW, MEDIUM, HIGH,  CRITICAL

    public MonitoredSystem(int id, String name, String ipAddress, String osType, String importance) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.osType = osType;
        this.importance = importance;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public String getOsType() {return osType;}
    public String getImportance() {
        return importance;
    }

    @Override
    public String toString() {
        return "[System]" + " ID: " +  id + "|" + " Name: " + name + "|" + " IP: " + ipAddress + "|" + " OS: " + osType +  "|" + " Importance: " + importance;
    }
}
