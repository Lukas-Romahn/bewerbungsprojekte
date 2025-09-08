package utils.QueryStrategy;

import java.util.ArrayList;
import java.util.List;

public class EqualsStrategy<T extends Object> extends BaseStrategy {
    
    private final List<T> param;
    
    public EqualsStrategy(String attribute, T param){
        filterString = attribute + " = ?";
        this.param = List.of(param);
    }
    
    @Override
    public List<Object> getParams() {
        return new ArrayList<>(param);
    }
}
