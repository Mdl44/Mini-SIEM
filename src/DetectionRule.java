public class DetectionRule {
    private int id;
    private String name;
    private String targetType;
    private String condition;
    private int threshold;
    private int timeWindow;

    public  DetectionRule(int id, String name, String targetType, String condition, int threshold, int timeWindow) {
        this.id = id;
        this.name = name;
        this.targetType = targetType;
        this.condition = condition;
        this.threshold = threshold;
        this.timeWindow = timeWindow;
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
    public String getTargetType() {
        return targetType;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public int getThreshold() {
        return threshold;
    }
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
    public int getTimeWindow() {
        return timeWindow;
    }
    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

    @Override
    public String toString() {
        return "[Detection Rule]" + " ID: " +  id + "|" + " Name: " + name + "|" + " Log Type: " + targetType + "|" + " Condition: " + condition + "|" + " Threshold: " + threshold + "|" + " TimeWindow: " + timeWindow;
    }

}
