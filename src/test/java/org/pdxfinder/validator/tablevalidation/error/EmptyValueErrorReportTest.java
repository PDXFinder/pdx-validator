package org.pdxfinder.validator.tablevalidation.error;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.ColumnReference;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class EmptyValueErrorReportTest {

  private EmptyValueErrorCreator emptyValueErrorCreator = new EmptyValueErrorCreator();
  private String PROVIDER = "provider";

  @Test
  public void verboseMessage_givenMissingValue_returnsAppropriateError() {
    String expected =
        "Error in [table] for provider [provider]: Missing value(s) in required column [column] row numbers [0]:\n"
            + "  invalid  \n"
            + " column1  |\n"
            + "-----------\n"
            + "          |";
    ColumnReference columnReference = ColumnReference.of("table", "column");
    Table invalidRows = Table.create("invalid", StringColumn.create("column1", ""));
    EmptyValueError error = emptyValueErrorCreator
        .create(columnReference, invalidRows, PROVIDER, "0");

    assertEquals(expected, error.verboseMessage());
  }

  @Test
  public void message_givenMissingValue_returnsAppropriateError() {
    String expected =
        "Error in [table] for provider [provider]: Missing value(s) in required column [column] row numbers [0]";
    ColumnReference columnReference = ColumnReference.of("table", "column");
    Table invalidRows = Table.create("invalid", StringColumn.create("column1", ""));

    EmptyValueError error = emptyValueErrorCreator
        .create(columnReference, invalidRows, PROVIDER, "0");

    assertEquals(expected, error.message());
  }
}
