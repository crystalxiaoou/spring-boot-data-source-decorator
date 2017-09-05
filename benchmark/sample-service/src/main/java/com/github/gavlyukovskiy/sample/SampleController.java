package com.github.gavlyukovskiy.sample;

import com.github.gavlyukovskiy.sample.InformationSchemaColumn.PK;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping
public class SampleController {

    private static final Logger log = getLogger(SampleController.class);

    private final DataSource dataSource;
    private final InformationSchemaColumnRepository informationSchemaColumnRepository;

    public SampleController(DataSource dataSource, InformationSchemaColumnRepository informationSchemaColumnRepository) {
        this.dataSource = dataSource;
        this.informationSchemaColumnRepository = informationSchemaColumnRepository;
    }

    @RequestMapping("/noop")
    public Iterable<InformationSchemaColumn> noop() {
        return Collections.emptyList();
    }

    @RequestMapping("/jdbc")
    public Iterable<InformationSchemaColumn> jdbc() {
        log.info("jdbc() - start");
        List<InformationSchemaColumn> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS")) {
            while (resultSet.next()) {
                PK pk = new PK();
                pk.setTableName(resultSet.getString("TABLE_NAME"));
                pk.setColumnName(resultSet.getString("COLUMN_NAME"));
                InformationSchemaColumn informationSchemaColumn = new InformationSchemaColumn();
                informationSchemaColumn.setPk(pk);
                informationSchemaColumn.setTableCatalog(resultSet.getString("TABLE_CATALOG"));
                informationSchemaColumn.setTableSchema(resultSet.getString("TABLE_SCHEMA"));
                informationSchemaColumn.setOrdinalPosition(resultSet.getString("ORDINAL_POSITION"));
                informationSchemaColumn.setColumnDefault(resultSet.getString("COLUMN_DEFAULT"));
                informationSchemaColumn.setIsNullable(resultSet.getString("IS_NULLABLE"));
                informationSchemaColumn.setDataType(resultSet.getString("DATA_TYPE"));
                informationSchemaColumn.setCharacterMaximumLength(resultSet.getString("CHARACTER_MAXIMUM_LENGTH"));
                informationSchemaColumn.setCharacterOctetLength(resultSet.getString("CHARACTER_OCTET_LENGTH"));
                informationSchemaColumn.setNumericPrecision(resultSet.getString("NUMERIC_PRECISION"));
                informationSchemaColumn.setNumericPrecisionRadix(resultSet.getString("NUMERIC_PRECISION_RADIX"));
                informationSchemaColumn.setNumericScale(resultSet.getString("NUMERIC_SCALE"));
                informationSchemaColumn.setCharacterSetName(resultSet.getString("CHARACTER_SET_NAME"));
                informationSchemaColumn.setCollationName(resultSet.getString("COLLATION_NAME"));
                informationSchemaColumn.setTypeName(resultSet.getString("TYPE_NAME"));
                informationSchemaColumn.setNullable(resultSet.getString("NULLABLE"));
                informationSchemaColumn.setIsComputed(resultSet.getString("IS_COMPUTED"));
                informationSchemaColumn.setSelectivity(resultSet.getString("SELECTIVITY"));
                informationSchemaColumn.setCheckConstraint(resultSet.getString("CHECK_CONSTRAINT"));
                informationSchemaColumn.setSequenceName(resultSet.getString("SEQUENCE_NAME"));
                informationSchemaColumn.setRemarks(resultSet.getString("REMARKS"));
                informationSchemaColumn.setSourceDataType(resultSet.getString("SOURCE_DATA_TYPE"));
                results.add(informationSchemaColumn);
            }
            connection.commit();
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        log.info("jdbc() - end");
        return results;
    }

    @RequestMapping("/hibernate")
    public Iterable<InformationSchemaColumn> hibernate() {
        log.info("hibernate() - start");
        Iterable<InformationSchemaColumn> results = informationSchemaColumnRepository.findAll();
        log.info("hibernate() - end");
        return results;
    }
}
