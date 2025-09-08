package utils.QueryStrategy;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    BaseStrategy rootStrat;
    String type;

    public QueryBuilder(String TYPE){
        this.type = TYPE;
    }
    public QueryBuilder(){
        this.type = "WHERE";
    }
    
    public void addStrategy(BaseStrategy strat){
        if(rootStrat == null){
            rootStrat = strat;
            return;
        }

        BaseStrategy currentStrategy = rootStrat;
        while(currentStrategy.next() != null){
            currentStrategy = currentStrategy.next();
        }
        currentStrategy.addStrategy(strat);
    }

    
    public String getClause(){
        if(rootStrat == null){
            return "";
        }

        BaseStrategy current = rootStrat;
        String clause = "";
        while(current != null){
            String currentFilterString = current.getFilterString();

            if(!clause.isBlank() && (current.getType().equals("WHERE"))){
                if(type.toUpperCase().equals("SET")){
                    clause += ", ";
                }else{
                    clause += " AND ";
                }
            }

            if(clause.isBlank() && current.getType().equals("WHERE")){
                clause += " " + type + " " ;
            }
            clause += currentFilterString;
            current = current.next();
        }
        
        return (clause);
    }
    
    
    public List<Object> getParams(){
        List<Object> parameters = new ArrayList<>();
        BaseStrategy current = rootStrat;
        while(current != null){
            List<Object> params = current.getParams();
            parameters.addAll(params);
            current = current.next();
        }
        
        return parameters;
    }
    
    public void popStrategy(){
        
        BaseStrategy current = rootStrat;
        if(rootStrat.next() == null){
            rootStrat = null;
            return;
        }
        while(current.next().next()!= null){
            current = current.next();
        }
        current.setNext(null);
    }
}
