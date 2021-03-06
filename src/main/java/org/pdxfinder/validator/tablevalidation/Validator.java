package org.pdxfinder.validator.tablevalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.pdxfinder.validator.tablevalidation.dto.ValidationError;
import org.pdxfinder.validator.tablevalidation.error.BrokenRelationErrorCreator;
import org.pdxfinder.validator.tablevalidation.error.DuplicateValueErrorCreator;
import org.pdxfinder.validator.tablevalidation.error.EmptyValueErrorCreator;
import org.pdxfinder.validator.tablevalidation.error.IllegalValueErrorCreator;
import org.pdxfinder.validator.tablevalidation.error.MissingColumnErrorCreator;
import org.pdxfinder.validator.tablevalidation.error.MissingTableErrorCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

@Component
public class Validator {

  private static final Logger log = LoggerFactory.getLogger(Validator.class);
  private List<ValidationError> validationErrors;
  private MissingTableErrorCreator missingTableErrorCreator;

  public Validator(MissingTableErrorCreator missingTableErrorCreator) {
    this.missingTableErrorCreator = missingTableErrorCreator;
    this.validationErrors = new ArrayList<>();
  }

  public Validator() {
    resetErrors();
  }

  public List<ValidationError> validate(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    checkRequiredTablesPresent(tableSet, tableSetSpecification);
    if (!thereAreErrors(validationErrors, tableSetSpecification)) {
      checkRequiredColumnsPresent(tableSet, tableSetSpecification);
      if (!thereAreErrors(validationErrors, tableSetSpecification)) {
        performColumnValidation(tableSet, tableSetSpecification);
      }
    }
    return validationErrors;
  }

  private void performColumnValidation(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    checkAllNonEmptyValuesPresent(tableSet, tableSetSpecification);
    checkForIllegalValues(tableSet, tableSetSpecification);
    checkAllUniqueColumnsForDuplicates(tableSet, tableSetSpecification);
    checkRelationsValid(tableSet, tableSetSpecification);
  }

  private boolean thereAreErrors(
      List<ValidationError> validationErrors, TableSetSpecification tableSetSpecification) {
    if (CollectionUtils.isNotEmpty(validationErrors)) {
      log.error(
          "Not all required tables where present for {}. Aborting further validation",
          tableSetSpecification.getProvider());
    }
    return CollectionUtils.isNotEmpty(validationErrors);
  }

  private void checkRequiredTablesPresent(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    if (tableSetSpecification.hasRequiredColumns())
      validationErrors.addAll(
          missingTableErrorCreator.generateErrors(tableSet, tableSetSpecification));
  }

  private void checkRequiredColumnsPresent(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    if (tableSetSpecification.hasRequiredColumns())
      validationErrors.addAll(
          new MissingColumnErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkAllNonEmptyValuesPresent(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new EmptyValueErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkAllUniqueColumnsForDuplicates(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new DuplicateValueErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkRelationsValid(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new BrokenRelationErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  private void checkForIllegalValues(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    validationErrors.addAll(
        new IllegalValueErrorCreator().generateErrors(tableSet, tableSetSpecification));
  }

  boolean passesValidation(
      Map<String, Table> tableSet, TableSetSpecification tableSetSpecification) {
    return validate(tableSet, tableSetSpecification).isEmpty();
  }

  List<ValidationError> getValidationErrors() {
    return this.validationErrors;
  }

  public static void reportAnyErrors(List<ValidationError> tableReports) {
    if (CollectionUtils.isNotEmpty(tableReports)) {
      for (ValidationError error : tableReports) {
        String message = error.getTableReport().getColumnReport().getMessage();
        log.error(message);
      }
    } else {
      log.info("There were no validation errors raised, great!");
    }
  }

  public void resetErrors() {
    this.missingTableErrorCreator = new MissingTableErrorCreator();
    this.validationErrors = new ArrayList<>();
  }
}
