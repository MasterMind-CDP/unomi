package org.oasis_open.wemi.context.server.persistence.elasticsearch.conditions;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.oasis_open.wemi.context.server.api.Item;
import org.oasis_open.wemi.context.server.api.conditions.Condition;

/**
* Created by toto on 27/06/14.
*/
public class MatchAllConditionEvaluator implements ConditionEvaluator {

    @Override
    public boolean eval(Condition condition, Item item, ConditionEvaluatorDispatcher dispatcher) {
        return true;
    }
}
