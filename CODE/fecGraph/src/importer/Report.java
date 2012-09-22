package org.followthedata.importer;

/**
 * @author mh
 * @since 21.08.12
 */
public interface Report {
    void reset();

    void finish();

    void dots();

    void finishImport(String type);
}