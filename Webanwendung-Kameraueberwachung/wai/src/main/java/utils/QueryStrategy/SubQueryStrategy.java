package utils.QueryStrategy;

import java.util.ArrayList;
import java.util.List;

public class SubQueryStrategy extends BaseStrategy{
    
    private final List<Object> param;
    
    @Override
    public List<Object> getParams() {
        return new ArrayList<>(param);
    }
    
    public SubQueryStrategy(String baseQuery, QueryBuilder builder){
        this.filterString = baseQuery + builder.getClause();
        if(this.filterString.contains("(") && !this.filterString.contains(")")){
            this.filterString += " )";
        }
        param = new ArrayList<>(builder.getParams());
    }
}
