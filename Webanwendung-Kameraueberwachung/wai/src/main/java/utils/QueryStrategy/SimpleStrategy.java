package utils.QueryStrategy;

import java.util.ArrayList;
import java.util.List;

public class SimpleStrategy extends BaseStrategy {

    private Object value;
    private List<Object> params = new ArrayList<>();

    public SimpleStrategy(String filterString, Object value, String type) {
        this.filterString = filterString;
        this.value = value;
        this.type = type;

        if (value != null) {
            params.add(value);
        }
    }

    @Override
    public List<Object> getParams() {
        return params;
    }
}
