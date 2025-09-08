package utils.QueryStrategy;

import java.util.List;

public class PaginateStrategy extends BaseStrategy {

    public PaginateStrategy(int LIMIT, int OFFSET){

        this.filterString = " LIMIT " + LIMIT+  " OFFSET " + (OFFSET-1 )* LIMIT;
        type = "OTHER";
    }
    
    @Override
    public List<Object> getParams(){
        return List.of();
        
    }
}
