package com.github.gavlyukovskiy.sample;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;

@Data
@Entity
@Immutable
@Table(catalog = "INFORMATION_SCHEMA", name = "COLUMNS")
public class InformationSchemaColumn implements Serializable {

    @EmbeddedId
    private PK pk;

    @Column(name = "TABLE_CATALOG")
    private String tableCatalog;
    @Column(name = "TABLE_SCHEMA")
    private String tableSchema;
    @Column(name = "ORDINAL_POSITION")
    private String ordinalPosition;
    @Column(name = "COLUMN_DEFAULT")
    private String columnDefault;
    @Column(name = "IS_NULLABLE")
    private String isNullable;
    @Column(name = "DATA_TYPE")
    private String dataType;
    @Column(name = "CHARACTER_MAXIMUM_LENGTH")
    private String characterMaximumLength;
    @Column(name = "CHARACTER_OCTET_LENGTH")
    private String characterOctetLength;
    @Column(name = "NUMERIC_PRECISION")
    private String numericPrecision;
    @Column(name = "NUMERIC_PRECISION_RADIX")
    private String numericPrecisionRadix;
    @Column(name = "NUMERIC_SCALE")
    private String numericScale;
    @Column(name = "CHARACTER_SET_NAME")
    private String characterSetName;
    @Column(name = "COLLATION_NAME")
    private String collationName;
    @Column(name = "TYPE_NAME")
    private String typeName;
    @Column(name = "NULLABLE")
    private String nullable;
    @Column(name = "IS_COMPUTED")
    private String isComputed;
    @Column(name = "SELECTIVITY")
    private String selectivity;
    @Column(name = "CHECK_CONSTRAINT")
    private String checkConstraint;
    @Column(name = "SEQUENCE_NAME")
    private String sequenceName;
    @Column(name = "REMARKS")
    private String remarks;
    @Column(name = "SOURCE_DATA_TYPE")
    private String sourceDataType;

    @Embeddable
    @Data
    public static class PK implements Serializable {
        @Column(name = "TABLE_NAME")
        private String tableName;
        @Column(name = "COLUMN_NAME")
        private String columnName;
    }
}
