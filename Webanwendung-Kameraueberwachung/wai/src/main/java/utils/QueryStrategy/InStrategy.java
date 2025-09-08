package utils.QueryStrategy;

import java.util.ArrayList;
import java.util.List;

public class InStrategy<T extends Object> extends BaseStrategy {
    
    
    private final List<T> param;
    
    @Override
    public List<Object> getParams() {
        return new ArrayList<>(param);
    }
    
    public InStrategy(String attribute, List<T> params){
        
        filterString = attribute + " in (";
        for(int i = 0; i < params.size(); i++){
            filterString += " ?";
            if(i < params.size() - 1){
               filterString += ", " ;
            }
        }
        filterString += " ) ";
        
        param = new ArrayList<>(params);
    }
}
