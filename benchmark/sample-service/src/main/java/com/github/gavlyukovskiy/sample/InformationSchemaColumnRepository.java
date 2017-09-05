package com.github.gavlyukovskiy.sample;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationSchemaColumnRepository extends CrudRepository<InformationSchemaColumn, InformationSchemaColumn.PK> {
}
