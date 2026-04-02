public class IoCBlackList {
    private int id;
    private String indicatorValue;
    private String indicatorType;

    public IoCBlackList(int id, String indicatorValue, String indicatorType) {
        this.id = id;
        this.indicatorValue = indicatorValue;
        this.indicatorType = indicatorType;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getIndicatorValue() {
        return indicatorValue;
    }
    public void setIndicatorValue(String indicatorValue) {
        this.indicatorValue = indicatorValue;
    }
    public String getIndicatorType() {
        return indicatorType;
    }
    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    @Override
    public String toString() {
        return "Id: " + id + "|" + "Value:" + indicatorValue + "|" + "Type:" +  indicatorType;
    }
}
