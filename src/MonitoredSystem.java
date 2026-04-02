public class MonitoredSystem {
    private int id;
    private String name;
    private String ipAddress;
    private String osType;

    public MonitoredSystem(int id, String name, String ipAddress, String osType) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.osType = osType;
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
    public void setName(String name) {
        this.name = name;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getOsType() {
        return osType;
    }
    public void setOsType(String osType) {
        this.osType = osType;
    }

    @Override
    public String toString() {
        return "[System]" + " ID: " +  id + "|" + " Name: " + name + "|" + " IP: " + ipAddress + "|" + " OS: " + osType;
    }
}
