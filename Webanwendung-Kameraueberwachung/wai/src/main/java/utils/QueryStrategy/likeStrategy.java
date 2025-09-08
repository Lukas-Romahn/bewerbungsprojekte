package utils.QueryStrategy;

import java.util.List;

public class likeStrategy<T extends Object> extends BaseStrategy{

    private final List<Object> param;
    
    public likeStrategy(String attribute, String type){
        filterString = attribute + " ilike ?";
        type += "%";
        this.param = List.of(type);
        this.type = "WHERE";
    }
    
    @Override
    public List<Object> getParams() {
        return param;
    }
}
