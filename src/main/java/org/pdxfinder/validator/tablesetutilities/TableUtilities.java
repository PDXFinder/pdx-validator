package org.pdxfinder.validator.tablesetutilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public final class TableUtilities {

  private static final Logger log = LoggerFactory.getLogger(TableUtilities.class);

  private TableUtilities() {
    throw new IllegalStateException("Utility class");
  }

  public static Table removeHeaderRows(Table table, int numberOfRows) {
    return doesNotHaveEnoughRows(table, numberOfRows)
        ? table.emptyCopy()
        : table.dropRange(numberOfRows);
  }

  public static Table cleanTableValues(
      Table table, String talbeName, List<String> columnExceptions) {
    Table lowerCasedTable = lowerCaseSelectColumnValues(table, columnExceptions);
    Table cleanedTable = trimColumnValues(lowerCasedTable);
    cleanedTable.setName(talbeName);
    return cleanedTable;
  }

  private static Table trimColumnValues(Table table) {
    List<String> columnNames = table.columnNames();
    Table transformedTable =
        Table.create(
            table.columns().stream()
                .map(x -> x.asStringColumn().trim())
                .collect(Collectors.toList()));
    return renameColumnsInTable(transformedTable, columnNames);
  }

  public static Table lowerCaseSelectColumnValues(Table table, List<String> columnExceptions) {
    List<String> columnNames = table.columnNames();
    List<Column<?>> transformedColumns = new ArrayList<>();
    for (Column<?> column : table.columns()) {
      if (!columnExceptions.contains(column.name())) {
        Column<String> lowerCasedColumn = column.asStringColumn().lowerCase();
        transformedColumns.add(lowerCasedColumn);
      } else {
        transformedColumns.add(column);
      }
    }
    return renameColumnsInTable(Table.create(transformedColumns), columnNames);
  }

  private static Table renameColumnsInTable(Table table, List<String> newNames) {
    for (int i = 0; i < newNames.size(); i++) {
      table.column(i).setName(newNames.get(i));
    }
    return table;
  }

  private static boolean doesNotHaveEnoughRows(Table table, int numberOfRows) {
    return table.rowCount() <= numberOfRows;
  }

  public static Table fromString(String tableName, String... lines) {
    Table table = Table.create();
    String string = String.join("\n", lines);
    try {
      table = Table.read().csv(IOUtils.toInputStream(string));
      table.setName(tableName);
    } catch (Exception e) {
      log.error("There was an error parsing string to Table", e);
    }
    return table;
  }
}
