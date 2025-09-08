package utils.QueryStrategy;

import java.util.ArrayList;
import java.util.List;

public class BetweenStrategy<T extends Object> extends BaseStrategy {
    
    private final List<T> param;
    
    @Override
    public List<Object> getParams() {
        return new ArrayList<>(param);
    }
    
    public BetweenStrategy(String attribute, T from, T end){
        
        this.filterString = attribute + " BETWEEN ? AND ? ";
        param = List.of(from, end);
    }
}
