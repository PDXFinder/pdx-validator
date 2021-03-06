package org.pdxfinder.validator.tablevalidation.error;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Test;
import org.pdxfinder.validator.tablevalidation.ColumnReference;
import org.pdxfinder.validator.tablevalidation.Relation;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class BrokenRelationReportErrorTest {

  private BrokenRelationErrorCreator brokenTableRelationErrorCreator =
      new BrokenRelationErrorCreator();

  @Test
  public void toString_givenBrokenRelationMissingRightColumn_returnsAppropriateMessage() {
    int count = 1;
    String expected =
        "Error in [bar.tsv] for provider [TEST]: Broken TABLE_KEY relation [(foo.tsv) foo_id -> foo_id (bar.tsv)]"
            + ": because [bar.tsv] is missing column [foo_id]:\n"
            + " not_foo_id  |\n"
            + "--------------";
    Relation relation =
        Relation.betweenTableKeys(
            ColumnReference.of("foo.tsv", "foo_id"), ColumnReference.of("bar.tsv", "foo_id"));
    Table tableMissingColumn = Table.create().addColumns(StringColumn.create("not_foo_id"));

    BrokenRelationError error =
        brokenTableRelationErrorCreator.create(
            "bar.tsv",
            relation,
            tableMissingColumn,
            "because [bar.tsv] is missing column [foo_id]",
            "TEST");

    assertEquals(expected, error.verboseMessage());
  }

  @Test
  public void verboseMessage_givenBrokenRelationOrphanIdsInRightColumn_returnsAppropriateMessage() {
    String expected =
        "Error in [bar.tsv] for provider [PROVIDER-BC]: "
            + "Broken TABLE_KEY relation [(foo.tsv) foo_id -> foo_id (bar.tsv)]: "
            + "2 orphan row(s) found in [bar.tsv]:\n"
            + " foo_id  |\n"
            + "----------\n"
            + "      1  |\n"
            + "      1  |";
    Relation relation =
        Relation.betweenTableKeys(
            ColumnReference.of("foo.tsv", "foo_id"), ColumnReference.of("bar.tsv", "foo_id"));
    Table tableMissingValues =
        Table.create().addColumns(StringColumn.create("foo_id", Arrays.asList("1", "1")));
    BrokenRelationError error =
        brokenTableRelationErrorCreator.create(
            "bar.tsv",
            relation,
            tableMissingValues,
            "2 orphan row(s) found in [bar.tsv]",
            "PROVIDER-BC");

    assertEquals(expected, error.verboseMessage());
  }

  @Test
  public void message_givenError_returnsAppropriateMessage() {
    String expected =
        "Error in [bar.tsv] for provider [PROVIDER-BC]: "
            + "Broken TABLE_KEY relation [(foo.tsv) foo_id -> foo_id (bar.tsv)]: "
            + "2 orphan row(s) found in [bar.tsv]";
    Relation relation =
        Relation.betweenTableKeys(
            ColumnReference.of("foo.tsv", "foo_id"), ColumnReference.of("bar.tsv", "foo_id"));
    Table tableMissingValues =
        Table.create().addColumns(StringColumn.create("foo_id", Arrays.asList("1", "1")));
    BrokenRelationError error =
        brokenTableRelationErrorCreator.create(
            "bar.tsv",
            relation,
            tableMissingValues,
            "2 orphan row(s) found in [bar.tsv]",
            "PROVIDER-BC");
    assertEquals(expected, error.message());
  }
}
