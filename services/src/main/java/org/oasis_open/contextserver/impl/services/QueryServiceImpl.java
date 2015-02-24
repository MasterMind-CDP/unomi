package org.oasis_open.contextserver.impl.services;

import org.oasis_open.contextserver.api.conditions.Condition;
import org.oasis_open.contextserver.api.query.AggregateQuery;
import org.oasis_open.contextserver.api.services.DefinitionsService;
import org.oasis_open.contextserver.api.services.QueryService;
import org.oasis_open.contextserver.persistence.spi.PersistenceService;
import org.oasis_open.contextserver.persistence.spi.aggregate.DateAggregate;
import org.oasis_open.contextserver.persistence.spi.aggregate.DateRangeAggregate;
import org.oasis_open.contextserver.persistence.spi.aggregate.NumericRangeAggregate;
import org.oasis_open.contextserver.persistence.spi.aggregate.TermsAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by toto on 23/09/14.
 */
public class QueryServiceImpl implements QueryService {
    private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class.getName());

    private PersistenceService persistenceService;

    private DefinitionsService definitionsService;

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setDefinitionsService(DefinitionsService definitionsService) {
        this.definitionsService = definitionsService;
    }

    public void postConstruct() {
    }

    public void preDestroy() {
    }

    @Override
    public Map<String, Long> getAggregate(String type, String property) {
        return persistenceService.aggregateQuery(null, new TermsAggregate(property), type);
    }

    @Override
    public Map<String, Long> getAggregate(String type, String property, AggregateQuery query) {
        if(query != null) {
            // resolve condition
            if(query.getCondition() != null){
                ParserHelper.resolveConditionType(definitionsService, query.getCondition());
            }

            // resolve aggregate
            if(query.getAggregate() != null) {
                if (query.getAggregate().getType() != null){
                    // try to guess the aggregate type
                    if(query.getAggregate().getType().equals("date")){
                        String interval = (String) query.getAggregate().getParameters().get("interval");
                        String format = (String) query.getAggregate().getParameters().get("format");
                        return persistenceService.aggregateQuery(query.getCondition(), new DateAggregate(property, interval, format), type);
                    }else if (query.getAggregate().getType().equals("dateRange") && query.getAggregate().getGenericRanges() != null && query.getAggregate().getGenericRanges().size() > 0) {
                        String format = (String) query.getAggregate().getParameters().get("format");
                        return persistenceService.aggregateQuery(query.getCondition(), new DateRangeAggregate(query.getAggregate().getProperty(), format, query.getAggregate().getGenericRanges()), type);
                    } else if (query.getAggregate().getType().equals("range") && query.getAggregate().getNumericRanges() != null && query.getAggregate().getNumericRanges().size() > 0) {
                        return persistenceService.aggregateQuery(query.getCondition(), new NumericRangeAggregate(query.getAggregate().getProperty(), query.getAggregate().getNumericRanges()), type);
                    }
                }
            }

            // fall back on terms aggregate
            return persistenceService.aggregateQuery(query.getCondition(), new TermsAggregate(property), type);
        }

        return getAggregate(type, property);
    }

    @Override
    public long getQueryCount(String type, Condition condition) {
        try {
            if (condition.getConditionType() == null) {
                ParserHelper.resolveConditionType(definitionsService, condition);
            }
            return persistenceService.queryCount(condition, type);
        } catch (Exception e) {
            logger.warn("Invalid query");
            return 0;
        }
    }
}
