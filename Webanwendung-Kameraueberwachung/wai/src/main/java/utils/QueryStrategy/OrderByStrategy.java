package utils.QueryStrategy;

import java.util.List;

public class OrderByStrategy extends BaseStrategy {
    

    private final List<Object> param;
    
    public OrderByStrategy(String attribute, String type){
        filterString = " ORDER BY " + attribute + " " + type;
        this.param = List.of();
        this.type = "OTHER";
    }
    
    @Override
    public List<Object> getParams() {
        return List.of();
    }
}
