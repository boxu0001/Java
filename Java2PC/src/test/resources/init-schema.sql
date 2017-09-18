DROP SCHEMA IF EXISTS SAMPLE_SCHEMA CASCADE;

CREATE SCHEMA SAMPLE_SCHEMA AUTHORIZATION sa;

SET DATABASE SQL SYNTAX ORA TRUE;

    drop table SAMPLE_SCHEMA.TESTA if exists;
    create table SAMPLE_SCHEMA.TESTA (
        ID varchar(10) not null,
        DESCRIPTION varchar(30),
        primary key (ID)
    );