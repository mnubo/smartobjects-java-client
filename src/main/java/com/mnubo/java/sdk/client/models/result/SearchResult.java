package com.mnubo.java.sdk.client.models.result;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SearchResult {
    private final static Log log = LogFactory.getLog(SearchResult.class);

    private final List<Column> columns;
    private final List<List<Object>> rows;

    @JsonCreator
    public SearchResult(@JsonProperty("columns") List<Column> columns, @JsonProperty("rows") List<List<Object>> rows)
    {
        Preconditions.checkNotNull(columns, "columns must not be null");
        Preconditions.checkNotNull(rows, "rows must not be null");
        this.columns = columns;
        this.rows = rows;
    }

    public List<Column> getColumns() {
        return columns;
    }
    public List<List<Object>> getRows() {
        return rows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SearchResult that = (SearchResult) o;

        if (!columns.equals(that.columns))
            return false;
        return rows.equals(that.rows);

    }

    @Override
    public int hashCode() {
        int result = columns.hashCode();
        result = 31 * result + rows.hashCode();
        return result;
    }

    public static class Column {
        private final String label;
        private final String type;

        @JsonCreator
        public Column(@JsonProperty("label") String label, @JsonProperty("type") String type) {
            Preconditions.checkNotNull(label, "label must not be null");
            Preconditions.checkNotNull(type, "type must not be null");
            this.label = label;
            this.type = type;
        }

        public String getLabel() {
            return label;
        }
        public String getType() { return type; }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Column column = (Column) o;

            if (label != null ? !label.equals(column.label) : column.label != null)
                return false;
            return type != null ? type.equals(column.type) : column.type == null;

        }

        @Override
        public int hashCode() {
            int result = label != null ? label.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }
    }
}
