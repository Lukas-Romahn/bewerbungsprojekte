package utils.QueryStrategy;

import java.util.List;

public class BaseStrategy{

    protected String type = "WHERE";
    private BaseStrategy next;
    protected String filterString;


    public void addStrategy(BaseStrategy strategy) {
        this.next = strategy;
    }

    public BaseStrategy next() {
        return next;
    }
    public void setNext(BaseStrategy next){
        this.next = next;
    }
    
    public String getFilterString(){
        return this.filterString;
    }

    public List<Object> getParams() {
        return List.of();
    }
    
    public String getType(){
        return type;
    }

}
