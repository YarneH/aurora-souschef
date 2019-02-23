package SouschefProcessor.Recipe;

public class Amount {

    private double value;
    private String unit;

    public Amount(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Amount){
            Amount a = (Amount) o;
            if(a.getUnit().equals(unit) && a.getValue()==value){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + unit.hashCode();
        result = 31 * result + Double.valueOf(value).hashCode();
        return result;
    }

    @Override
    public String toString(){
        return value+" "+unit;
    }
}
