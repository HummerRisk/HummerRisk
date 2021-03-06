package com.hummerrisk.base.domain;

import java.util.ArrayList;
import java.util.List;

public class ImageGrypeTableExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public ImageGrypeTableExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andResultIdIsNull() {
            addCriterion("result_id is null");
            return (Criteria) this;
        }

        public Criteria andResultIdIsNotNull() {
            addCriterion("result_id is not null");
            return (Criteria) this;
        }

        public Criteria andResultIdEqualTo(String value) {
            addCriterion("result_id =", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdNotEqualTo(String value) {
            addCriterion("result_id <>", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdGreaterThan(String value) {
            addCriterion("result_id >", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdGreaterThanOrEqualTo(String value) {
            addCriterion("result_id >=", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdLessThan(String value) {
            addCriterion("result_id <", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdLessThanOrEqualTo(String value) {
            addCriterion("result_id <=", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdLike(String value) {
            addCriterion("result_id like", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdNotLike(String value) {
            addCriterion("result_id not like", value, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdIn(List<String> values) {
            addCriterion("result_id in", values, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdNotIn(List<String> values) {
            addCriterion("result_id not in", values, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdBetween(String value1, String value2) {
            addCriterion("result_id between", value1, value2, "resultId");
            return (Criteria) this;
        }

        public Criteria andResultIdNotBetween(String value1, String value2) {
            addCriterion("result_id not between", value1, value2, "resultId");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("`name` is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("`name` is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("`name` =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("`name` <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("`name` >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("`name` >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("`name` <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("`name` <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("`name` like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("`name` not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("`name` in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("`name` not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("`name` between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("`name` not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andInstalledIsNull() {
            addCriterion("installed is null");
            return (Criteria) this;
        }

        public Criteria andInstalledIsNotNull() {
            addCriterion("installed is not null");
            return (Criteria) this;
        }

        public Criteria andInstalledEqualTo(String value) {
            addCriterion("installed =", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledNotEqualTo(String value) {
            addCriterion("installed <>", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledGreaterThan(String value) {
            addCriterion("installed >", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledGreaterThanOrEqualTo(String value) {
            addCriterion("installed >=", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledLessThan(String value) {
            addCriterion("installed <", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledLessThanOrEqualTo(String value) {
            addCriterion("installed <=", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledLike(String value) {
            addCriterion("installed like", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledNotLike(String value) {
            addCriterion("installed not like", value, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledIn(List<String> values) {
            addCriterion("installed in", values, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledNotIn(List<String> values) {
            addCriterion("installed not in", values, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledBetween(String value1, String value2) {
            addCriterion("installed between", value1, value2, "installed");
            return (Criteria) this;
        }

        public Criteria andInstalledNotBetween(String value1, String value2) {
            addCriterion("installed not between", value1, value2, "installed");
            return (Criteria) this;
        }

        public Criteria andFixedInIsNull() {
            addCriterion("fixed_in is null");
            return (Criteria) this;
        }

        public Criteria andFixedInIsNotNull() {
            addCriterion("fixed_in is not null");
            return (Criteria) this;
        }

        public Criteria andFixedInEqualTo(String value) {
            addCriterion("fixed_in =", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInNotEqualTo(String value) {
            addCriterion("fixed_in <>", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInGreaterThan(String value) {
            addCriterion("fixed_in >", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInGreaterThanOrEqualTo(String value) {
            addCriterion("fixed_in >=", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInLessThan(String value) {
            addCriterion("fixed_in <", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInLessThanOrEqualTo(String value) {
            addCriterion("fixed_in <=", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInLike(String value) {
            addCriterion("fixed_in like", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInNotLike(String value) {
            addCriterion("fixed_in not like", value, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInIn(List<String> values) {
            addCriterion("fixed_in in", values, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInNotIn(List<String> values) {
            addCriterion("fixed_in not in", values, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInBetween(String value1, String value2) {
            addCriterion("fixed_in between", value1, value2, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andFixedInNotBetween(String value1, String value2) {
            addCriterion("fixed_in not between", value1, value2, "fixedIn");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("`type` is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("`type` is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("`type` =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("`type` <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("`type` >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("`type` >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("`type` <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("`type` <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("`type` like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("`type` not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("`type` in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("`type` not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("`type` between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("`type` not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityIsNull() {
            addCriterion("vulnerability is null");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityIsNotNull() {
            addCriterion("vulnerability is not null");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityEqualTo(String value) {
            addCriterion("vulnerability =", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityNotEqualTo(String value) {
            addCriterion("vulnerability <>", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityGreaterThan(String value) {
            addCriterion("vulnerability >", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityGreaterThanOrEqualTo(String value) {
            addCriterion("vulnerability >=", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityLessThan(String value) {
            addCriterion("vulnerability <", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityLessThanOrEqualTo(String value) {
            addCriterion("vulnerability <=", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityLike(String value) {
            addCriterion("vulnerability like", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityNotLike(String value) {
            addCriterion("vulnerability not like", value, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityIn(List<String> values) {
            addCriterion("vulnerability in", values, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityNotIn(List<String> values) {
            addCriterion("vulnerability not in", values, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityBetween(String value1, String value2) {
            addCriterion("vulnerability between", value1, value2, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andVulnerabilityNotBetween(String value1, String value2) {
            addCriterion("vulnerability not between", value1, value2, "vulnerability");
            return (Criteria) this;
        }

        public Criteria andSeverityIsNull() {
            addCriterion("severity is null");
            return (Criteria) this;
        }

        public Criteria andSeverityIsNotNull() {
            addCriterion("severity is not null");
            return (Criteria) this;
        }

        public Criteria andSeverityEqualTo(String value) {
            addCriterion("severity =", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityNotEqualTo(String value) {
            addCriterion("severity <>", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityGreaterThan(String value) {
            addCriterion("severity >", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityGreaterThanOrEqualTo(String value) {
            addCriterion("severity >=", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityLessThan(String value) {
            addCriterion("severity <", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityLessThanOrEqualTo(String value) {
            addCriterion("severity <=", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityLike(String value) {
            addCriterion("severity like", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityNotLike(String value) {
            addCriterion("severity not like", value, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityIn(List<String> values) {
            addCriterion("severity in", values, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityNotIn(List<String> values) {
            addCriterion("severity not in", values, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityBetween(String value1, String value2) {
            addCriterion("severity between", value1, value2, "severity");
            return (Criteria) this;
        }

        public Criteria andSeverityNotBetween(String value1, String value2) {
            addCriterion("severity not between", value1, value2, "severity");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table image_grype_table
     *
     * @mbg.generated do_not_delete_during_merge Sun Jul 24 02:46:37 CST 2022
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table image_grype_table
     *
     * @mbg.generated Sun Jul 24 02:46:37 CST 2022
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}