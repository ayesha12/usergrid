package org.apache.usergrid.java.client.query;

import org.apache.usergrid.java.client.UsergridEnums.UsergridQueryOperator;
import org.apache.usergrid.java.client.UsergridEnums.UsergridQuerySortOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Robert Walsh on 2/9/16.
 */
@SuppressWarnings("unused")
public final class UsergridQuery {
    private static final int LIMIT_DEFAULT = 10;
    private static final String AMPERSAND = "&";
    private static final String AND = "and";
    private static final String APOSTROPHE = "'";
    private static final String COMMA = ",";
    private static final String CONTAINS = "contains";
    private static final String CURSOR = "cursor";
    private static final String EMPTY_STRING = "";
    private static final String EQUALS = "=";
    private static final String LIMIT = "limit";
    private static final String LOCATION = "location";
    private static final String NOT = "not";
    private static final String OF = "of";
    private static final String OR = "or";
    private static final String ORDER_BY = "order by";
    private static final String QL = "ql";
    private static final String QUESTION_MARK = "?";
    private static final String SELECT_ALL = "select *";
    private static final String SPACE = " ";
    private static final String UTF8 = "UTF-8";
    private static final String WHERE = "where";
    private static final String WITHIN = "within";
    private final ArrayList<String> requirementStrings = new ArrayList<>();
    private final ArrayList<String> urlTerms = new ArrayList<>();
    private final HashMap<String, UsergridQuerySortOrder> orderClauses = new HashMap<>();
    private Integer limit = UsergridQuery.LIMIT_DEFAULT;
    private String cursor = null;
    private String fromStringValue = null;
    private String collectionName = null;

    public UsergridQuery() {
        this(null);
    }

    public UsergridQuery(@Nullable final String collectionName) {
        this.collectionName = collectionName;
        this.requirementStrings.add(UsergridQuery.EMPTY_STRING);
    }

    private static boolean isUUID(@Nonnull final String string) {
        try {
            UUID uuid = UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Nonnull
    private static String encode(@Nonnull final String stringValue) {
        String escapedString;
        try {
            escapedString = URLEncoder.encode(stringValue, UTF8);
        } catch (Exception e) {
            escapedString = stringValue;
        }
        return escapedString;
    }

    @Nonnull
    public static String strJoin(@Nonnull final List<String> array, @Nonnull final String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, il = array.size(); i < il; i++) {
            if (i > 0) {
                stringBuilder.append(separator);
            }
            stringBuilder.append(array.get(i));
        }
        return stringBuilder.toString();
    }

    @Nonnull
    public UsergridQuery fromString(@Nonnull final String stringValue) {
        this.fromStringValue = stringValue;
        return this;
    }

    @Nullable
    public String getType() {
        return this.collectionName;
    }

    @Nullable
    public String getCollectionName() {
        return this.collectionName;
    }

    @Nullable
    public String getCollection() {
        return this.collectionName;
    }

    @Nonnull
    public UsergridQuery type(@Nullable final String type) {
        this.collectionName = type;
        return this;
    }

    @Nonnull
    public UsergridQuery collection(@Nullable final String collectionName) {
        return this.type(collectionName);
    }

    @Nonnull
    public UsergridQuery collectionName(@Nullable final String collectionName) {
        return this.type(collectionName);
    }

    @Nonnull
    public UsergridQuery cursor(@Nullable final String value) {
        this.cursor = value;
        return this;
    }

    @Nonnull
    public UsergridQuery limit(@Nonnull final Integer limit) {
        this.limit = limit;
        return this;
    }

    @Nonnull
    private UsergridQuery addConditionalSeparator(@Nonnull final String separator) {
        if (!this.requirementStrings.get(0).isEmpty()) {
            this.requirementStrings.add(0, separator);
            this.requirementStrings.add(0, UsergridQuery.EMPTY_STRING);
        }
        return this;
    }

    @Nonnull
    public UsergridQuery and() {
        return this.addConditionalSeparator(UsergridQuery.AND);
    }

    @Nonnull
    public UsergridQuery or() {
        return this.addConditionalSeparator(UsergridQuery.OR);
    }

    @Nonnull
    public UsergridQuery not() {
        return this.addConditionalSeparator(UsergridQuery.NOT);
    }

    @Nonnull
    public UsergridQuery sort(@Nonnull final String term, @Nonnull final UsergridQuerySortOrder sortOrder) {
        this.orderClauses.put(term, sortOrder);
        return this;
    }

    @Nonnull
    public UsergridQuery ascending(@Nonnull final String term) {
        return this.asc(term);
    }

    @Nonnull
    public UsergridQuery asc(@Nonnull final String term) {
        return this.sort(term, UsergridQuerySortOrder.ASC);
    }

    @Nonnull
    public UsergridQuery descending(@Nonnull final String term) {
        return this.desc(term);
    }

    @Nonnull
    public UsergridQuery desc(@Nonnull final String term) {
        return this.sort(term, UsergridQuerySortOrder.DESC);
    }

    @Nonnull
    public UsergridQuery contains(@Nonnull final String term, @Nonnull final String value) {
        return this.containsWord(term, value);
    }

    @Nonnull
    public UsergridQuery containsString(@Nonnull final String term, @Nonnull final String value) {
        return this.containsWord(term, value);
    }

    @Nonnull
    public UsergridQuery containsWord(@Nonnull final String term, @Nonnull final String value) {
        return this.addRequirement(term + SPACE + CONTAINS + SPACE + ((UsergridQuery.isUUID(value)) ? EMPTY_STRING : APOSTROPHE) + value + ((UsergridQuery.isUUID(value)) ? EMPTY_STRING : APOSTROPHE));
    }

    @Nonnull
    public UsergridQuery filter(@Nonnull final String term, @Nonnull final Object value) {
        return this.eq(term, value);
    }

    @Nonnull
    public UsergridQuery equals(@Nonnull final String term, @Nonnull final Object value) {
        return this.eq(term, value);
    }

    @Nonnull
    public UsergridQuery eq(@Nonnull final String term, @Nonnull final Object value) {
        return this.addOperationRequirement(term, UsergridQueryOperator.EQUAL, value);
    }

    @Nonnull
    public UsergridQuery greaterThan(@Nonnull final String term, @Nonnull final Object value) {
        return this.gt(term, value);
    }

    @Nonnull
    public UsergridQuery gt(@Nonnull final String term, @Nonnull final Object value) {
        return this.addOperationRequirement(term, UsergridQueryOperator.GREATER_THAN, value);
    }

    @Nonnull
    public UsergridQuery greaterThanOrEqual(@Nonnull final String term, @Nonnull final Object value) {
        return this.gte(term, value);
    }

    @Nonnull
    public UsergridQuery gte(@Nonnull final String term, @Nonnull final Object value) {
        return this.addOperationRequirement(term, UsergridQueryOperator.GREATER_THAN_EQUAL_TO, value);
    }

    @Nonnull
    public UsergridQuery lessThan(@Nonnull final String term, @Nonnull final Object value) {
        return this.lt(term, value);
    }

    @Nonnull
    public UsergridQuery lt(@Nonnull final String term, @Nonnull final Object value) {
        return this.addOperationRequirement(term, UsergridQueryOperator.LESS_THAN, value);
    }

    @Nonnull
    public UsergridQuery lessThanOrEqual(@Nonnull final String term, @Nonnull final Object value) {
        return this.lte(term, value);
    }

    @Nonnull
    public UsergridQuery lte(@Nonnull final String term, @Nonnull final Object value) {
        return this.addOperationRequirement(term, UsergridQueryOperator.LESS_THAN_EQUAL_TO, value);
    }

    @Nonnull
    public UsergridQuery locationWithin(final double distance, final double latitude, final double longitude) {
        return this.addRequirement(LOCATION + SPACE + WITHIN + SPACE + distance + SPACE + OF + SPACE + latitude + SPACE + COMMA + longitude);
    }

    @Nonnull
    public UsergridQuery urlTerm(@Nonnull final String term, @Nonnull final String equalsValue) {
        if (term.equalsIgnoreCase(QL)) {
            this.ql(equalsValue);
        } else {
            this.urlTerms.add(UsergridQuery.encode(term) + EQUALS + UsergridQuery.encode(equalsValue));
        }
        return this;
    }

    @Nonnull
    public UsergridQuery ql(@Nonnull final String value) {
        return this.addRequirement(value);
    }

    @Nonnull
    public UsergridQuery addRequirement(@Nonnull final String requirement) {
        String requirementString = this.requirementStrings.remove(0);
        if (!requirement.isEmpty() && !requirementString.isEmpty()) {
            requirementString += SPACE + AND + SPACE;
        }
        requirementString += requirement;
        this.requirementStrings.add(0, requirementString);
        return this;
    }

    @Nonnull
    public UsergridQuery addOperationRequirement(@Nonnull final String term, @Nonnull final UsergridQueryOperator operation, final int intValue) {
        return this.addOperationRequirement(term, operation, Integer.valueOf(intValue));
    }

    @Nonnull
    public UsergridQuery addOperationRequirement(@Nonnull final String term, @Nonnull final UsergridQueryOperator operation, @Nonnull final Object value) {
        if (value instanceof String) {
            String valueString = value.toString();
            if (!UsergridQuery.isUUID(valueString)) {
                valueString = APOSTROPHE + value + APOSTROPHE;
            }
            return addRequirement(term + SPACE + operation.operatorValue() + SPACE + valueString);
        } else {
            return addRequirement(term + SPACE + operation.operatorValue() + SPACE + value.toString());
        }
    }

    @Nonnull
    private String constructOrderByString() {
        String orderByString = EMPTY_STRING;
        if (!this.orderClauses.isEmpty()) {
            for (Map.Entry<String, UsergridQuerySortOrder> orderClause : this.orderClauses.entrySet()) {
                if (!orderByString.isEmpty()) {
                    orderByString += COMMA;
                }
                orderByString += orderClause.getKey() + SPACE + orderClause.getValue().toString();
            }
            if (!orderByString.isEmpty()) {
                orderByString = SPACE + ORDER_BY + SPACE + orderByString;
            }
        }
        return orderByString;
    }

    @Nonnull
    private String constructURLTermsString() {
        String urlTermsString = EMPTY_STRING;
        if (!this.urlTerms.isEmpty()) {
            urlTermsString = UsergridQuery.strJoin(this.urlTerms, AMPERSAND);
        }
        return urlTermsString;
    }

    @Nonnull
    private String constructRequirementString() {
        ArrayList<String> requirementStrings = new ArrayList<>(this.requirementStrings);
        String firstString = requirementStrings.get(0);
        if (firstString.isEmpty()) {
            requirementStrings.remove(0);
        }
        String requirementsString = EMPTY_STRING;
        if (!requirementStrings.isEmpty()) {
            firstString = requirementStrings.get(0);
            if (firstString.equalsIgnoreCase(OR) || firstString.equalsIgnoreCase(AND) || firstString.equalsIgnoreCase(NOT)) {
                requirementStrings.remove(0);
            }
            if (!requirementStrings.isEmpty()) {
                Collections.reverse(requirementStrings);
                requirementsString = UsergridQuery.strJoin(requirementStrings, SPACE);
            }
        }
        return requirementsString;
    }

    @Nonnull
    private String constructURLAppend() {
        return this.constructURLAppend(true);
    }

    @Nonnull
    private String constructURLAppend(final boolean autoURLEncode) {
        if (this.fromStringValue != null) {
            String requirementsString = this.fromStringValue;
            if (autoURLEncode) {
                requirementsString = UsergridQuery.encode(requirementsString);
            }
            return QUESTION_MARK + QL + EQUALS + requirementsString;
        }
        String urlAppend = EMPTY_STRING;
        if (this.limit != LIMIT_DEFAULT) {
            urlAppend += LIMIT + EQUALS + this.limit.toString();
        }
        String urlTermsString = this.constructURLTermsString();
        if (!urlTermsString.isEmpty()) {
            if (!urlAppend.isEmpty()) {
                urlAppend += AMPERSAND;
            }
            urlAppend += urlTermsString;
        }
        if (this.cursor != null && !this.cursor.isEmpty()) {
            if (!urlAppend.isEmpty()) {
                urlAppend += AMPERSAND;
            }
            urlAppend += CURSOR + EQUALS + this.cursor;
        }

        String requirementsString = this.constructRequirementString();
        if (!requirementsString.isEmpty()) {
            requirementsString = SELECT_ALL + SPACE + WHERE + SPACE + requirementsString;
        } else {
            requirementsString = SELECT_ALL + SPACE;
        }

        String orderByString = this.constructOrderByString();
        if (!orderByString.isEmpty()) {
            requirementsString += orderByString;
        }
        if (!requirementsString.isEmpty()) {
            if (autoURLEncode) {
                requirementsString = UsergridQuery.encode(requirementsString);
            }
            if (!urlAppend.isEmpty()) {
                urlAppend += AMPERSAND;
            }
            urlAppend += QL + EQUALS + requirementsString;
        }
        if (!urlAppend.isEmpty()) {
            urlAppend = QUESTION_MARK + urlAppend;
        }
        return urlAppend;
    }

    @Nonnull
    public String build() {
        return this.build(true);
    }

    @Nonnull
    public String build(final boolean autoURLEncode) {
        return this.constructURLAppend(autoURLEncode);
    }
}
