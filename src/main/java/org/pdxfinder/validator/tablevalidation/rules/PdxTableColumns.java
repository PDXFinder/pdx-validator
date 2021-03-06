package org.pdxfinder.validator.tablevalidation.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.pdxfinder.validator.tablevalidation.ColumnReference;

public class PdxTableColumns {

  private PdxTableColumns() {
  }

  public static Set<ColumnReference> getMetadataColumns() {
    Set<ColumnReference> tableColumns = new HashSet<>();
    Arrays.asList(
        "patient_id",
        "sex",
        "history",
        "ethnicity",
        "ethnicity_assessment_method",
        "initial_diagnosis",
        "age_at_initial_diagnosis")
        .forEach(s -> tableColumns.add(ColumnReference.of("patient", s)));
    Arrays.asList(
        "patient_id",
        "sample_id",
        "collection_date",
        "collection_event",
        "months_since_collection_1",
        "age_in_years_at_collection",
        "diagnosis",
        "tumour_type",
        "primary_site",
        "collection_site",
        "stage",
        "staging_system",
        "grade",
        "grading_system",
        "virology_status",
        "sharable",
        "treatment_naive_at_collection",
        "treated",
        "prior_treatment",
        "model_id")
        .forEach(s -> tableColumns.add(ColumnReference.of("sample", s)));
    Arrays.asList(
        "model_id",
        "host_strain",
        "host_strain_full",
        "engraftment_site",
        "engraftment_type",
        "sample_type",
        "sample_state",
        "passage_number",
        "publications")
        .forEach(s -> tableColumns.add(ColumnReference.of("model", s)));
    Arrays.asList(
        "validation_technique", "description", "passages_tested", "validation_host_strain_full")
        .forEach(s -> tableColumns.add(ColumnReference.of("model_validation", s)));
    Arrays.asList(
        "model_id",
        "provider_type",
        "accessibility",
        "europdx_access_modality",
        "email",
        "name",
        "form_url",
        "database_url",
        "provider_name",
        "provider_abbreviation",
        "project")
        .forEach(s -> tableColumns.add(ColumnReference.of("sharing", s)));
    return tableColumns;
  }

  public static Set<ColumnReference> getLoaderParameterColumns() {
    Set<ColumnReference> tableColumns = new HashSet<>();
    Arrays.asList("name", "abbreviation", "internal_url", "internal_dosing_url")
        .forEach(s -> tableColumns.add(ColumnReference.of("load-parameters", s)));
    return tableColumns;
  }
}
