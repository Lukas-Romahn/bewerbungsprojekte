package utils.QueryStrategy;

import java.util.ArrayList;
import java.util.List;

public class LessStrategy<T extends Object> extends BaseStrategy {
    
    private final List<T> param;
    
    public LessStrategy(String attribute, T param){
        filterString = attribute + " < ?";
        this.param = List.of(param);
    }
    
    @Override
    public List<Object> getParams() {
        return new ArrayList<>(param);
    }
}