class Admin extends User {
    private String accessLevel;
    private int managedSystemsCount;

    public Admin(int id, String name, String email, String accessLevel) {
        super(id, name, email);
        this.accessLevel = accessLevel;
        this.managedSystemsCount = 0;
    }

    public String getAccessLevel() {
        return accessLevel;
    }
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
    public int getManagedSystemsCount() {
        return managedSystemsCount;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public void enrollNewSystem() {
        this.managedSystemsCount++;
        System.out.println("Admin " + getName() + " has enrolled a new monitored system.");
    }

    public void updateSecurityPolicy() {
        System.out.println("Admin " + getName() + " updated the global security policies.");
    }
}
