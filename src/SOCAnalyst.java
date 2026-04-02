class SOCAnalyst extends User {
    private String rank; // Junior, Mid, Senior
    private int resolvedIncidents;
    private int currentWorkload;

    public SOCAnalyst(int id, String name, String email, String rank) {
        super(id, name, email);
        this.resolvedIncidents = 0;
        this.rank = rank;
        this.currentWorkload = 0;
    }

    public int getResolvedIncidents() {
        return resolvedIncidents;
    }
    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public int getCurrentWorkload() {
        return currentWorkload;
    }

    @Override
    public String getRole() {
        return "SOC_ANALYST";
    }

    public void assignTask() {
        this.currentWorkload++;
    }

    public void solveTask() {
        if(currentWorkload > 0) {
            this.currentWorkload--;
            this.resolvedIncidents++;
            System.out.println("Analyst " + getName() + "solved an incident.");
        }
    }

    public boolean canAcceptTask() {
        if(rank.equals("Junior") && currentWorkload >= 5) return false;
        if(rank.equals("Mid") && currentWorkload >= 10) return false;
        return currentWorkload < 20;
    }
}
